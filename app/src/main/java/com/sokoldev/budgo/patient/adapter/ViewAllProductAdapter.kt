package com.sokoldev.budgo.patient.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
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
import com.sokoldev.budgo.R
import com.sokoldev.budgo.common.data.models.response.Product
import com.sokoldev.budgo.patient.ui.menu.ProductDetailsActivity

class ViewAllProductAdapter(private val productList: List<Product>) :
    RecyclerView.Adapter<ViewAllProductAdapter.ProductViewHolder>() {
    private lateinit var context: Context

    inner class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productName: AppCompatTextView = itemView.findViewById(R.id.product_name)
        val dispensaryName: AppCompatTextView = itemView.findViewById(R.id.dispensary_name)
        val productType: AppCompatTextView = itemView.findViewById(R.id.dispensary_category)
        val productT: AppCompatTextView = itemView.findViewById(R.id.t_value)
        val productC: AppCompatTextView = itemView.findViewById(R.id.c_value)
        val productPrice: AppCompatTextView = itemView.findViewById(R.id.product_price)
        val productImage: AppCompatImageView = itemView.findViewById(R.id.product_image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        context = parent.context
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_product_view_all, parent, false)
        return ProductViewHolder(itemView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val currentItem = productList[position]
        currentItem.productImage?.let { loadImageWithBaseUrlFallback(holder.productImage, it) }
        currentItem.productName.let { holder.productName.text = it }
        currentItem.productType.let { holder.productType.text = it }
        currentItem.temperature.let { holder.productT.text = it }
        currentItem.condition.let { holder.productC.text = it }
        currentItem.productName.let { holder.productName.text = it }
        currentItem.productPrice.let { holder.productPrice.text = "$$it" }
        currentItem.dispensory?.dispensoryName.let { holder.dispensaryName.text = it }

        holder.itemView.setOnClickListener {
            val intent = Intent(context, ProductDetailsActivity::class.java)
            intent.putExtra("product",currentItem)
            context.startActivity(intent)
        }
    }


    override fun getItemCount() = productList.size

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