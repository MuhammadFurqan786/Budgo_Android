package com.sokoldev.budgo.patient.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import com.sokoldev.budgo.R
import com.sokoldev.budgo.common.data.models.response.Brand
import com.sokoldev.budgo.common.data.models.response.Product
import com.sokoldev.budgo.common.data.models.response.TopSeller
import com.sokoldev.budgo.common.utils.Constants
import com.sokoldev.budgo.patient.ui.viewall.ViewAllActivity

class BrandAdapter(private val brandList: List<Brand>) :
    RecyclerView.Adapter<BrandAdapter.BrandViewHolder>() {
    private lateinit var context: Context

    inner class BrandViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val brandImage: ShapeableImageView = itemView.findViewById(R.id.image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BrandViewHolder {
        context = parent.context
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_brand, parent, false)
        return BrandViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: BrandViewHolder, position: Int) {
        val currentItem = brandList[position]
        currentItem.brandImage.let { Glide.with(context).load(it).into(holder.brandImage) }

        holder.itemView.setOnClickListener {
            val intent = Intent(context, ViewAllActivity::class.java)
            intent.putExtra(Constants.TYPE, Constants.BRAND)
            intent.putExtra(Constants.BRAND_ID, currentItem.id)
            intent.putExtra(Constants.BRAND_NAME, currentItem.brandName)
            context.startActivity(intent)
        }
    }


    override fun getItemCount() = brandList.size
}