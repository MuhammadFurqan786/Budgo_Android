package com.sokoldev.budgo.caregiver.ui.task

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.sokoldev.budgo.common.ui.chat.ChatActivity
import com.sokoldev.budgo.databinding.ActivityTaskDetailsBinding


class TaskDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTaskDetailsBinding
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


        binding.apply {
            chatButton.setOnClickListener {
                startActivity(Intent(this@TaskDetailsActivity, ChatActivity::class.java))
            }

            noButton.setOnClickListener {
                finish()
            }
            goButton.setOnClickListener {
                startActivity(Intent(this@TaskDetailsActivity, DispensariesActivity::class.java))
            }
            back.setOnClickListener { finish() }
        }
    }
}