package com.sokoldev.budgo.patient.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.graphics.drawable.shapes.Shape
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
import com.sokoldev.budgo.common.data.models.response.Product
import com.sokoldev.budgo.patient.ui.menu.ProductDetailsActivity

class EdiblesAdapter(private val productList: List<Product>) :
    RecyclerView.Adapter<EdiblesAdapter.ProductViewHolder>() {
    private lateinit var context: Context

    inner class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val edibleName: AppCompatTextView = itemView.findViewById(R.id.edible_name)
        val edibleDesc: AppCompatTextView = itemView.findViewById(R.id.edible_desc)
        val ediblePrice: AppCompatTextView = itemView.findViewById(R.id.edible_price)
        val edibleImage: ShapeableImageView = itemView.findViewById(R.id.edible_image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        context = parent.context
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_edibels, parent, false)
        return ProductViewHolder(itemView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val currentItem = productList[position]
        currentItem.productImage?.let { loadImageWithBaseUrlFallback(holder.edibleImage,it) }
        currentItem.productName.let { holder.edibleName.text = it }
        currentItem.productPrice.let { holder.ediblePrice.text = "$$it" }
        currentItem.dispensory?.dispensoryName.let { holder.edibleDesc.text = it }

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