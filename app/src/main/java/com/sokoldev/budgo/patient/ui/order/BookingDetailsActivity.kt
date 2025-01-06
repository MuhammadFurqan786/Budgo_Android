package com.sokoldev.budgo.patient.ui.order

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.sokoldev.budgo.common.data.models.response.Booking
import com.sokoldev.budgo.common.ui.chat.ChatActivity
import com.sokoldev.budgo.databinding.ActivityBookingDetailsBinding

class BookingDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBookingDetailsBinding
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

        val booking: Booking? = intent.getParcelableExtra("booking")
        if (booking != null) {
            binding.apply {
                booking.products[0].productImage.let {
                    Glide.with(this@BookingDetailsActivity).load(it).into(productImage)
                }
                booking.amount.let { productPrice.text = "$$it" }
                booking.products[0].productName.let { productName.text = it }
                booking.products[0].productDescription.let { productDesc.text = it }

            }
        }



        binding.apply {

            back.setOnClickListener { finish() }
            buttonChat.setOnClickListener {
                startActivity(Intent(this@BookingDetailsActivity, ChatActivity::class.java))
            }

        }
    }
}