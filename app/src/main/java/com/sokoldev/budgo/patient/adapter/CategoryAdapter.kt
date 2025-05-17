package com.sokoldev.budgo.patient.adapter

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
import com.sokoldev.budgo.common.utils.Constants
import com.sokoldev.budgo.patient.ui.viewall.ViewAllActivity

class CategoryAdapter(private val categoryList: List<com.sokoldev.budgo.common.data.models.response.Category>) :
    RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {
    private lateinit var context: Context

    inner class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val categoryName: AppCompatTextView = itemView.findViewById(R.id.category_name)
        val dispensaryName: AppCompatTextView = itemView.findViewById(R.id.dispensary_name)
        val categoryImage: AppCompatImageView = itemView.findViewById(R.id.category_image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        context = parent.context
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_category, parent, false)
        return CategoryViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val currentItem = categoryList[position]
        currentItem.categoryImage.let { loadImageWithBaseUrlFallback(holder.categoryImage,it) }
        currentItem.categoryName.let { holder.categoryName.text = it }
        currentItem.dispensory.let { holder.dispensaryName.text = it }

        holder.itemView.setOnClickListener {
            val intent = Intent(context, ViewAllActivity::class.java)
            intent.putExtra(Constants.TYPE,Constants.CATEGORY)
            intent.putExtra(Constants.CATEGORY_ID, currentItem.id)
            intent.putExtra(Constants.CATEGORY_NAME, currentItem.categoryName)
            context.startActivity(intent)
        }
    }


    override fun getItemCount() = categoryList.size


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