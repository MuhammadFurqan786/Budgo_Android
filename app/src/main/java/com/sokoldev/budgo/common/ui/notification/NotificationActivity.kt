package com.sokoldev.budgo.common.ui.notification

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.sokoldev.budgo.R
import com.sokoldev.budgo.common.data.remote.network.ApiResponse
import com.sokoldev.budgo.common.utils.prefs.PreferenceHelper
import com.sokoldev.budgo.common.utils.prefs.PreferenceKeys
import com.sokoldev.budgo.common.viewmodels.NotificationViewModel
import com.sokoldev.budgo.databinding.ActivityNotificationBinding
import kotlinx.coroutines.launch

class NotificationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNotificationBinding
    private val viewModel: NotificationViewModel by viewModels()
    private lateinit var helper: PreferenceHelper
    private lateinit var adapter: NotificationAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityNotificationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding.back.setOnClickListener { finish() }
        helper = PreferenceHelper.getPref(this)
        helper.getStringValue(PreferenceKeys.PREF_USER_TOKEN)?.let {
            viewModel.getNotifications(it)
        }
        adapter = NotificationAdapter()
        binding.rvNotification.adapter = adapter
        initObserver()
    }

    private fun initObserver() {
        // 4. Observe the result
        lifecycleScope.launch {
            viewModel.notificationState.collect { response ->
                when (response) {
                    is ApiResponse.Loading -> {
                        binding.loadingView.visibility = View.VISIBLE
                        // Show loading state (e.g., progress bar)
                    }

                    is ApiResponse.Success -> {
                        binding.loadingView.visibility = View.GONE
                        // Handle the success response
                        val pagingData = response.data
                        // Submit this data to your PagingDataAdapter
                        adapter.submitData(pagingData)

                    }

                    is ApiResponse.Error -> {
                        // Show error message
                        binding.loadingView.visibility = View.GONE
                        Toast.makeText(
                            this@NotificationActivity,
                            response.errorMessage,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }

    }

}