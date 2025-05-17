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
import com.sokoldev.budgo.R
import com.sokoldev.budgo.caregiver.ui.DashboardActivity
import com.sokoldev.budgo.caregiver.viewmodels.JobViewModel
import com.sokoldev.budgo.common.data.remote.network.ApiResponse
import com.sokoldev.budgo.common.utils.prefs.PreferenceHelper
import com.sokoldev.budgo.common.utils.prefs.PreferenceKeys
import com.sokoldev.budgo.databinding.ActivityFeedbackBinding
import com.sokoldev.budgo.patient.ui.home.HomeActivity

class FeedbackActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFeedbackBinding
    private val jobViewModel: JobViewModel by viewModels()
    private lateinit var helper: PreferenceHelper
    private var otherUserId: String? = null
    private var bookingId: String? = null

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityFeedbackBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        helper = PreferenceHelper.getPref(this)

        otherUserId = intent.getStringExtra("otherUserId")
        bookingId = intent.getStringExtra(PreferenceKeys.BOOKING_ID)


        binding.userRatingBar.setOnRatingBarChangeListener { ratingBar, rating, fromUser ->
            binding.rating.text = rating.toString()
        }

        binding.apply {

            back.setOnClickListener {
                finish()
            }

            doneButton.setOnClickListener {
                val rating = userRatingBar.rating
                val feedback = userFeedback.text.toString()

                if (userFeedback.text.toString().isEmpty()) {
                    userFeedback.error = "Please enter your feedback"
                    return@setOnClickListener
                }
                if (rating == 0f) {
                    Toast.makeText(
                        this@FeedbackActivity, "Please rate the user", Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }
                helper.getStringValue(PreferenceKeys.PREF_USER_TOKEN)?.let {
                    otherUserId?.let { it1 ->
                        bookingId?.let { it2 ->
                            jobViewModel.addReview(
                                token = it,
                                otherUserId = it1,
                                rating = rating.toString(),
                                review = feedback,
                                orderId = it2
                            )
                        }
                    }
                }
            }
        }

        initObserver()
    }

    private fun initObserver() {
        jobViewModel.reviewResponse.observe(this) {
            when (it) {
                is ApiResponse.Success -> {
                    binding.loadingView.visibility = View.GONE
                    Toast.makeText(this, "Review added successfully", Toast.LENGTH_SHORT).show()
                    if (helper.getCurrentUser()?.type == "1") {
                        startActivity(Intent(this@FeedbackActivity, HomeActivity::class.java))
                    } else {
                        startActivity(Intent(this@FeedbackActivity, DashboardActivity::class.java))
                    }

                    finish()
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
    }
}