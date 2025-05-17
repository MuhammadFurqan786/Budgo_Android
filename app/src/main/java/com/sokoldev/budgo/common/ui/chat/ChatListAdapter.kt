package com.sokoldev.budgo.common.ui.chat

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sokoldev.budgo.R
import com.sokoldev.budgo.common.data.models.ChatUser

class ChatListAdapter(
    private val chatList: List<ChatUser>,
    private val onItemClick: (ChatUser) -> Unit
) : RecyclerView.Adapter<ChatListAdapter.ChatViewHolder>() {

    inner class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: AppCompatTextView = itemView.findViewById(R.id.name)
        val lastMessage: AppCompatTextView = itemView.findViewById(R.id.lastMessage)
        val profileImage: ImageView = itemView.findViewById(R.id.profileImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_chat_list, parent, false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val user = chatList[position]
        holder.name.text = user.name
        holder.lastMessage.text = user.lastMessage

        // Bold text if unread
        holder.lastMessage.setTypeface(null, if (user.isRead) Typeface.NORMAL else Typeface.BOLD)

        user.profileImageUrl?.let { loadImageWithBaseUrlFallback(holder.profileImage, it) }

        holder.itemView.setOnClickListener { onItemClick(user) }
    }

    override fun getItemCount(): Int = chatList.size
    private fun loadImageWithBaseUrlFallback(imageView: ImageView, originalUrl: String) {
        // Sanitize the original URL by replacing spaces
        val safeOriginalUrl = originalUrl.replace(" ", "%20")

        // Replace base URL and sanitize the fallback as well
        val modifiedUrl = safeOriginalUrl.replace(
            "https://budgo.net/budgo/public/",
            "https://admin.budgo.net/"
        )

        // Build the fallback request with the safe original URL
        val fallbackRequest = Glide.with(imageView.context)
            .load(safeOriginalUrl)

        // Start primary request with fallback
        Glide.with(imageView.context)
            .load(modifiedUrl)
            .error(fallbackRequest) // Try this if the primary fails
            .into(imageView)
    }
}
