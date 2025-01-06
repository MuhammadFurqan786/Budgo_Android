package com.sokoldev.budgo.common.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.sokoldev.budgo.common.ui.user.LoginActivity
import com.sokoldev.budgo.common.utils.GPSTracker
import com.sokoldev.budgo.common.utils.Global
import com.sokoldev.budgo.common.utils.prefs.PreferenceHelper
import com.sokoldev.budgo.common.utils.prefs.PreferenceKeys
import com.sokoldev.budgo.databinding.ActivitySplashBinding
import com.sokoldev.budgo.patient.dialog.CustomAgeDialogFragment

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    private val RC_PERMISSIONS = 101
    private val permissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.READ_EXTERNAL_STORAGE
    )

    private lateinit var binding: ActivitySplashBinding
    private lateinit var helper: PreferenceHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        helper = PreferenceHelper.getPref(this)


        binding.apply {
            getStarted.setOnClickListener {
                checkForPermission()
                getStarted.visibility = View.GONE
                selectRole.visibility = View.VISIBLE
            }
            patient.setOnClickListener {
                helper.setPatientUser(true)
                val dialogFragment = CustomAgeDialogFragment()
                dialogFragment.show(supportFragmentManager, "CustomDialog")
            }
            caregiver.setOnClickListener {
                val intent = Intent(this@SplashActivity, LoginActivity::class.java)
                helper.setPatientUser(false)
                startActivity(intent)
            }

        }

    }

    private fun checkForPermission() {
        Log.d("TAG", "checkForPermission: called   ${Global.hasPermissions(this, permissions)}")
        if (Global.hasPermissions(this, permissions)) {
            getLatLon()
        } else {
            requestPermissions(permissions, RC_PERMISSIONS)
        }
    }

    private fun getLatLon() {
        val gpsTracker = GPSTracker(this)

        if (gpsTracker.isGPSTrackingEnabled) {
            val stringLatitude = gpsTracker.getLatitude().toString()
            val stringLongitude = gpsTracker.getLongitude().toString()
            val country = gpsTracker.getCountryName(this)
            val city = gpsTracker.getLocality(this)
            val postalCode = gpsTracker.getPostalCode(this)
            val addressLine = gpsTracker.getAddressLine(this)

            helper.saveStringValue(PreferenceKeys.KEY_LATITUDE, stringLatitude)
            helper.saveStringValue(PreferenceKeys.KEY_LONGITUDE, stringLongitude)


        } else {
            gpsTracker.showSettingsAlert()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == RC_PERMISSIONS) {
            // Check if all permissions were granted
            var allPermissionsGranted = true
            for (result in grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allPermissionsGranted = false
                    break
                }
            }

            if (allPermissionsGranted) {
                // If all permissions were granted, get the current location
                getLatLon()
            } else {
                Log.d("TAG", "Permissions not granted")
            }
        }


    }

}