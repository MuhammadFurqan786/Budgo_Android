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
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.maps.android.PolyUtil
import com.sokoldev.budgo.R
import com.sokoldev.budgo.caregiver.viewmodels.JobViewModel
import com.sokoldev.budgo.common.data.models.ChatUser
import com.sokoldev.budgo.common.data.models.response.BookingDetails
import com.sokoldev.budgo.common.data.models.response.Dispensory
import com.sokoldev.budgo.common.data.remote.network.ApiResponse
import com.sokoldev.budgo.common.ui.chat.ChatActivity
import com.sokoldev.budgo.common.utils.GPSTracker
import com.sokoldev.budgo.common.utils.Global
import com.sokoldev.budgo.common.utils.OrderStatus
import com.sokoldev.budgo.common.utils.prefs.PreferenceHelper
import com.sokoldev.budgo.common.utils.prefs.PreferenceKeys
import com.sokoldev.budgo.databinding.ActivityPatientLocationBinding
import com.sokoldev.budgo.patient.ui.order.OrderViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL

class PatientLocationActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var binding: ActivityPatientLocationBinding
    private lateinit var mMap: GoogleMap
    private lateinit var latLng: LatLng
    private lateinit var userLatLng: LatLng
    private var dispensary: Dispensory? = null
    private lateinit var helper: PreferenceHelper
    private var bookingDetails: BookingDetails? = null
    private val viewModel: OrderViewModel by viewModels()
    private val viewModelJob: JobViewModel by viewModels()
    private var patientLat: Double? = null
    private var patientLon: Double? = null
    private var bookingId: String? = null
    private var latitude: String = ""
    private var longitude: String = ""
    private var patientId: String = ""
    private var caregiverId: String = ""

    private val apiKey = "AIzaSyAlT_fm5x5NLOan7OWKGMG_taLHhaQ01ko"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityPatientLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        helper = PreferenceHelper.getPref(this)
        bookingId = intent.getStringExtra(PreferenceKeys.BOOKING_ID)

        getLatLon()
        bookingId = intent.getStringExtra(PreferenceKeys.BOOKING_ID)
        helper.getStringValue(PreferenceKeys.PREF_USER_TOKEN)
            ?.let {
                if (bookingId != null) {
                    viewModel.getBookingById(it, bookingId!!)
                }
            }


        if (latitude.isNotEmpty() && longitude.isNotEmpty()) {
            userLatLng = LatLng(latitude.toDouble(), longitude.toDouble())
        }

        binding.back.setOnClickListener { finish() }
        binding.navigate.setOnClickListener {
            helper.getStringValue(PreferenceKeys.PREF_USER_TOKEN)?.let {
                if (bookingId != null) {
                    viewModelJob.changeOrderStatus(it, bookingId!!, OrderStatus.NAVIGATING)
                }
            }
        }


        binding.apply {
            back.setOnClickListener { finish() }

            chatButton.setOnClickListener {
                if (bookingDetails != null) {
                    val receiverId = bookingDetails?.patientId
                    val receiverName = bookingDetails?.patientDetails?.name
                    val receiverImage = bookingDetails?.patientDetails?.profileImage
                    val senderId =
                        helper.getCurrentUser()?.id
                    val senderName = helper.getCurrentUser()?.name
                    val senderImage = helper.getCurrentUser()?.profileImage

                    if (senderName != null && senderImage != null && receiverId != null && receiverName != null && receiverImage != null) {
                        checkOrStartChat(
                            senderId.toString(), senderName, senderImage,
                            receiverId.toString(), receiverName, receiverImage
                        )
                    }
                }
            }
        }

        initObserver()
        binding.btnOpenGoogleMaps.setOnClickListener {
            openGoogleMap()
        }


    }

    private fun openGoogleMap() {
        val uri = Uri.parse(
            "https://www.google.com/maps/dir/?api=1" +
                    "&origin=$latitude,$longitude" +
                    "&destination=$patientLat,$patientLon" +
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

    private fun checkOrStartChat(
        senderId: String,
        senderName: String,
        senderImage: String,
        receiverId: String,
        receiverName: String,
        receiverImage: String
    ) {

        val chatListRef = FirebaseDatabase.getInstance().getReference("chat_list")
        val chatNodeRef = chatListRef.child(senderId).child(receiverId)

        chatNodeRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val chatKey: String
                if (snapshot.exists()) {
                    // Chat already exists — get existing chatKey
                    chatKey = snapshot.child("chatKey").getValue(String::class.java) ?: return
                } else {
                    // Chat doesn't exist — create it

                    chatKey =
                        if (senderId < receiverId) "${senderId}_${receiverId}" else "${receiverId}_${senderId}"
                    val timestamp = System.currentTimeMillis()

                    val senderEntry = mapOf(
                        "name" to receiverName,
                        "profileImageUrl" to receiverImage,
                        "chatKey" to chatKey,
                        "lastMessage" to "",
                        "timestamp" to timestamp,
                        "isRead" to true
                    )

                    val receiverEntry = mapOf(
                        "name" to senderName,
                        "profileImageUrl" to senderImage,
                        "chatKey" to chatKey,
                        "lastMessage" to "",
                        "timestamp" to timestamp,
                        "isRead" to false
                    )
                    chatListRef.child(senderId).child(receiverId).setValue(senderEntry)
                    chatListRef.child(receiverId).child(senderId).setValue(receiverEntry)
                }

                // Start ChatActivity regardless
                val intent = Intent(this@PatientLocationActivity, ChatActivity::class.java)
                intent.putExtra(
                    "chatUser",
                    ChatUser(receiverId, receiverName, receiverImage, chatKey)
                )
                startActivity(intent)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    this@PatientLocationActivity,
                    "Error: ${error.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
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
                        OrderStatus.NAVIGATING
                    )
                    helper.saveBooleanValue(
                        PreferenceKeys.Is_FIRST_NAVIGATING,
                        false
                    )

                    startActivity(
                        Intent(this, DropOffLocationActivity::class.java)
                            .putExtra("latitude", patientLat)
                            .putExtra("longitude", patientLon)
                            .putExtra(PreferenceKeys.BOOKING_ID, bookingId)
                            .putExtra("patientId", patientId)
                            .putExtra("caregiverId", caregiverId)
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
        patientId = bookingDetails.patientDetails.id.toString()
        caregiverId = bookingDetails.caregiverDetails.id.toString()
        binding.name.text = bookingDetails.patientDetails.name
        patientLat = bookingDetails.patientDetails.latitude.toDouble()
        patientLon = bookingDetails.patientDetails.longitude.toDouble()
        val results = FloatArray(1)
        patientLat?.let {
            patientLon?.let { it1 ->
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


        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)


    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        latLng = patientLat?.let { patientLon?.let { it1 -> LatLng(it, it1) } }!!

        if (latLng != null) {
            mMap.addMarker(
                MarkerOptions()
                    .position(userLatLng)
                    .title("You")
                    .icon(bitmapDescriptorFromVector(R.drawable.ic_dispensary))
            )
        }

        if (userLatLng != null) {
            mMap.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .title(bookingDetails?.patientDetails?.name)
            )
        }

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
                            this@PatientLocationActivity,
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
                                        this@PatientLocationActivity,
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
                            this@PatientLocationActivity,
                            "No route found",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@PatientLocationActivity,
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
