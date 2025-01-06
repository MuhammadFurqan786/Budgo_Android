package com.sokoldev.budgo.common.data.models.response


import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class Dispensory(
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("dispensory_name")
    val dispensoryName: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("latitude")
    val latitude: String,
    @SerializedName("location")
    val location: String,
    @SerializedName("longitude")
    val longitude: String,
    @SerializedName("updated_at")
    val updatedAt: String
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString()!!,   // createdAt
        parcel.readString()!!,   // dispensoryName
        parcel.readInt(),        // id
        parcel.readString()!!,   // latitude
        parcel.readString()!!,   // location
        parcel.readString()!!,   // longitude
        parcel.readString()!!    // updatedAt
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(createdAt)
        parcel.writeString(dispensoryName)
        parcel.writeInt(id)
        parcel.writeString(latitude)
        parcel.writeString(location)
        parcel.writeString(longitude)
        parcel.writeString(updatedAt)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Dispensory> {
        override fun createFromParcel(parcel: Parcel): Dispensory {
            return Dispensory(parcel)
        }

        override fun newArray(size: Int): Array<Dispensory?> {
            return arrayOfNulls(size)
        }
    }
}
