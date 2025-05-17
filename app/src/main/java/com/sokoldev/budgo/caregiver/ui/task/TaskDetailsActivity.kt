package com.sokoldev.budgo.caregiver.ui.task

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.sokoldev.budgo.caregiver.adapter.OrderItemAdapter
import com.sokoldev.budgo.caregiver.viewmodels.JobViewModel
import com.sokoldev.budgo.common.data.models.ChatUser
import com.sokoldev.budgo.common.data.models.response.BookingDetails
import com.sokoldev.budgo.common.data.models.response.Job
import com.sokoldev.budgo.common.data.remote.network.ApiResponse
import com.sokoldev.budgo.common.ui.chat.ChatActivity
import com.sokoldev.budgo.common.utils.OrderStatus
import com.sokoldev.budgo.common.utils.prefs.PreferenceHelper
import com.sokoldev.budgo.common.utils.prefs.PreferenceKeys
import com.sokoldev.budgo.databinding.ActivityTaskDetailsBinding
import com.sokoldev.budgo.patient.adapter.OrderProductAdapter
import com.sokoldev.budgo.patient.ui.order.OrderViewModel


class TaskDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTaskDetailsBinding
    private lateinit var helper: PreferenceHelper
    private val viewModel: JobViewModel by viewModels()
    private var isAccepted = false
    private var bookingId: String? = null
    private var patientCardFront: String? = null
    private val viewModelOrder: OrderViewModel by viewModels()
    private var bookingDetails: BookingDetails? = null

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityTaskDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        helper = PreferenceHelper.getPref(this)
        isAccepted = intent.getBooleanExtra(PreferenceKeys.FROM_BOOKING, false)

        // Load data accordingly
        if (isAccepted) {
            val id = intent.getStringExtra(PreferenceKeys.BOOKING_ID)
            helper.getStringValue(PreferenceKeys.PREF_USER_TOKEN)?.let { token ->
                viewModelOrder.getBookingById(token, id.toString())
            }
        } else {
            val job = intent.getParcelableExtra<Job>("job")
            job?.let {
                setupUIWithJob(it)
            }
        }

        initObservers()

        binding.back.setOnClickListener { finish() }
    }

    private fun initObservers() {
        viewModel.orderStatusResponse.observe(this) { response ->
            when (response) {
                is ApiResponse.Loading -> {
                    binding.loadingView.visibility = View.VISIBLE
                }

                is ApiResponse.Success -> {
                    binding.loadingView.visibility = View.GONE
                    Toast.makeText(this, "Order Accepted", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, DispensariesActivity::class.java)
                    intent.putExtra(PreferenceKeys.BOOKING_ID, bookingId)
                    helper.saveBooleanValue(PreferenceKeys.IS_CURRENT_BOOKING, true)
                    patientCardFront?.let {
                        helper.saveStringValue(
                            PreferenceKeys.PATIENT_CARD_FRONT,
                            it
                        )
                    }
                    helper.saveStringValue(
                        PreferenceKeys.CURRENT_BOOKING_STATUS,
                        OrderStatus.ACCEPTED
                    )
                    startActivity(intent)
                    finish()
                }

                is ApiResponse.Error -> {
                    binding.loadingView.visibility = View.GONE
                    Toast.makeText(this, "Error: ${response.errorMessage}", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }

        viewModelOrder.apiResponseSingleBooking.observe(this) { response ->
            when (response) {
                is ApiResponse.Loading -> {
                    binding.loadingView.visibility = View.VISIBLE
                }

                is ApiResponse.Success -> {
                    binding.loadingView.visibility = View.GONE
                    response.data.data.let {
                        bookingDetails = it
                        setupUIWithBookingDetails(it)
                    }
                }

                is ApiResponse.Error -> {
                    binding.loadingView.visibility = View.GONE
                    Toast.makeText(this, response.errorMessage, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    @SuppressLint("SetTextI18n")
    private fun setupUIWithJob(job: Job) {
        val list = job.orderProducts
        bookingId = job.id.toString()
        binding.customerName.text = job.userDetails.name
        binding.driverShare.text = "Your Share : ${job.caregiverShare}"
        binding.total.text = "Total : ${job.amount}"

        list?.let {
            binding.rvOrderProducts.adapter = OrderItemAdapter(it)
        }

        binding.chatButton.setOnClickListener {
            val receiver = job.userDetails
            val sender = helper.getCurrentUser()
            if (receiver != null && sender != null) {
                checkOrStartChat(
                    senderId = sender.id.toString(),
                    senderName = sender.name ?: "",
                    senderImage = sender.profileImage ?: "",
                    receiverId = receiver.id.toString(),
                    receiverName = receiver.name ?: "",
                    receiverImage = receiver.profileImage ?: ""
                )
            }
        }

        binding.goButton.setOnClickListener {
            val isCurrentBooking = helper.getBooleanValue(PreferenceKeys.IS_CURRENT_BOOKING, false)
            if (!isCurrentBooking) {
                helper.getStringValue(PreferenceKeys.PREF_USER_TOKEN)?.let { token ->
                    viewModel.changeOrderStatus(token, job.id.toString(), OrderStatus.ACCEPTED)
                }

            } else {
                Toast.makeText(this, "You have an ongoing booking", Toast.LENGTH_SHORT).show()
            }
        }

        binding.noButton.setOnClickListener {
            finish()
        }
    }

    private fun setupUIWithBookingDetails(details: BookingDetails) {
        bookingId = details.id.toString()
        patientCardFront = details.patientDetails.cardFrontImage

        binding.customerName.text = details.patientDetails.name ?: ""
        binding.driverShare.text = "Your Share : ${details.caregiverShare}"
        binding.total.text = "Total : ${details.amount}"

        details.products.let {
            binding.rvOrderProducts.adapter = OrderProductAdapter(it)
        }

        binding.chatButton.setOnClickListener {
            val receiver = details.patientDetails
            val sender = helper.getCurrentUser()
            if (receiver != null && sender != null) {
                checkOrStartChat(
                    senderId = sender.id.toString(),
                    senderName = sender.name ?: "",
                    senderImage = sender.profileImage ?: "",
                    receiverId = receiver.id.toString(),
                    receiverName = receiver.name ?: "",
                    receiverImage = receiver.profileImage ?: ""
                )
            }
        }

        binding.goButton.setOnClickListener {
            helper.getStringValue(PreferenceKeys.PREF_USER_TOKEN)?.let { token ->
                viewModel.changeOrderStatus(token, details.id.toString(), OrderStatus.ACCEPTED)
            }
        }

        binding.noButton.setOnClickListener {
            finish()
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
                val intent = Intent(this@TaskDetailsActivity, ChatActivity::class.java)
                intent.putExtra(
                    "chatUser",
                    ChatUser(receiverId, receiverName, receiverImage, chatKey)
                )
                startActivity(intent)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    this@TaskDetailsActivity,
                    "Error: ${error.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

}