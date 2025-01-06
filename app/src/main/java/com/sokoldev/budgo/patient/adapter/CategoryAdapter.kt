package com.sokoldev.budgo.patient.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sokoldev.budgo.R

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
        currentItem.categoryImage.let { Glide.with(context).load(it).into(holder.categoryImage) }
        currentItem.categoryName.let { holder.categoryName.text = it }
        currentItem.dispensory.let { holder.dispensaryName.text = it }

        holder.itemView.setOnClickListener {
//            val intent = Intent(context, DetailActivity::class.java)
//            intent.putExtra(GlobalKeys.IS_OFFER, GlobalKeys.NO)
//            intent.putExtra(GlobalKeys.EVENT, currentItem)
//            context.startActivity(intent)
        }
    }


    override fun getItemCount() = categoryList.size
}