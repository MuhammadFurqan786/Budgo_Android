package com.sokoldev.budgo.common.ui.chat

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.sokoldev.budgo.common.data.models.ChatUser
import com.sokoldev.budgo.common.data.models.response.User
import com.sokoldev.budgo.common.utils.Global
import com.sokoldev.budgo.common.utils.prefs.PreferenceHelper
import com.sokoldev.budgo.databinding.ActivityChatListBinding

class ChatListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatListBinding
    private lateinit var adapter: ChatListAdapter
    private val chatList = mutableListOf<ChatUser>()
    private lateinit var helper: PreferenceHelper
    private lateinit var user: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        helper = PreferenceHelper.getPref(this)
        user = helper.getCurrentUser()!!

        adapter = ChatListAdapter(chatList) { user ->
            val intent = Intent(this, ChatActivity::class.java)
            intent.putExtra("chatUser", user)
            startActivity(intent)
        }
        binding.rvChatList.adapter = adapter

        loadChatList()
    }

    private fun loadChatList() {
        val dbRef =
            FirebaseDatabase.getInstance().getReference("chat_list").child(user.id.toString())

        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                chatList.clear()
                if (snapshot.exists()) {
                    for (child in snapshot.children) {
                        val user = child.getValue(ChatUser::class.java)
                        user?.uid = child.key
                        user?.let { chatList.add(it) }
                    }
                    chatList.sortByDescending { it.timestamp }
                    adapter.notifyDataSetChanged()
                    binding.noDataFound.visibility = View.GONE
                    binding.rvChatList.visibility = View.VISIBLE
                } else {
                    binding.noDataFound.visibility = View.VISIBLE
                    binding.rvChatList.visibility = View.GONE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Global.showMessage(binding.root, error.message)
            }
        })
    }
}
