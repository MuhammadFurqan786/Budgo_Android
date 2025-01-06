package com.sokoldev.budgo.common.data.models.response


import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class Product(
    @SerializedName("brand")
    val brand: String,
    @SerializedName("brand_id")
    val brandId: Int,
    @SerializedName("category")
    val category: String,
    @SerializedName("category_id")
    val categoryId: Int,
    @SerializedName("condition")
    val condition: String,
    @SerializedName("discount_percentage")
    val discountPercentage: String,
    @SerializedName("dispensory")
    val dispensory: Dispensory,
    @SerializedName("id")
    val id: Int,
    @SerializedName("price_currency")
    val priceCurrency: String,
    @SerializedName("product_description")
    val productDescription: String,
    @SerializedName("product_image")
    val productImage: String,
    @SerializedName("product_name")
    val productName: String,
    @SerializedName("product_nature")
    val productNature: String,
    @SerializedName("product_price")
    val productPrice: String,
    @SerializedName("product_type")
    val productType: String,
    @SerializedName("temperature")
    val temperature: String
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString()!!,  // brand
        parcel.readInt(),       // brandId
        parcel.readString()!!,  // category
        parcel.readInt(),       // categoryId
        parcel.readString()!!,  // condition
        parcel.readString()!!,  // discountPercentage
        parcel.readParcelable(Dispensory::class.java.classLoader)!!,  // dispensory
        parcel.readInt(),       // id
        parcel.readString()!!,  // priceCurrency
        parcel.readString()!!,  // productDescription
        parcel.readString()!!,  // productImage
        parcel.readString()!!,  // productName
        parcel.readString()!!,  // productNature
        parcel.readString()!!,  // productPrice
        parcel.readString()!!,  // productType
        parcel.readString()!!   // temperature
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(brand)
        parcel.writeInt(brandId)
        parcel.writeString(category)
        parcel.writeInt(categoryId)
        parcel.writeString(condition)
        parcel.writeString(discountPercentage)
        parcel.writeParcelable(dispensory, flags)
        parcel.writeInt(id)
        parcel.writeString(priceCurrency)
        parcel.writeString(productDescription)
        parcel.writeString(productImage)
        parcel.writeString(productName)
        parcel.writeString(productNature)
        parcel.writeString(productPrice)
        parcel.writeString(productType)
        parcel.writeString(temperature)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Product> {
        override fun createFromParcel(parcel: Parcel): Product {
            return Product(parcel)
        }

        override fun newArray(size: Int): Array<Product?> {
            return arrayOfNulls(size)
        }
    }
}
