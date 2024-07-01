package com.sokoldev.budgo.common.ui.chat

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.sokoldev.budgo.R
import com.sokoldev.budgo.common.viewmodels.ChatViewModel
import com.sokoldev.budgo.databinding.ActivityChatBinding

class ChatActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val chatViewModel = ViewModelProvider(this)[ChatViewModel::class.java]
        chatViewModel.getAllMessages()

        initObservers(chatViewModel)

        binding.back.setOnClickListener { finish() }
    }

    private fun initObservers(chatViewModel: ChatViewModel) {
        chatViewModel.listChat.observe(this) {
            val adapter = ChatAdapter(it)
            binding.rvChat.layoutManager = LinearLayoutManager(this)
            binding.rvChat.adapter = adapter
        }
    }
}