package com.sokoldev.budgo.patient.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.sokoldev.budgo.R
import com.sokoldev.budgo.caregiver.ui.DashboardActivity
import com.sokoldev.budgo.caregiver.ui.task.OrderDetailsActivity
import com.sokoldev.budgo.common.ui.chat.ChatActivity
import com.sokoldev.budgo.common.ui.notification.NotificationType
import com.sokoldev.budgo.common.utils.prefs.PreferenceHelper
import com.sokoldev.budgo.patient.models.Notification
import com.sokoldev.budgo.patient.ui.home.HomeActivity
import com.sokoldev.budgo.patient.ui.order.BookingDetailsActivity
import com.sokoldev.budgo.patient.utils.dialog.DriverDialogFragment

class NotificationAdapter(
    private val notificationList: List<Notification>,
   private val supportFragmentManager: FragmentManager
) :
    RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {
    private lateinit var helper: PreferenceHelper
    private lateinit var context: Context

    inner class NotificationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val notificationMessage: AppCompatTextView =
            itemView.findViewById(R.id.notification_message)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        context = parent.context
        helper = PreferenceHelper.getPref(context)
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_notification, parent, false)
        return NotificationViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        val currentItem = notificationList[position]
        currentItem.notificationMessage.let { holder.notificationMessage.text = it }

        holder.itemView.setOnClickListener {

            val notificationType = currentItem.notificationType
            when (notificationType) {
                NotificationType.DRIVER_DETAILS -> {
                    val dialogFragment = DriverDialogFragment()
                    dialogFragment.show(supportFragmentManager, "CustomDialog")
                }

                NotificationType.ORDER_DETAILS -> {
                    val isPatient = helper.isPatientUser()
                    if (isPatient) {
                        val intent = Intent(context, BookingDetailsActivity::class.java)
                        context.startActivity(intent)
                    } else {
                        val intent = Intent(context, OrderDetailsActivity::class.java)
                        context.startActivity(intent)
                    }
                }

                NotificationType.MESSAGE -> {
                    val intent = Intent(context, ChatActivity::class.java)
                    context.startActivity(intent)
                }

                NotificationType.OTHER -> {
                    val isPatient = helper.isPatientUser()
                    if (isPatient) {
                        val intent = Intent(context, HomeActivity::class.java)
                        context.startActivity(intent)
                    } else {
                        val intent = Intent(context, DashboardActivity::class.java)
                        context.startActivity(intent)
                    }
                }

                else -> {
                    val isPatient = helper.isPatientUser()
                    if (isPatient) {
                        val intent = Intent(context, HomeActivity::class.java)
                        context.startActivity(intent)
                    } else {
                        val intent = Intent(context, DashboardActivity::class.java)
                        context.startActivity(intent)
                    }
                }
            }
        }
    }


    override fun getItemCount() = notificationList.size
}