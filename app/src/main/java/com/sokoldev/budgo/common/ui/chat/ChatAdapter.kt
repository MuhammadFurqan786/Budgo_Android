package com.sokoldev.budgo.common.ui.chat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.imageview.ShapeableImageView
import com.sokoldev.budgo.R
import com.sokoldev.budgo.common.data.models.ChatMessage

class ChatAdapter(private val chatMessages: List<com.sokoldev.budgo.common.data.models.ChatMessage>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val VIEW_TYPE_SENDER = 1
        const val VIEW_TYPE_RECEIVER = 2
    }

    override fun getItemViewType(position: Int): Int {
        return if (chatMessages[position].isSender) {
            VIEW_TYPE_SENDER
        } else {
            VIEW_TYPE_RECEIVER
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_SENDER) {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.item_sender, parent, false)
            SenderViewHolder(view)
        } else {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.item_reciver, parent, false)
            ReceiverViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = chatMessages[position]
        if (holder is SenderViewHolder) {
            holder.bind(message)
        } else if (holder is ReceiverViewHolder) {
            holder.bind(message)
        }
    }

    override fun getItemCount(): Int {
        return chatMessages.size
    }

    class SenderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textMessage: TextView = itemView.findViewById(R.id.message)
        fun bind(chatMessage: com.sokoldev.budgo.common.data.models.ChatMessage) {
            textMessage.text = chatMessage.message
        }
    }

    class ReceiverViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textMessage: TextView = itemView.findViewById(R.id.message)
        private val profileImage: ShapeableImageView = itemView.findViewById(R.id.profile_image)
        fun bind(chatMessage: com.sokoldev.budgo.common.data.models.ChatMessage) {
            textMessage.text = chatMessage.message
            profileImage.setImageResource(chatMessage.profileImage)
        }
    }
}
