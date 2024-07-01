package com.sokoldev.budgo.common.ui.notification

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.sokoldev.budgo.R
import com.sokoldev.budgo.databinding.ActivityNotificationBinding
import com.sokoldev.budgo.patient.adapter.NotificationAdapter

class NotificationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNotificationBinding
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
        val notificationViewModel = ViewModelProvider(this)[NotificationViewModel::class.java]
        notificationViewModel.getNotificationList()

        initObserver(notificationViewModel)


    }

    private fun initObserver(notificationViewModel: NotificationViewModel) {
        notificationViewModel.listNotification.observe(this) {
            val adapter = NotificationAdapter(it,supportFragmentManager)
            binding.rvNotification.adapter = adapter
        }

    }

}