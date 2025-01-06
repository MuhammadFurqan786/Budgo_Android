package com.sokoldev.budgo.caregiver.ui.task

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.sokoldev.budgo.R
import com.sokoldev.budgo.databinding.ActivityPatientLocationBinding
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class PatientLocationActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var binding: ActivityPatientLocationBinding
    private lateinit var latLng: LatLng
    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPatientLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            back.setOnClickListener { finish() }
        }

        val lat = intent.getDoubleExtra("latitude", 0.0)
        val lon = intent.getDoubleExtra("longitude", 0.0)
        latLng = LatLng(lat, lon)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        binding.navigate.setOnClickListener {
            binding.navigate.visibility = View.GONE
            binding.navigate.visibility = View.VISIBLE
        }
        binding.navigate.setOnClickListener {
            startActivity(Intent(this@PatientLocationActivity, DropOffLocationActivity::class.java))
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        val patientLocation = LatLng(40.730610, -73.935242)

        mMap.addMarker(
            MarkerOptions().position(latLng).title("Dispensary")
                .icon(bitmapDescriptorFromVector(R.drawable.ic_dispensary))
        )

        mMap.addMarker(
            MarkerOptions().position(patientLocation).title("Patient Location")
                .icon(bitmapDescriptorFromVector(R.drawable.ic_pateint_location_map))
        )

        val url = getDirectionsUrl(latLng, patientLocation)
        Log.d("DirectionsURL", url)
        GetDirection(url).execute()

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10f))
    }

    private fun getDirectionsUrl(origin: LatLng, dest: LatLng): String {
        val strOrigin = "origin=${origin.latitude},${origin.longitude}"
        val strDest = "destination=${dest.latitude},${dest.longitude}"
        val mode = "mode=driving"
        val parameters = "$strOrigin&$strDest&$mode"
        val output = "json"
        return "https://maps.googleapis.com/maps/api/directions/$output?$parameters&key=Your_API_KEY"
    }

    private inner class GetDirection(val url: String) : AsyncTask<Void, Void, String>() {
        override fun doInBackground(vararg params: Void?): String {
            var data = ""
            try {
                val url = URL(url)
                val connection = url.openConnection() as HttpURLConnection
                connection.connect()
                val inputStream = connection.inputStream
                val reader = BufferedReader(InputStreamReader(inputStream))
                val buffer = StringBuffer()
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    buffer.append(line)
                }
                data = buffer.toString()
                reader.close()
            } catch (e: Exception) {
                Log.e("Background Task", e.toString())
            }
            return data
        }

        override fun onPostExecute(result: String) {
            super.onPostExecute(result)
            Log.d("DirectionsResponse", result)
            val parserTask = ParserTask()
            parserTask.execute(result)
        }
    }

    private inner class ParserTask : AsyncTask<String, Int, List<List<HashMap<String, String>>>>() {
        override fun doInBackground(vararg jsonData: String?): List<List<HashMap<String, String>>> {
            val jsonObject: JSONObject
            var routes: List<List<HashMap<String, String>>> = ArrayList()
            try {
                jsonObject = JSONObject(jsonData[0]!!)
                val parser = DirectionsJSONParser()
                routes = parser.parse(jsonObject)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return routes
        }

        override fun onPostExecute(result: List<List<HashMap<String, String>>>) {
            val lineOptions = PolylineOptions()
            for (i in result.indices) {
                val path = result[i]
                val points = ArrayList<LatLng>()
                for (j in path.indices) {
                    val point = path[j]
                    val lat = point["lat"]!!.toDouble()
                    val lng = point["lng"]!!.toDouble()
                    val position = LatLng(lat, lng)
                    points.add(position)
                }
                lineOptions.addAll(points)
                lineOptions.width(10f)
                lineOptions.color(
                    ContextCompat.getColor(
                        this@PatientLocationActivity,
                        R.color.primary
                    )
                )
            }
            if (lineOptions != null) {
                mMap.addPolyline(lineOptions)
            }
        }
    }

    inner class DirectionsJSONParser {
        fun parse(jObject: JSONObject): List<List<HashMap<String, String>>> {
            val routes = ArrayList<List<HashMap<String, String>>>()
            val jRoutes: JSONArray
            var jLegs: JSONArray
            var jSteps: JSONArray
            try {
                jRoutes = jObject.getJSONArray("routes")
                for (i in 0 until jRoutes.length()) {
                    jLegs = (jRoutes[i] as JSONObject).getJSONArray("legs")
                    val path = ArrayList<HashMap<String, String>>()
                    for (j in 0 until jLegs.length()) {
                        jSteps = (jLegs[j] as JSONObject).getJSONArray("steps")
                        for (k in 0 until jSteps.length()) {
                            val polyline =
                                ((jSteps[k] as JSONObject).get("polyline") as JSONObject).get("points") as String
                            val list = decodePoly(polyline)
                            for (l in list.indices) {
                                val hm = HashMap<String, String>()
                                hm["lat"] = java.lang.Double.toString(list[l].latitude)
                                hm["lng"] = java.lang.Double.toString(list[l].longitude)
                                path.add(hm)
                            }
                        }
                    }
                    routes.add(path)
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            return routes
        }

        private fun decodePoly(encoded: String): List<LatLng> {
            val poly = ArrayList<LatLng>()
            var index = 0
            val len = encoded.length
            var lat = 0
            var lng = 0
            while (index < len) {
                var b: Int
                var shift = 0
                var result = 0
                do {
                    b = encoded[index++].toInt() - 63
                    result = result or (b and 0x1f shl shift)
                    shift += 5
                } while (b >= 0x20)
                val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
                lat += dlat
                shift = 0
                result = 0
                do {
                    b = encoded[index++].toInt() - 63
                    result = result or (b and 0x1f shl shift)
                    shift += 5
                } while (b >= 0x20)
                val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
                lng += dlng
                val p = LatLng(lat.toDouble() / 1E5, lng.toDouble() / 1E5)
                poly.add(p)
            }
            return poly
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
}
