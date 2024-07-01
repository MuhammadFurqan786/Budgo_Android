package com.sokoldev.budgo.patient.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.imageview.ShapeableImageView
import com.sokoldev.budgo.R
import com.sokoldev.budgo.patient.models.Brand

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
        currentItem.brandImage?.let { holder.brandImage.setImageResource(it) }

        holder.itemView.setOnClickListener {
//            val intent = Intent(context, DetailActivity::class.java)
//            intent.putExtra(GlobalKeys.IS_OFFER, GlobalKeys.NO)
//            intent.putExtra(GlobalKeys.EVENT, currentItem)
//            context.startActivity(intent)
        }
    }


    override fun getItemCount() = brandList.size
}