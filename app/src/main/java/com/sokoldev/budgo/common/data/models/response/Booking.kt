package com.sokoldev.budgo.common.data.models.response


import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Booking(
    @SerializedName("amount")
    val amount: String?,
    @SerializedName("amount_paid")
    val amountPaid: String?,
    @SerializedName("caregiver_id")
    val caregiverId: String?,
    @SerializedName("caregiver_share")
    val caregiverShare: String?,
    @SerializedName("company_share")
    val companyShare: String?,
    @SerializedName("created_at")
    val createdAt: String?,
    @SerializedName("currency")
    val currency: String?,
    @SerializedName("id")
    val id: Int,
    @SerializedName("order_status")
    val orderStatus: String?,
    @SerializedName("patient_id")
    val patientId: Int?,
    @SerializedName("products")
    val products: List<ProductItem?>?
) : Parcelable
