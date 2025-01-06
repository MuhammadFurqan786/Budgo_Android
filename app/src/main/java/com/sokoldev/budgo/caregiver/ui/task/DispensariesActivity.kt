package com.sokoldev.budgo.caregiver.ui.task

import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.sokoldev.budgo.R
import com.sokoldev.budgo.databinding.ActivityDispensariesBinding
import com.sokoldev.budgo.patient.dialog.BottomSheetDialog

class DispensariesActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityDispensariesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDispensariesBinding.inflate(layoutInflater)
        setContentView(binding.root)
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
        mMap = googleMap

        val locations = listOf(
            LatLng(40.7128, -72.0060),
            LatLng(40.751282, -73.0060),
            LatLng(40.712776, -74.0050)
        )

        locations.forEach {
            mMap.addMarker(
                MarkerOptions().position(it).title("Dispensary")
                    .icon(bitmapDescriptorFromVector(R.drawable.ic_dispensary))
            )
        }

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(locations[0], 10f))

        mMap.setOnMarkerClickListener { marker ->
            showBottomSheet(marker)
            true
        }
    }

    private fun showBottomSheet(marker: Marker) {
        val bottomSheetDialog = BottomSheetDialog.newInstance(
            marker.title,
            "Product Available",
            "1.2 miles away",
            marker.position
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

}