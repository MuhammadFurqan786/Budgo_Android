package com.sokoldev.budgo.patient.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.material.imageview.ShapeableImageView
import com.sokoldev.budgo.R
import com.sokoldev.budgo.common.data.local.CartItem
import com.sokoldev.budgo.patient.models.Cart
import com.sokoldev.budgo.patient.ui.cart.CartViewModel


class CartAdapter(private var cartList: List<CartItem>, private val viewModel: CartViewModel) :
    RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    private lateinit var context: Context

    inner class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productName: AppCompatTextView = itemView.findViewById(R.id.product_name)
        val productPrice: AppCompatTextView = itemView.findViewById(R.id.product_price)
        val productQuantity: AppCompatTextView = itemView.findViewById(R.id.quantity)
        val productImage: AppCompatImageView = itemView.findViewById(R.id.product_image)
        val buttonMinus: AppCompatImageView = itemView.findViewById(R.id.buttonMinus)
        val buttonPlus: AppCompatImageView = itemView.findViewById(R.id.buttonPlus)
        val deleteButton: AppCompatImageView = itemView.findViewById(R.id.deleteButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        context = parent.context
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_cart, parent, false)
        return CartViewHolder(itemView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val currentItem = cartList[position]

        // Load product image
        currentItem.productImage.let { loadImageWithBaseUrlFallback(holder.productImage,it) }
        holder.productName.text = currentItem.productName
        holder.productQuantity.text = currentItem.quantity.toString()

        // Calculate total price
        val totalPrice = currentItem.productPrice * currentItem.quantity
        holder.productPrice.text = "$$totalPrice.00"

        // Set button listeners
        holder.buttonPlus.setOnClickListener {
            viewModel.updateQuantity(currentItem.productId, currentItem.quantity + 1)
        }

        holder.buttonMinus.setOnClickListener {
            if (currentItem.quantity > 1) {
                viewModel.updateQuantity(currentItem.productId, currentItem.quantity - 1)
            } else {
                viewModel.removeFromCart(currentItem.productId) // Remove item if quantity is 0
            }
        }

        holder.itemView.setOnClickListener {
            // You can add any click handling for item view here, e.g., open a detailed view
        }

        holder.deleteButton.setOnClickListener {
            viewModel.removeFromCart(currentItem.productId)
        }
    }

    override fun getItemCount(): Int {
        return cartList.size
    }

    // Method to update the cart list in the adapter
    fun updateCartItems(newCartItems: List<CartItem>) {
        cartList = newCartItems
        notifyDataSetChanged() // Notify the adapter to refresh the views
    }

    fun getCartItems(): List<CartItem> {
        return cartList
    }
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
