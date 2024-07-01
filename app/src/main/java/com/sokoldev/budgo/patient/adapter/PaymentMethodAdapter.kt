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
import com.sokoldev.budgo.patient.models.Payment

class PaymentMethodAdapter(private val paymentMethodList: List<Payment>) :
    RecyclerView.Adapter<PaymentMethodAdapter.PaymentViewHolder>() {
    private lateinit var context: Context

    private var selectedPosition = -1

    inner class PaymentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardImage: AppCompatImageView = itemView.findViewById(R.id.payment_image)
        val cardName: AppCompatTextView = itemView.findViewById(R.id.paymentType)
        val cardDetails: AppCompatTextView = itemView.findViewById(R.id.payment_number)
        val editButton: AppCompatImageView = itemView.findViewById(R.id.editButton)
        val deleteButton: AppCompatImageView = itemView.findViewById(R.id.deleteButton)
        val selectedButton: AppCompatImageView = itemView.findViewById(R.id.selected)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentViewHolder {
        context = parent.context
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_payment_card, parent, false)
        return PaymentViewHolder(itemView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(
        holder: PaymentViewHolder,
        @SuppressLint("RecyclerView") position: Int
    ) {

        val currentItem = paymentMethodList[position]
        currentItem.cardImage.let { holder.cardImage.setImageResource(it) }
        currentItem.cardName.let { holder.cardName.text = it }
        currentItem.cardDetails.let { holder.cardDetails.text = it }
        currentItem.isSelected = position == selectedPosition
        holder.selectedButton.visibility =
            if (position == selectedPosition) View.VISIBLE else View.GONE
        holder.itemView.setOnClickListener {
            if (selectedPosition != position) {
                notifyItemChanged(selectedPosition)
                selectedPosition = position
                notifyItemChanged(selectedPosition)
            }
        }
    }

        override fun getItemCount() = paymentMethodList.size

        fun getSelectedItem(): Payment? {
            return if (selectedPosition >= 0) paymentMethodList[selectedPosition] else null
        }
    }