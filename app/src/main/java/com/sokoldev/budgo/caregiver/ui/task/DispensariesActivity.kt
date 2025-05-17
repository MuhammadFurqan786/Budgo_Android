package com.sokoldev.budgo.caregiver.ui.task

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import com.sokoldev.budgo.R
import com.sokoldev.budgo.caregiver.viewmodels.JobViewModel
import com.sokoldev.budgo.common.data.models.response.Dispensory
import com.sokoldev.budgo.common.data.remote.network.ApiResponse
import com.sokoldev.budgo.common.utils.GPSTracker
import com.sokoldev.budgo.common.utils.Global
import com.sokoldev.budgo.common.utils.prefs.PreferenceHelper
import com.sokoldev.budgo.common.utils.prefs.PreferenceKeys
import com.sokoldev.budgo.databinding.ActivityDispensariesBinding
import com.sokoldev.budgo.patient.dialog.BottomSheetDialog

class DispensariesActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityDispensariesBinding
    private val viewModel: JobViewModel by viewModels()
    private lateinit var helper: PreferenceHelper
    private var latitude: String = ""
    private var longitude: String = ""
    private var bookingId : String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDispensariesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        helper = PreferenceHelper.getPref(this)
        bookingId = intent.getStringExtra(PreferenceKeys.BOOKING_ID) ?: ""
        getLatLon()
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        binding.back.setOnClickListener { finish() }
    }


    override fun onMapReady(googleMap: GoogleMap) {
        Log.d("MapReady", "Map is ready")
        mMap = googleMap
        if (latitude.isNotEmpty() && longitude.isNotEmpty()) {
            helper.getStringValue(PreferenceKeys.PREF_USER_TOKEN)?.let {
                if (latitude.isNotEmpty() && longitude.isNotEmpty()) {
                    viewModel.getDispensories(
                        it,
                        latitude,
                        longitude
                    )
                }
            }
        }

        viewModel.dispensoryResponse.observe(this, Observer {
            when (it) {
                is ApiResponse.Success -> {

                    if (it.data.data.isNotEmpty()) {
                        showDispensories(it.data.data, mMap)
                        Log.d(
                            "Dispensariiiiiiiiiiii",
                            "Showing ${it.data.data.size} dispensaries on the map"
                        )
                    }
                    Global.showMessage(
                        binding.root.rootView,
                        it.data.message,
                        Snackbar.LENGTH_SHORT
                    )
                }

                is ApiResponse.Error -> {
                    Global.showErrorMessage(
                        binding.root.rootView,
                        it.errorMessage,
                        Snackbar.LENGTH_SHORT
                    )
//                    binding.loadingView.visibility = View.GONE
//                    binding.loadingView.hide()
                }

                ApiResponse.Loading -> {
//                    binding.loadingView.visibility = View.VISIBLE
//                    binding.loadingView.show()
                }
            }

        })
    }

    @SuppressLint("PotentialBehaviorOverride")
    private fun showDispensories(data: List<Dispensory>, mMap: GoogleMap) {

        data.forEach {
            Log.d(
                "Marker",
                "Adding marker at: ${it.latitude}, ${it.longitude} - ${it.dispensoryName}"
            )
            try {
                val lat = it.latitude.toDouble()
                val lon = it.longitude.toDouble()
                val name = it.dispensoryName ?: "No Name"
                mMap.addMarker(
                    MarkerOptions()
                        .position(LatLng(lat, lon))
                        .title(name)
                        .icon(bitmapDescriptorFromVector(R.drawable.ic_dispensary))
                )
            } catch (e: Exception) {
                Log.e("MapError", "Invalid location data: ${it.latitude}, ${it.longitude}")
            }
        }


        mMap.moveCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(
                    data[0].latitude.toDouble(),
                    data[0].longitude.toDouble()
                ), 10f
            )
        )

        mMap.setOnMarkerClickListener { marker ->
            val selectedDispensory = data.find {
                it.dispensoryName == marker.title
            }

            showBottomSheet(marker, selectedDispensory)
            true
        }


    }


    private fun showBottomSheet(marker: Marker, selectedDispensory: Dispensory?) {
        val bottomSheetDialog = BottomSheetDialog.newInstance(
            marker.title,
            "Product Available",
            "${selectedDispensory?.distance ?: "0"} miles away",
            marker.position,
            selectedDispensory,
            bookingId
        )
        bottomSheetDialog.show(supportFragmentManager, "BottomSheetDialog")
    }


    private fun bitmapDescriptorFromVector(vectorResId: Int): BitmapDescriptor {
        val vectorDrawable = ContextCompat.getDrawable(this, vectorResId)
        vectorDrawable!!.setBounds(
            0,
            0,
            60,
            60
        )
        val bitmap = Bitmap.createBitmap(
            60,
           60,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    private fun getLatLon() {
        val gpsTracker = GPSTracker(this)

        if (gpsTracker.isGPSTrackingEnabled) {
            latitude = gpsTracker.getLatitude().toString()
            longitude = gpsTracker.getLongitude().toString()

            helper.saveStringValue(PreferenceKeys.KEY_LATITUDE, latitude)
            helper.saveStringValue(PreferenceKeys.KEY_LONGITUDE, longitude)
            Global.showMessage(
                binding.root.rootView,
                "Latitude : $latitude == Longitude:$longitude",
                Toast.LENGTH_LONG
            )

        } else {
            gpsTracker.showSettingsAlert()
        }
    }

}