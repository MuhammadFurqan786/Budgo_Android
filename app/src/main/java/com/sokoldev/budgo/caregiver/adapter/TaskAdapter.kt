package com.sokoldev.budgo.caregiver.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.sokoldev.budgo.R
import com.sokoldev.budgo.caregiver.models.Task
import com.sokoldev.budgo.caregiver.ui.task.TaskDetailsActivity


class TaskAdapter(
    private val taskList: List<Task>,
    private val itemClickListener: ItemClickListener
) :
    RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {
    private lateinit var context: Context

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productName: AppCompatTextView = itemView.findViewById(R.id.product_name)
        val customerName: AppCompatTextView = itemView.findViewById(R.id.name)
        val distance: AppCompatTextView = itemView.findViewById(R.id.job_distance)
        val taskPrice: AppCompatTextView = itemView.findViewById(R.id.job_price)
        val productQuantity: AppCompatTextView = itemView.findViewById(R.id.product_quantity)
        val productImage: AppCompatImageView = itemView.findViewById(R.id.job_image)
        val checkDetailsButton: RelativeLayout = itemView.findViewById(R.id.button_check)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        context = parent.context
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_job, parent, false)
        return TaskViewHolder(itemView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {

        val currentItem = taskList[position]
        currentItem.image.let { holder.productImage.setImageResource(it) }
        currentItem.productName.let { holder.productName.text = it }
        currentItem.customerName.let { holder.customerName.text = it }
        currentItem.price.let { holder.taskPrice.text = "$$it.00" }
        currentItem.distance.let { holder.distance.text = it }
        currentItem.quantity.let { holder.productQuantity.text = it.toString() }

        holder.itemView.setOnClickListener {
            itemClickListener.onItemClicked(currentItem)
        }
        holder.checkDetailsButton.setOnClickListener {
            val intent = Intent(context, TaskDetailsActivity::class.java)
            context.startActivity(intent)
        }
    }

    interface ItemClickListener {
        fun onItemClicked(task: Task)
    }

    override fun getItemCount() = taskList.size
}