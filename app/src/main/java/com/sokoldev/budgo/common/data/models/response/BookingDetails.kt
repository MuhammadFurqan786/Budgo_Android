package com.sokoldev.budgo.common.data.models.response


import com.google.gson.annotations.SerializedName

data class BookingDetails(
    @SerializedName("amount")
    val amount: String,
    @SerializedName("amount_paid")
    val amountPaid: String,
    @SerializedName("caregiver_details")
    val caregiverDetails: PatientDetails,
    @SerializedName("caregiver_id")
    val caregiverId: Int,
    @SerializedName("caregiver_share")
    val caregiverShare: String,
    @SerializedName("company_share")
    val companyShare: String,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("currency")
    val currency: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("image")
    val image: String,
    @SerializedName("order_status")
    val orderStatus: String,
    @SerializedName("patient_details")
    val patientDetails: PatientDetails,
    @SerializedName("patient_id")
    val patientId: Int,
    @SerializedName("products")
    val products: List<ProductItem>,
    @SerializedName("reviews")
    val reviews: List<Review>?
)