package com.sokoldev.budgo.common.data.models.response


import com.google.gson.annotations.SerializedName

data class MyBookingsData(
    @SerializedName("completed_bookings")
    val completedBookings: List<Booking>,
    @SerializedName("current_bookings")
    val currentBookings: List<Booking>
)