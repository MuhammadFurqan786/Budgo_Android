package com.sokoldev.budgo.caregiver.ui.task

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
import com.sokoldev.budgo.caregiver.viewmodels.JobViewModel
import com.sokoldev.budgo.common.data.remote.network.ApiResponse
import com.sokoldev.budgo.common.utils.OrderStatus
import com.sokoldev.budgo.common.utils.prefs.PreferenceHelper
import com.sokoldev.budgo.common.utils.prefs.PreferenceKeys
import com.sokoldev.budgo.databinding.ActivityDropOffLocationBinding


class DropOffLocationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDropOffLocationBinding
    private var patientLat: Double? = null
    private var patientLon: Double? = null
    private var bookingId: String? = null
    private var patientId: String? = null
    private var caregiverId: String? = null
    private val viewModelJob: JobViewModel by viewModels()
    private lateinit var helper: PreferenceHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDropOffLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        helper = PreferenceHelper.getPref(this)
        patientLat = intent.getDoubleExtra("latitude", 0.0)
        patientLon = intent.getDoubleExtra("longitude", 0.0)
        bookingId = intent.getStringExtra(PreferenceKeys.BOOKING_ID)
        patientId = intent.getStringExtra("patientId")
        caregiverId = intent.getStringExtra("caregiverId")


        binding.back.setOnClickListener { finish() }
        binding.continueButton.setOnClickListener {

            helper.getStringValue(PreferenceKeys.PREF_USER_TOKEN)?.let {
                viewModelJob.changeOrderStatus(
                    it,
                    bookingId.toString(),
                    OrderStatus.ARRIVED
                )
            }


        }
        initObserver()
    }

    private fun initObserver() {
        viewModelJob.orderStatusResponse.observe(this) {
            when (it) {
                is ApiResponse.Success -> {
                    helper.saveStringValue(
                        PreferenceKeys.CURRENT_BOOKING_STATUS,
                        OrderStatus.ARRIVED
                    )
                    binding.loadingView.visibility = View.GONE
                    startActivity(
                        Intent(this, ToDoActivity::class.java)
                            .putExtra("latitude", patientLat)
                            .putExtra("longitude", patientLon)
                            .putExtra(PreferenceKeys.BOOKING_ID, bookingId)
                            .putExtra("patientId", patientId)
                            .putExtra("caregiverId", caregiverId)
                    )
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