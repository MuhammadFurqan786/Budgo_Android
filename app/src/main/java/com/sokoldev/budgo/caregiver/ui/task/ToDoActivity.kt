package com.sokoldev.budgo.caregiver.ui.task

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
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
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.sokoldev.budgo.R
import com.sokoldev.budgo.caregiver.viewmodels.JobViewModel
import com.sokoldev.budgo.common.data.models.ChatUser
import com.sokoldev.budgo.common.data.models.response.BookingDetails
import com.sokoldev.budgo.common.data.remote.network.ApiResponse
import com.sokoldev.budgo.common.ui.chat.ChatActivity
import com.sokoldev.budgo.common.utils.OrderStatus
import com.sokoldev.budgo.common.utils.prefs.PreferenceHelper
import com.sokoldev.budgo.common.utils.prefs.PreferenceKeys
import com.sokoldev.budgo.databinding.ActivityToDoBinding
import com.sokoldev.budgo.patient.dialog.OrderDialogFragment
import com.sokoldev.budgo.patient.ui.order.OrderViewModel
import java.io.File

class ToDoActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var binding: ActivityToDoBinding
    private lateinit var mMap: GoogleMap
    private var patientLat: Double? = null
    private var patientLon: Double? = null
    private var bookingId: String? = null
    private var patientId: String? = null
    private lateinit var helper: PreferenceHelper
    private var caregiverId: String? = null
    private lateinit var photoUri: Uri
    private var photoFile: File? = null
    private lateinit var userType: String
    private val viewModel: JobViewModel by viewModels()
    private var bookingDetails: BookingDetails? = null
    private val viewModelOrder: OrderViewModel by viewModels()
    private var latitude: String = ""
    private var longitude: String = ""

    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success && photoFile?.exists() == true) {
                binding.confirmPhotoLayout.visibility = View.VISIBLE
                binding.takePhotoButton.visibility = View.GONE
                binding.completeOrderButton.visibility = View.VISIBLE
            } else {
                Toast.makeText(this, "Picture was not taken.", Toast.LENGTH_SHORT).show()
            }
        }



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

        helper = PreferenceHelper.getPref(this)
        userType = helper.getCurrentUser()?.type.toString()
        patientLat = intent.getDoubleExtra("latitude", 0.0)
        patientLon = intent.getDoubleExtra("longitude", 0.0)
        bookingId = intent.getStringExtra(PreferenceKeys.BOOKING_ID)
        patientId = intent.getStringExtra("patientId")
        caregiverId = intent.getStringExtra("caregiverId")

        helper.getStringValue(PreferenceKeys.PREF_USER_TOKEN)
            ?.let {
                if (bookingId != null) {
                    viewModelOrder.getBookingById(it, bookingId!!)
                }
            }


        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        initObserver()

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
                startActivity(
                    Intent(this@ToDoActivity, ScanActivity::class.java)
                        .putExtra("latitude", patientLat)
                        .putExtra("longitude", patientLon)
                        .putExtra(PreferenceKeys.BOOKING_ID, bookingId)
                        .putExtra("patientId", patientId)
                        .putExtra("caregiverId", caregiverId)
                )
            }
            takePhotoButton.setOnClickListener {
                photoFile = createImageFile()
                photoUri = FileProvider.getUriForFile(
                    applicationContext,
                    "${applicationContext.packageName}.provider",
                    photoFile!!
                )
                cameraLauncher.launch(photoUri)
            }

            completeOrderButton.setOnClickListener {
                helper.getStringValue(PreferenceKeys.PREF_USER_TOKEN)?.let { it ->
                    if (bookingId != null) {
                        photoFile?.let { it1 ->
                            viewModel.changeOrderStatus(
                                it, bookingId!!, "completed",
                                it1
                            )
                        }
                    }
                }
            }
            chatButton.setOnClickListener {
                if (bookingDetails != null) {
                    val receiverId = bookingDetails?.patientId
                    val receiverName = bookingDetails?.patientDetails?.name
                    val receiverImage = bookingDetails?.patientDetails?.profileImage
                    val senderId = helper.getCurrentUser()?.id
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
    }


    fun initObserver() {
        viewModel.orderStatusResponse.observe(this) {
            when (it) {
                is ApiResponse.Success -> {
                    binding.loadingView.visibility = View.GONE
                    val otherUserId = if (userType == "1") caregiverId else patientId
                    helper.saveStringValue(
                        PreferenceKeys.CURRENT_BOOKING_STATUS,
                        OrderStatus.COMPLETED
                    )
                    helper.saveBooleanValue(PreferenceKeys.IS_CURRENT_BOOKING, false)
                    val dialogFragment = otherUserId?.let { it1 ->
                        bookingId?.let { it2 ->
                            OrderDialogFragment.newInstance(
                                it1,
                                it2
                            )
                        }
                    }
                    if (dialogFragment != null) {
                        dialogFragment.show(supportFragmentManager, "CustomDialog")
                    }
                }

                is ApiResponse.Error -> {
                    binding.loadingView.visibility = View.GONE
                    Toast.makeText(this, it.errorMessage, Toast.LENGTH_SHORT).show()
                }

                is ApiResponse.Loading -> {
                    binding.loadingView.visibility = View.VISIBLE
                }
            }
        }


        viewModelOrder.apiResponseSingleBooking.observe(this) { response ->
            when (response) {
                is ApiResponse.Success -> {
                    binding.loadingView.visibility = View.GONE
                    response.data.data.let {
                        bookingDetails = it
                    }
                }

                is ApiResponse.Error -> {
                    binding.loadingView.visibility = View.GONE
                    Toast.makeText(this, response.errorMessage, Toast.LENGTH_SHORT).show()
                }

                is ApiResponse.Loading -> {
                    binding.loadingView.visibility = View.VISIBLE
                }
            }
        }


    }

    private fun createImageFile(): File {
        val timestamp = System.currentTimeMillis()
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("JPEG_${timestamp}_", ".jpg", storageDir)
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        val patientLocation = LatLng(patientLat!!, patientLon!!)

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
                val intent = Intent(this@ToDoActivity, ChatActivity::class.java)
                intent.putExtra(
                    "chatUser",
                    ChatUser(receiverId, receiverName, receiverImage, chatKey)
                )
                startActivity(intent)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    this@ToDoActivity,
                    "Error: ${error.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }
}