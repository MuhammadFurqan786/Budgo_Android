package com.sokoldev.budgo.common.ui.notification

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sokoldev.budgo.R
import com.sokoldev.budgo.patient.models.Notification

class NotificationViewModel : ViewModel() {
    private val _listNotification: MutableLiveData<List<Notification>> = MutableLiveData()
    val listNotification: LiveData<List<Notification>>
        get() = _listNotification


    fun getNotificationList() {
        val arraylist = ArrayList<Notification>()

        arraylist.add(
            Notification(
                R.drawable.user_image,
                "Check Driver Details",
                NotificationType.DRIVER_DETAILS
            )
        )

        arraylist.add(
            Notification(
                R.drawable.image_1,
                "Check Your Order Status",
                NotificationType.ORDER_DETAILS
            )
        )

        arraylist.add(
            Notification(
                R.drawable.user_image,
                "Check New Message",
                NotificationType.MESSAGE
            )
        )

        arraylist.add(
            Notification(
                R.drawable.image__2,
                "New Notification",
                NotificationType.OTHER
            )
        )
        _listNotification.value = arraylist
    }


}