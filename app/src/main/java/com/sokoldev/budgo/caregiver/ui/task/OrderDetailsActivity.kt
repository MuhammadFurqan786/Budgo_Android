package com.sokoldev.budgo.caregiver.ui.task

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.imageview.ShapeableImageView
import com.google.maps.android.PolyUtil
import com.sokoldev.budgo.R
import com.sokoldev.budgo.caregiver.viewmodels.JobViewModel
import com.sokoldev.budgo.common.data.models.response.BookingDetails
import com.sokoldev.budgo.common.data.models.response.Dispensory
import com.sokoldev.budgo.common.data.remote.network.ApiResponse
import com.sokoldev.budgo.common.utils.OrderStatus
import com.sokoldev.budgo.common.utils.prefs.PreferenceHelper
import com.sokoldev.budgo.common.utils.prefs.PreferenceKeys
import com.sokoldev.budgo.databinding.ActivityOrderDetailsBinding
import com.sokoldev.budgo.patient.ui.order.OrderViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL

class OrderDetailsActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var binding: ActivityOrderDetailsBinding
    private lateinit var mMap: GoogleMap
    private lateinit var latLng: LatLng
    private lateinit var userLatLng: LatLng
    private var dispensary: Dispensory? = null
    private lateinit var helper: PreferenceHelper
    private var bookingDetails: BookingDetails? = null
    private val viewModel: OrderViewModel by viewModels()
    private val viewModelJob: JobViewModel by viewModels()
    private var dispensaryLat: Double? = null
    private var dispensaryLon: Double? = null
    private var userLat: Double? = null
    private var userLon: Double? = null
    private var bookingId: String? = null
    private var cardFrontImage: String? = null

    private val apiKey = "AIzaSyAlT_fm5x5NLOan7OWKGMG_taLHhaQ01ko"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityOrderDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        helper = PreferenceHelper.getPref(this)

        userLat = helper.getStringValue(PreferenceKeys.KEY_LATITUDE)?.toDouble()
        userLon = helper.getStringValue(PreferenceKeys.KEY_LONGITUDE)?.toDouble()

        bookingId = intent.getStringExtra(PreferenceKeys.BOOKING_ID)
        helper.getStringValue(PreferenceKeys.PREF_USER_TOKEN)
            ?.let {
                if (bookingId != null) {
                    viewModel.getBookingById(it, bookingId!!)
                }
            }
        dispensary = intent.getParcelableExtra<Dispensory>("dispensory")

        dispensaryLat = dispensary?.latitude?.toDouble()
        dispensaryLon = dispensary?.longitude?.toDouble()
        val dispensoryName = dispensary?.dispensoryName
        binding.dispenaryName.text = "Location: $dispensoryName"

        if (dispensaryLat != null && dispensaryLon != null) {
            latLng = LatLng(dispensaryLat!!, dispensaryLon!!)
        }
        if (userLat != null && userLon != null) {
            userLatLng = LatLng(userLat!!, userLon!!)
        }

        binding.back.setOnClickListener { finish() }
        binding.leaveButton.setOnClickListener {
            helper.getStringValue(PreferenceKeys.PREF_USER_TOKEN)?.let {
                if (bookingId != null) {
                    viewModelJob.changeOrderStatus(it, bookingId!!, OrderStatus.LEAVING)
                }
            }
        }

        binding.btnOpenGoogleMaps.setOnClickListener {
            openGoogleMap()
        }

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        initObserver()
    }

    private fun openGoogleMap() {
        val uri = Uri.parse(
            "https://www.google.com/maps/dir/?api=1" +
                    "&origin=$userLat,$userLon" +
                    "&destination=$dispensaryLat,$dispensaryLon" +
                    "&travelmode=driving"
        )

        val intent = Intent(Intent.ACTION_VIEW, uri)
        intent.setPackage("com.google.android.apps.maps")

        try {
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            // Google Maps not installed, open in browser
            startActivity(Intent(Intent.ACTION_VIEW, uri))
        }

    }

    private fun initObserver() {
        viewModel.apiResponseSingleBooking.observe(this) { response ->
            when (response) {
                is ApiResponse.Success -> {
                    response.data.data.let {
                        bookingDetails = it
                        showData(it)
                    }
                }

                is ApiResponse.Error -> {
                    Toast.makeText(this, response.errorMessage, Toast.LENGTH_SHORT).show()
                }

                is ApiResponse.Loading -> {

                }
            }
        }

        viewModelJob.orderStatusResponse.observe(this) {
            when (it) {
                is ApiResponse.Success -> {
                    helper.saveStringValue(
                        PreferenceKeys.CURRENT_BOOKING_STATUS,
                        OrderStatus.LEAVING
                    )
                    cardFrontImage?.let { it1 ->
                        helper.saveStringValue(
                            PreferenceKeys.PATIENT_CARD_FRONT,
                            it1
                        )
                    }
                    startActivity(
                        Intent(this, PatientLocationActivity::class.java)
                            .putExtra("latitude", dispensaryLat)
                            .putExtra("longitude", dispensaryLon)
                            .putExtra(PreferenceKeys.BOOKING_ID, bookingId)
                    )
                }

                is ApiResponse.Error -> {
                    Toast.makeText(this, it.errorMessage, Toast.LENGTH_SHORT).show()
                }

                is ApiResponse.Loading -> {
                }


            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun showData(bookingDetails: BookingDetails) {
        cardFrontImage = bookingDetails.patientDetails.cardFrontImage
        binding.name.text = bookingDetails.patientDetails.name
        val results = FloatArray(1)
        dispensaryLat?.let {
            dispensaryLon?.let { it1 ->
                Location.distanceBetween(
                    userLatLng.latitude,
                    userLatLng.longitude,
                    it,
                    it1,
                    results
                )
            }
        }
        val distanceInMeters = results[0]
        val distanceInKilometers = distanceInMeters / 1000
        binding.jobDistance.text = "$distanceInKilometers KM"
        bookingDetails.products[0].productImage?.let {
            loadImageWithBaseUrlFallback(binding.jobImage,
                it
            )
        }

        binding.productQuantity.text = bookingDetails.products.size.toString()
        binding.productName.text = bookingDetails.patientDetails.phone
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        if (latLng != null) {
            mMap.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .title(dispensary?.dispensoryName ?: "Dispensary")
                    .icon(bitmapDescriptorFromVector(R.drawable.ic_dispensary))
            )
        }

        if (userLatLng != null) {
            mMap.addMarker(
                MarkerOptions()
                    .position(userLatLng)
                    .title("You")
            )
        }
        googleMap.uiSettings.isMapToolbarEnabled = true
        if (latLng != null && userLatLng != null) {
            drawRoute(userLatLng, latLng)
        }

    }

    private fun drawRoute(origin: LatLng, destination: LatLng) {
        val url = "https://maps.googleapis.com/maps/api/directions/json?" +
                "origin=${origin.latitude},${origin.longitude}" +
                "&destination=${destination.latitude},${destination.longitude}" +
                "&key=$apiKey"

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val result = URL(url).readText()

                // 👉 Print full JSON response to log
                println("Directions API Response: $result")

                val jsonObject = JSONObject(result)
                val status = jsonObject.getString("status")

                if (status != "OK") {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@OrderDetailsActivity,
                            "Error from API: $status",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    return@launch
                }

                val routes = jsonObject.getJSONArray("routes")
                if (routes.length() > 0) {
                    val points = routes.getJSONObject(0)
                        .getJSONObject("overview_polyline")
                        .getString("points")
                    val decodedPath = PolyUtil.decode(points)

                    withContext(Dispatchers.Main) {
                        mMap.addPolyline(
                            PolylineOptions()
                                .addAll(decodedPath)
                                .color(
                                    ContextCompat.getColor(
                                        this@OrderDetailsActivity,
                                        R.color.primary
                                    )
                                )
                                .width(10f)
                        )

                        val builder = LatLngBounds.builder()
                        for (point in decodedPath) {
                            builder.include(point)
                        }
                        val bounds = builder.build()
                        val padding = 150
                        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding))
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@OrderDetailsActivity,
                            "No route found",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@OrderDetailsActivity,
                        "Failed to draw route",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }



    private fun bitmapDescriptorFromVector(vectorResId: Int): BitmapDescriptor {
        val vectorDrawable = ContextCompat.getDrawable(this, vectorResId)
        vectorDrawable!!.setBounds(0, 0, 60, 60)
        val bitmap = Bitmap.createBitmap(60, 60, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    private fun loadImageWithBaseUrlFallback(imageView: ShapeableImageView, originalUrl: String) {
        // Sanitize the original URL by replacing spaces
        val safeOriginalUrl = originalUrl.replace(" ", "%20")

        // Replace base URL and sanitize the fallback as well
        val modifiedUrl = safeOriginalUrl.replace(
            "https://budgo.net/budgo/public/",
            "https://admin.budgo.net/"
        )

        // Build the fallback request with the safe original URL
        val fallbackRequest = Glide.with(imageView.context)
            .load(safeOriginalUrl)

        // Start primary request with fallback
        Glide.with(imageView.context)
            .load(modifiedUrl)
            .error(fallbackRequest) // Try this if the primary fails
            .into(imageView)
    }



}
