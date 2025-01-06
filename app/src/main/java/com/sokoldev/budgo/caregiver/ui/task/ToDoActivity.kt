package com.sokoldev.budgo.caregiver.ui.task

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.view.View
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
import com.sokoldev.budgo.databinding.ActivityToDoBinding
import com.sokoldev.budgo.patient.dialog.OrderDialogFragment

class ToDoActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var binding: ActivityToDoBinding
    private lateinit var mMap: GoogleMap
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityToDoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)


        binding.apply {

            back.setOnClickListener { finish() }
            val isDoneScanning = intent.getBooleanExtra("isDone", false)
            if (isDoneScanning) {
                scanButton.visibility = View.GONE
                contactDetailsLayout.visibility = View.GONE
                checkedButton.visibility = View.VISIBLE
                takePhotoButton.visibility = View.VISIBLE
            }


            scanButton.setOnClickListener {
                startActivity(Intent(this@ToDoActivity, ScanActivity::class.java))
            }
            takePhotoButton.setOnClickListener {
                confirmPhotoLayout.visibility = View.VISIBLE
                takePhotoButton.visibility = View.GONE
                completeOrderButton.visibility = View.VISIBLE
            }

            completeOrderButton.setOnClickListener {
                val dialogFragment = OrderDialogFragment()
                dialogFragment.show(supportFragmentManager, "CustomDialog")
            }

        }

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        val patientLocation = LatLng(40.730610, -73.935242)

        mMap.addMarker(
            MarkerOptions().position(patientLocation).title("Patient Location")
                .icon(bitmapDescriptorFromVector(R.drawable.ic_pateint_location_map))
        )

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(patientLocation, 10f))
    }

    private fun bitmapDescriptorFromVector(vectorResId: Int): BitmapDescriptor {
        val vectorDrawable = ContextCompat.getDrawable(this, vectorResId)
        vectorDrawable!!.setBounds(0, 0, 60, 60)
        val bitmap = Bitmap.createBitmap(60, 60, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }
}