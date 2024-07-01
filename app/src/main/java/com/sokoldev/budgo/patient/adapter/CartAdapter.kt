package com.sokoldev.budgo.patient.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.sokoldev.budgo.R
import com.sokoldev.budgo.patient.models.Cart

class CartAdapter(private val cartList: List<Cart>) :
    RecyclerView.Adapter<CartAdapter.CartViewHolder>() {
    private lateinit var context: Context

    inner class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productName: AppCompatTextView = itemView.findViewById(R.id.product_name)
        val productPrice: AppCompatTextView = itemView.findViewById(R.id.product_price)
        val productQuantity: AppCompatTextView = itemView.findViewById(R.id.quantity)
        val productImage: AppCompatImageView = itemView.findViewById(R.id.product_image)
        val buttonMinus: AppCompatImageView = itemView.findViewById(R.id.buttonMinus)
        val buttonPlus: AppCompatImageView = itemView.findViewById(R.id.buttonPlus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        context = parent.context
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_cart, parent, false)
        return CartViewHolder(itemView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {

        val currentItem = cartList[position]
        currentItem.productImage?.let { holder.productImage.setImageResource(it) }
        currentItem.productName?.let { holder.productName.text = it }
        currentItem.productPrice?.let { holder.productPrice.text = "$$it.00" }
        currentItem.productQuantity?.let { holder.productQuantity.text = it.toString() }

        holder.itemView.setOnClickListener {
//            val intent = Intent(context, DetailActivity::class.java)
//            intent.putExtra(GlobalKeys.IS_OFFER, GlobalKeys.NO)
//            intent.putExtra(GlobalKeys.EVENT, currentItem)
//            context.startActivity(intent)
        }
    }


    override fun getItemCount() = cartList.size
}