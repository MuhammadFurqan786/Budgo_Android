package com.sokoldev.budgo.common.ui.notification

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.sokoldev.budgo.R
import com.sokoldev.budgo.common.data.models.NotificationItem
import com.sokoldev.budgo.common.utils.Global.toReadableFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NotificationAdapter :
    PagingDataAdapter<NotificationItem, NotificationAdapter.NotificationViewHolder>(
        NotificationDiffCallback()
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_notification, parent, false)
        return NotificationViewHolder(view)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        val item = getItem(position)
        item?.let { holder.bind(it) }
    }

    inner class NotificationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvNotificationTitle: TextView = itemView.findViewById(R.id.tvTitle)
        private val tvNotificationText: TextView = itemView.findViewById(R.id.tvNotificationText)
        private val tvNotificationTime: TextView = itemView.findViewById(R.id.tvNotificationTime)

        fun bind(item: NotificationItem) {

            tvNotificationTitle.text = item.notificationType.toReadableFormat()
            tvNotificationText.text = item.notification

            // Convert UNIX time to readable format
            val date = Date(item.notificationTime * 1000.toLong())
            val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
            tvNotificationTime.text = sdf.format(date)
        }
    }

    class NotificationDiffCallback : DiffUtil.ItemCallback<NotificationItem>() {
        override fun areItemsTheSame(
            oldItem: NotificationItem,
            newItem: NotificationItem
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: NotificationItem,
            newItem: NotificationItem
        ): Boolean {
            return oldItem == newItem
        }
    }
}
