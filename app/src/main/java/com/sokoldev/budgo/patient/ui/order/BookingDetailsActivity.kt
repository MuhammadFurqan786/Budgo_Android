package com.sokoldev.budgo.patient.ui.order

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.sokoldev.budgo.caregiver.ui.task.FeedbackActivity
import com.sokoldev.budgo.common.data.models.ChatUser
import com.sokoldev.budgo.common.data.models.response.Booking
import com.sokoldev.budgo.common.data.models.response.BookingDetails
import com.sokoldev.budgo.common.data.models.response.Review
import com.sokoldev.budgo.common.data.remote.network.ApiResponse
import com.sokoldev.budgo.common.ui.chat.ChatActivity
import com.sokoldev.budgo.common.utils.OrderStatus
import com.sokoldev.budgo.common.utils.prefs.PreferenceHelper
import com.sokoldev.budgo.common.utils.prefs.PreferenceKeys
import com.sokoldev.budgo.common.utils.prefs.PreferenceKeys.Companion.BOOKING
import com.sokoldev.budgo.databinding.ActivityBookingDetailsBinding
import com.sokoldev.budgo.patient.adapter.OrderProductAdapter

class BookingDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBookingDetailsBinding
    private lateinit var helper: PreferenceHelper

    private val viewModel: OrderViewModel by viewModels()
    private var bookingDetails: BookingDetails? = null

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityBookingDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        helper = PreferenceHelper.getPref(this)
        val booking: Booking? = intent.getParcelableExtra<Booking>(BOOKING)
        helper.getStringValue(PreferenceKeys.PREF_USER_TOKEN).let {
            viewModel.getBookingById(it.toString(), booking?.id.toString())
        }

        Log.d("BOOKING", booking.toString())


        binding.apply {
            back.setOnClickListener { finish() }
            buttonChat.setOnClickListener {
                if (bookingDetails != null) {
                    val receiverId = bookingDetails?.caregiverId
                    val receiverName = bookingDetails?.caregiverDetails?.name
                    val receiverImage = bookingDetails?.caregiverDetails?.profileImage

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

    }

    private fun initObserver() {
        viewModel.apiResponseSingleBooking.observe(this) { response ->
            when (response) {
                is ApiResponse.Success -> {
                    if (response.data.data != null) {
                        bookingDetails = response.data.data
                        binding.apply {
                            if (bookingDetails?.products?.isNotEmpty() == true) {
                                val products = response.data.data.products
                                val adapter = OrderProductAdapter(products)
                                binding.rvOrderProducts.adapter = adapter
                                bookingDetails?.amount.let { productPrice.text = "$$it" }
                                bookingDetails?.caregiverDetails?.name.let {
                                    driverName.text = it
                                }
                                bookingDetails?.caregiverDetails?.profileImage?.let {
                                    loadImageWithBaseUrlFallback(driverImage, it)
                                }


                                bookingDetails?.caregiverDetails?.phone.let {
                                    driverContact.text = it
                                }
                                if (bookingDetails?.orderStatus == OrderStatus.COMPLETED) {

                                    val currentUserId = helper.getCurrentUser()?.id
                                    val currentUserType = helper.getCurrentUser()?.type

                                    val reviews = bookingDetails?.reviews.orEmpty()
                                    var currentUserReview: Review? = null
                                    var otherUserReview: Review? = null

                                    // Split reviews into current and other user
                                    for (review in reviews) {
                                        if (review.from.id == currentUserId) {
                                            currentUserReview = review
                                        } else {
                                            otherUserReview = review
                                        }
                                    }

                                    // Show current user's review if present
                                    if (currentUserReview != null) {
                                        customerFeedbackLayout.visibility = View.VISIBLE
                                        userRatingBar.rating = currentUserReview.rating.toFloat()
                                        rating.text = currentUserReview.rating.toString()
                                        userFeedbackText.text = currentUserReview.review
                                    }

                                    // Show other user's review if present
                                    if (otherUserReview != null) {
                                        otherFeedback.visibility = View.VISIBLE
                                        binding.reviewText.text =
                                            if (currentUserType == "1") "Caregiver" else "Patient"
                                        binding.secondUserFeedback.text = otherUserReview.review
                                        binding.otherUserRatingText.text =
                                            otherUserReview.rating.toString()
                                        binding.otherUserRating.rating =
                                            otherUserReview.rating.toFloat() // Assuming secondUserRatingBar exists
                                    }

                                    // If current user hasn't given review, redirect to FeedbackActivity
                                    if (currentUserReview == null) {
                                        buttonChat.visibility = View.GONE
                                        val otherUserId =
                                            if (currentUserType == "1") bookingDetails?.caregiverId else bookingDetails?.patientId
                                        val bookingId = bookingDetails?.id.toString()

                                        val intent = Intent(
                                            this@BookingDetailsActivity,
                                            FeedbackActivity::class.java
                                        )
                                        intent.putExtra(PreferenceKeys.BOOKING_ID, bookingId)
                                        intent.putExtra("otherUserId", otherUserId.toString())
                                        startActivity(intent)
                                    }
                                }
                            }
                        }
                    }
                }

                is ApiResponse.Error -> {
                    Toast.makeText(this, response.errorMessage, Toast.LENGTH_SHORT).show()
                }

                is ApiResponse.Loading -> {

                }
            }

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
                val intent = Intent(this@BookingDetailsActivity, ChatActivity::class.java)
                intent.putExtra(
                    "chatUser",
                    ChatUser(receiverId, receiverName, receiverImage, chatKey)
                )
                startActivity(intent)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    this@BookingDetailsActivity,
                    "Error: ${error.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
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