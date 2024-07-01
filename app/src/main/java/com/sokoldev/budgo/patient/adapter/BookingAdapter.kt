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
import com.sokoldev.budgo.R
import com.sokoldev.budgo.caregiver.ui.task.OrderDetailsActivity
import com.sokoldev.budgo.patient.models.Booking
import com.sokoldev.budgo.patient.ui.order.BookingDetailsActivity

class BookingAdapter(private val bookingList: List<Booking>) :
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
        currentItem.productImage?.let { holder.productImage.setImageResource(it) }
        currentItem.productName?.let { holder.productName.text = it }
        currentItem.categoryName?.let { holder.categoryName.text = it }
        currentItem.productPrice?.let { holder.productPrice.text = "$$it.00" }
        currentItem.productType?.let { holder.productType.text = it }
        currentItem.productQuantity?.let { holder.productQuantity.text = it.toString() }

        holder.itemView.setOnClickListener {
            val intent = Intent(context, BookingDetailsActivity::class.java)
            context.startActivity(intent)
        }
    }


    override fun getItemCount() = bookingList.size
}