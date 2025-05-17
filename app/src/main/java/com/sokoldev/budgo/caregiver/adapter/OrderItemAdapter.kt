package com.sokoldev.budgo.caregiver.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sokoldev.budgo.R
import com.sokoldev.budgo.common.data.models.response.OrderProduct

class OrderItemAdapter(private val itemList: List<OrderProduct>?) :
    RecyclerView.Adapter<OrderItemAdapter.BookingViewHolder>() {
    private lateinit var context: Context

    inner class BookingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productName: AppCompatTextView = itemView.findViewById(R.id.product_name)
        val productType: AppCompatTextView = itemView.findViewById(R.id.product_type)
        val productPrice: AppCompatTextView = itemView.findViewById(R.id.product_price)
        val productQuantity: AppCompatTextView = itemView.findViewById(R.id.product_quantity)
        val productImage: AppCompatImageView = itemView.findViewById(R.id.product_image)
        val categoryName: AppCompatTextView = itemView.findViewById(R.id.category_name)
        val checkDetails: AppCompatTextView = itemView.findViewById(R.id.checkDetail)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingViewHolder {
        context = parent.context
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_orders, parent, false)
        return BookingViewHolder(itemView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: BookingViewHolder, position: Int) {

        val currentItem = itemList?.get(position)

        holder.checkDetails.visibility = View.GONE
        if (currentItem?.product != null) {
            currentItem.product.productImage?.let {
                loadImageWithBaseUrlFallback(holder.productImage, it)
            }
            currentItem.product.productName?.let { holder.productName.text = it }
            currentItem.product.category.let { holder.categoryName.text = it }
            currentItem.product.productType.let { holder.productType.text = it }
        }
        currentItem?.quantity.let { holder.productQuantity.text = it.toString() }
        currentItem?.amount.let { holder.productPrice.text = "$$it" }

    }


    override fun getItemCount() = itemList!!.size

    private fun loadImageWithBaseUrlFallback(imageView: AppCompatImageView, originalUrl: String) {
        // Sanitize the original URL by replacing spaces
        val safeOriginalUrl = originalUrl.replace(" ", "%20")

        // Replace base URL and sanitize the fallback as well
        val modifiedUrl = safeOriginalUrl.replace(
            "https://budgo.net/budgo/public/",
            "https://admin.budgo.net/"
        )

        // Build the fallback request with the safe original URL
        val fallbackRequest = Glide.with(imageView.context)
            .load(safeOriginalUrl)

        // Start primary request with fallback
        Glide.with(imageView.context)
            .load(modifiedUrl)
            .error(fallbackRequest) // Try this if the primary fails
            .into(imageView)
    }


}