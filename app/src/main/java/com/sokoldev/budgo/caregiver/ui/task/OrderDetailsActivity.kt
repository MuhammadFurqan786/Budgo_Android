package com.sokoldev.budgo.caregiver.ui.task

import android.content.Intent
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
import com.google.android.gms.maps.model.MarkerOptions
import com.sokoldev.budgo.R
import com.sokoldev.budgo.databinding.ActivityOrderDetailsBinding


class OrderDetailsActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var binding: ActivityOrderDetailsBinding
    private lateinit var mMap: GoogleMap
    private lateinit var latLng: LatLng
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

        val lat = intent.getDoubleExtra("latitude", 0.0)
        val lon = intent.getDoubleExtra("longitude", 0.0)
        latLng = LatLng(lat, lon)

        binding.apply {
            back.setOnClickListener { finish() }
        }
        binding.leaveButton.setOnClickListener {
            startActivity(
                Intent(this, PatientLocationActivity::class.java)
                    .putExtra("latitude", lat)
                    .putExtra("longitude", lon)
            )
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(p0: GoogleMap) {
        mMap = p0
        if (latLng != null) {
            mMap.addMarker(
                MarkerOptions().position(latLng).title("Dispensary")
                    .icon(bitmapDescriptorFromVector(R.drawable.ic_dispensary))
            )
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10f))
        }

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