package com.sokoldev.budgo.common.ui.chat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import com.sokoldev.budgo.R
import com.sokoldev.budgo.common.data.models.ChatMessage

class ChatAdapter(
    private val messages: List<ChatMessage>,
    private val currentUserId: String,
    private val profileImage: String,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val VIEW_TYPE_SENDER = 1
    private val VIEW_TYPE_RECEIVER = 2

    override fun getItemViewType(position: Int): Int {
        return if (messages[position].senderId == currentUserId) VIEW_TYPE_SENDER else VIEW_TYPE_RECEIVER
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == VIEW_TYPE_SENDER) {
            val view = inflater.inflate(R.layout.item_sender, parent, false)
            SenderViewHolder(view)
        } else {
            val view = inflater.inflate(R.layout.item_reciver, parent, false)
            ReceiverViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val msg = messages[position]
        if (holder is SenderViewHolder) {
            holder.msg.text = msg.message
        } else if (holder is ReceiverViewHolder) {
            holder.msg.text = msg.message
            loadImageWithBaseUrlFallback(holder.profileImage, profileImage)
        }
    }

    override fun getItemCount(): Int = messages.size

    class SenderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val msg: AppCompatTextView = view.findViewById(R.id.message)
    }

    class ReceiverViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val msg: AppCompatTextView = view.findViewById(R.id.message)
        val profileImage: ShapeableImageView = view.findViewById(R.id.profile_image)
    }

    private fun loadImageWithBaseUrlFallback(imageView: ShapeableImageView, originalUrl: String) {
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

