package com.sokoldev.budgo.common.data.models.response


import com.google.gson.annotations.SerializedName

data class PatientDetails(
    @SerializedName("id")
    val id: Int,
    @SerializedName("latitude")
    val latitude: String,
    @SerializedName("location")
    val location: String,
    @SerializedName("longitude")
    val longitude: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("phone")
    val phone: String,
    @SerializedName("profile_image")
    val profileImage: String,
    @SerializedName("patient_card_front_side")
    val cardFrontImage: String,
    @SerializedName("patient_card_back_side")
    val cardBackImage: String

)