package com.sokoldev.budgo.common.data.models.response


import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName


data class Booking(
    @SerializedName("amount")
    val amount: String,
    @SerializedName("amount_paid")
    val amountPaid: String,
    @SerializedName("caregiver_id")
    val caregiverId: String,
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
    @SerializedName("order_status")
    val orderStatus: String,
    @SerializedName("patient_id")
    val patientId: Int,
    @SerializedName("products")
    val products: List<Product>
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString()!!,  // amount
        parcel.readString()!!,  // amountPaid
        parcel.readString()!!,  // caregiverId
        parcel.readString()!!,  // caregiverShare
        parcel.readString()!!,  // companyShare
        parcel.readString()!!,  // createdAt
        parcel.readString()!!,  // currency
        parcel.readInt(),       // id
        parcel.readString()!!,  // orderStatus
        parcel.readInt(),       // patientId
        parcel.createTypedArrayList(Product.CREATOR)!! // products
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(amount)
        parcel.writeString(amountPaid)
        parcel.writeString(caregiverId)
        parcel.writeString(caregiverShare)
        parcel.writeString(companyShare)
        parcel.writeString(createdAt)
        parcel.writeString(currency)
        parcel.writeInt(id)
        parcel.writeString(orderStatus)
        parcel.writeInt(patientId)
        parcel.writeTypedList(products)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Booking> {
        override fun createFromParcel(parcel: Parcel): Booking {
            return Booking(parcel)
        }

        override fun newArray(size: Int): Array<Booking?> {
            return arrayOfNulls(size)
        }
    }
}
