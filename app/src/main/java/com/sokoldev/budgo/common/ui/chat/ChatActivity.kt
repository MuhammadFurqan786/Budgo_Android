package com.sokoldev.budgo.common.ui.chat

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.sokoldev.budgo.common.data.models.ChatMessage
import com.sokoldev.budgo.common.data.models.ChatUser
import com.sokoldev.budgo.common.utils.Global
import com.sokoldev.budgo.common.utils.prefs.PreferenceHelper
import com.sokoldev.budgo.databinding.ActivityChatBinding
import org.json.JSONObject

class ChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatBinding
    private lateinit var messageAdapter: ChatAdapter
    private val messages = mutableListOf<ChatMessage>()

    private lateinit var senderId: String
    private lateinit var receiverId: String
    private lateinit var chatKey: String
    private lateinit var receiverName: String
    private lateinit var receiverImage: String
    private lateinit var helper: PreferenceHelper

    private val dbRef = FirebaseDatabase.getInstance().reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        helper = PreferenceHelper.getPref(this)

        senderId = helper.getCurrentUser()?.id.toString()
        val chatUser = intent.getParcelableExtra<ChatUser>("chatUser") ?: return

        receiverId = chatUser.uid.toString()
        receiverName = chatUser.name.toString()
        receiverImage = chatUser.profileImageUrl.toString()
        chatKey = chatUser.chatKey.toString()

        setupRecyclerView()
        loadMessages()
        handleSendClick()
        binding.back.setOnClickListener { finish() }
    }

    private fun setupRecyclerView() {
        messageAdapter = ChatAdapter(messages, receiverImage, senderId)
        binding.rvChat.apply {
            layoutManager = LinearLayoutManager(this@ChatActivity)
            adapter = messageAdapter
        }
    }

    private fun loadMessages() {
        dbRef.child("chats").child(chatKey).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                messages.clear()
                for (snap in snapshot.children) {
                    val msg = snap.getValue(ChatMessage::class.java)
                    msg?.let { messages.add(it) }
                }
                messageAdapter.notifyDataSetChanged()
                binding.rvChat.scrollToPosition(messages.size - 1)
            }

            override fun onCancelled(error: DatabaseError) {
                Global.showErrorMessage(binding.root, error.message)
            }
        })
    }

    private fun handleSendClick() {
        binding.sendMessage.setOnClickListener {
            val msgText = binding.edMessage.text.toString().trim()
            if (msgText.isNotEmpty()) {
                val timestamp = System.currentTimeMillis()
                val chatMessage = ChatMessage(senderId, msgText, timestamp)

                // Save message
                dbRef.child("chats").child(chatKey).push().setValue(chatMessage)

                // Update chat list metadata
                dbRef.child("chat_list").child(senderId).child(receiverId).child("lastMessage")
                    .setValue(msgText)
                dbRef.child("chat_list").child(senderId).child(receiverId).child("timestamp")
                    .setValue(timestamp)
                dbRef.child("chat_list").child(receiverId).child(senderId).child("lastMessage")
                    .setValue(msgText)
                dbRef.child("chat_list").child(receiverId).child(senderId).child("timestamp")
                    .setValue(timestamp)

                // Optional: set isRead status
                dbRef.child("chat_list").child(receiverId).child(senderId).child("isRead")
                    .setValue(false)

                // Send notification
                sendNotification(receiverId, receiverName, msgText)

                binding.edMessage.text.clear()
            }
        }
    }

    private fun sendNotification(toUid: String, title: String, message: String) {
        FirebaseDatabase.getInstance().reference.child("users").child(toUid).child("fcmToken")
            .get()
            .addOnSuccessListener { snapshot ->
                val token = snapshot.value as? String ?: return@addOnSuccessListener
                val notification = JSONObject().apply {
                    put("to", token)
                    put("notification", JSONObject().apply {
                        put("title", title)
                        put("body", message)
                    })
                    put("data", JSONObject().apply {
                        put("chatKey", chatKey)
                        put("senderId", senderId)
                    })
                }
            }
    }

}