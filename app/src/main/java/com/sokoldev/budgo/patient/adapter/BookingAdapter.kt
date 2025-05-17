package com.sokoldev.budgo.patient.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sokoldev.budgo.R
import com.sokoldev.budgo.caregiver.ui.task.DispensariesActivity
import com.sokoldev.budgo.caregiver.ui.task.TaskDetailsActivity
import com.sokoldev.budgo.common.data.models.response.Booking
import com.sokoldev.budgo.common.utils.OrderStatus
import com.sokoldev.budgo.common.utils.prefs.PreferenceKeys.Companion.BOOKING
import com.sokoldev.budgo.common.utils.prefs.PreferenceKeys.Companion.BOOKING_ID
import com.sokoldev.budgo.common.utils.prefs.PreferenceKeys.Companion.FROM_BOOKING
import com.sokoldev.budgo.patient.ui.order.BookingDetailsActivity

class BookingAdapter(private val bookingList: List<Booking>, private val isPatient: Boolean) :
    RecyclerView.Adapter<BookingAdapter.BookingViewHolder>() {
    private lateinit var context: Context

    inner class BookingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productName: AppCompatTextView = itemView.findViewById(R.id.product_name)
        val productType: AppCompatTextView = itemView.findViewById(R.id.product_type)
        val productPrice: AppCompatTextView = itemView.findViewById(R.id.product_price)
        val productQuantity: AppCompatTextView = itemView.findViewById(R.id.product_quantity)
        val productImage: AppCompatImageView = itemView.findViewById(R.id.product_image)
        val categoryName: AppCompatTextView = itemView.findViewById(R.id.category_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingViewHolder {
        context = parent.context
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_orders, parent, false)
        return BookingViewHolder(itemView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: BookingViewHolder, position: Int) {

        val currentItem = bookingList[position]

        if (currentItem.products?.isNotEmpty() == true) {
            currentItem.products[0]?.productImage?.let {
                loadImageWithBaseUrlFallback(holder.productImage, it)
            }
            currentItem.products[0]?.productName.let { holder.productName.text = it }
            currentItem.products[0]?.category.let { holder.categoryName.text = it }
            currentItem.products[0]?.productType.let { holder.productType.text = it }
            currentItem.products.size.let { holder.productQuantity.text = it.toString() }
        }
        currentItem.amount.let { holder.productPrice.text = "$$it" }

        holder.itemView.setOnClickListener {
            if (isPatient) {
                val intent = Intent(context, BookingDetailsActivity::class.java)
                intent.putExtra(BOOKING, currentItem)
                context.startActivity(intent)
            } else {
                if (currentItem.orderStatus == OrderStatus.ACCEPTED) {
                    val intent = Intent(context, DispensariesActivity::class.java)
                    intent.putExtra(BOOKING_ID, currentItem.id.toString())
                    context.startActivity(intent)
                } else if (currentItem.orderStatus == OrderStatus.COMPLETED) {
                    val intent = Intent(context, BookingDetailsActivity::class.java)
                    intent.putExtra(BOOKING, currentItem)
                    context.startActivity(intent)
                } else {
                    val intent = Intent(context, TaskDetailsActivity::class.java)
                    intent.putExtra(BOOKING_ID, currentItem.id.toString())
                    intent.putExtra(FROM_BOOKING, true)
                    context.startActivity(intent)
                }
            }
        }
    }


    override fun getItemCount() = bookingList.size

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