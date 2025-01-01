package com.sokoldev.budgo.common.data.models.response


import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("caregiver_card_back_side")
    val caregiverCardBackSide: String,
    @SerializedName("caregiver_card_front_side")
    val caregiverCardFrontSide: String,
    @SerializedName("caregiver_type")
    val caregiverType: String,
    @SerializedName("device_type")
    val deviceType: String,
    @SerializedName("dob")
    val dob: String,
    @SerializedName("driving_license_back_side")
    val drivingLicenseBackSide: String,
    @SerializedName("driving_license_front_side")
    val drivingLicenseFrontSide: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("is_online")
    val isOnline: Int,
    @SerializedName("is_verified")
    val isVerified: Int,
    @SerializedName("latitude")
    val latitude: String,
    @SerializedName("location")
    val location: String,
    @SerializedName("longitude")
    val longitude: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("patient_card_back_side")
    val patientCardBackSide: String,
    @SerializedName("patient_card_front_side")
    val patientCardFrontSide: String,
    @SerializedName("phone")
    val phone: String,
    @SerializedName("profile_image")
    val profileImage: String,
    @SerializedName("token")
    val token: Any,
    @SerializedName("type")
    val type: String
)