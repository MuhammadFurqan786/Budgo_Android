package com.sokoldev.budgo.patient.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import com.sokoldev.budgo.R
import com.sokoldev.budgo.common.data.models.response.Product

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
        currentItem.productImage.let { Glide.with(context).load(it).into(holder.edibleImage) }
        currentItem.productName.let { holder.edibleName.text = it }
        currentItem.productPrice.let { holder.ediblePrice.text = "$$it.00" }
        currentItem.dispensory.dispensoryName.let { holder.edibleDesc.text = it }

        holder.itemView.setOnClickListener {
//            val intent = Intent(context, DetailActivity::class.java)
//            intent.putExtra(GlobalKeys.IS_OFFER, GlobalKeys.NO)
//            intent.putExtra(GlobalKeys.EVENT, currentItem)
//            context.startActivity(intent)
        }
    }


    override fun getItemCount() = productList.size
}