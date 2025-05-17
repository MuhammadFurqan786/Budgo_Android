package com.sokoldev.budgo.common.data.models.response


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Job(
    @SerializedName("amount")
    val amount: String,
    @SerializedName("amount_paid")
    val amountPaid: String,
    @SerializedName("caregiver_id")
    val caregiverId: String?,
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
    @SerializedName("order_products")
    val orderProducts: List<OrderProduct>?,
    @SerializedName("order_status")
    val orderStatus: String,
    @SerializedName("patient_id")
    val patientId: Int,
    @SerializedName("user_details")
    val userDetails: User
):Parcelable