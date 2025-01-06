package com.sokoldev.budgo.common.utils
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.IBinder
import android.provider.Settings
import android.util.Log
import com.sokoldev.budgo.R
import java.io.IOException
import java.util.Locale

class GPSTracker(private val mContext: Context) : Service(), LocationListener {

    private val TAG = GPSTracker::class.java.name

    private var isGPSEnabled = false
    private var isNetworkEnabled = false
    var isGPSTrackingEnabled = false
    private var location: Location? = null
    private var latitude = 0.0
    private var longitude = 0.0
    private val geocoderMaxResults = 1
    private var providerInfo: String? = null
    private var locationManager: LocationManager? = null

    init {
        getLocation()
    }

    @SuppressLint("MissingPermission")
    private fun getLocation() {
        try {
            locationManager = mContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager

            isGPSEnabled = locationManager?.isProviderEnabled(LocationManager.GPS_PROVIDER) ?: false
            isNetworkEnabled = locationManager?.isProviderEnabled(LocationManager.NETWORK_PROVIDER) ?: false

            if (isGPSEnabled) {
                isGPSTrackingEnabled = true
                providerInfo = LocationManager.GPS_PROVIDER
            } else if (isNetworkEnabled) {
                isGPSTrackingEnabled = true
                providerInfo = LocationManager.NETWORK_PROVIDER
            }

            if (!providerInfo.isNullOrEmpty()) {
                locationManager?.requestLocationUpdates(
                    providerInfo!!,
                    MIN_TIME_BW_UPDATES,
                    MIN_DISTANCE_CHANGE_FOR_UPDATES.toFloat(),
                    this
                )

                location = locationManager?.getLastKnownLocation(providerInfo!!)
                updateGPSCoordinates()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Impossible to connect to LocationManager", e)
        }
    }

    private fun updateGPSCoordinates() {
        location?.let {
            latitude = it.latitude
            longitude = it.longitude
        }
    }

    fun getLatitude(): Double {
        location?.let {
            latitude = it.latitude
        }
        return latitude
    }

    fun getLongitude(): Double {
        location?.let {
            longitude = it.longitude
        }
        return longitude
    }

    fun getIsGPSTrackingEnabled(): Boolean {
        return isGPSTrackingEnabled
    }

    fun stopUsingGPS() {
        locationManager?.removeUpdates(this)
    }

    fun showSettingsAlert() {
        val alertDialog = AlertDialog.Builder(mContext)

        alertDialog.setTitle(R.string.GPSAlertDialogTitle)
        alertDialog.setMessage(R.string.GPSAlertDialogMessage)

        alertDialog.setPositiveButton(R.string.action_settings) { _, _ ->
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            mContext.startActivity(intent)
        }

        alertDialog.setNegativeButton(R.string.cancel) { dialog, _ ->
            dialog.cancel()
        }

        alertDialog.show()
    }

    fun getGeocoderAddress(context: Context?): List<Address>? {
        location?.let {
            val geocoder = context?.let { it1 -> Geocoder(it1, Locale.ENGLISH) }
            try {
                return geocoder?.getFromLocation(latitude, longitude, geocoderMaxResults)
            } catch (e: IOException) {
                Log.e(TAG, "Impossible to connect to Geocoder", e)
            }
        }
        return null
    }

    fun getAddressLine(context: Context?): String? {
        val addresses = getGeocoderAddress(context)
        return if (!addresses.isNullOrEmpty()) {
            val address = addresses[0]
            address.getAddressLine(0)
        } else {
            null
        }
    }

    fun getLocality(context: Context?): String? {
        val addresses = getGeocoderAddress(context)
        return if (!addresses.isNullOrEmpty()) {
            val address = addresses[0]
            address.locality
        } else {
            null
        }
    }

    fun getPostalCode(context: Context?): String? {
        val addresses = getGeocoderAddress(context)
        return if (!addresses.isNullOrEmpty()) {
            val address = addresses[0]
            address.postalCode
        } else {
            null
        }
    }

    fun getCountryName(context: Context?): String? {
        val addresses = getGeocoderAddress(context)
        return if (!addresses.isNullOrEmpty()) {
            val address = addresses[0]
            address.countryName
        } else {
            null
        }
    }

    override fun onLocationChanged(location: Location) {}
    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
    override fun onProviderEnabled(provider: String) {}
    override fun onProviderDisabled(provider: String) {}
    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    companion object {
        private const val MIN_DISTANCE_CHANGE_FOR_UPDATES: Long = 10
        private const val MIN_TIME_BW_UPDATES: Long = 1000 * 60 * 1
    }
}
