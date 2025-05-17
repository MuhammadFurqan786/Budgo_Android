package com.sokoldev.budgo.caregiver.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.location.Location
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.sokoldev.budgo.R
import com.sokoldev.budgo.caregiver.ui.task.TaskDetailsActivity
import com.sokoldev.budgo.common.data.models.response.Job

class TaskAdapter(
    private val lat1: Double,
    private val lon1: Double,
    private val taskList: List<Job>,
    private val itemClickListener: ItemClickListener
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

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
        val itemView = LayoutInflater.from(context).inflate(R.layout.item_job, parent, false)
        return TaskViewHolder(itemView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val currentItem = taskList[position]

        // Load image with fallback
        val imageUrl = currentItem.orderProducts?.getOrNull(0)?.product?.productImage
        if (!imageUrl.isNullOrEmpty()) {
            loadImageWithBaseUrlFallback(holder.productImage, imageUrl)
        }

        holder.productName.text =
            currentItem.orderProducts?.getOrNull(0)?.product?.productName ?: ""
        holder.customerName.text = currentItem.userDetails.name ?: ""
        holder.taskPrice.text = "$${currentItem.amount}"

        // Calculate distance
        val results = FloatArray(1)
        currentItem.userDetails.latitude?.let {
            currentItem.userDetails.longitude?.let { lon ->
                Location.distanceBetween(lat1, lon1, it.toDouble(), lon.toDouble(), results)
            }
        }
        val distanceInKilometers = results[0] / 1000
        holder.distance.text = "%.2f KM".format(distanceInKilometers)

        holder.productQuantity.text = currentItem.orderProducts?.size.toString()

        holder.itemView.setOnClickListener {
            itemClickListener.onItemClicked(currentItem)
        }

        holder.checkDetailsButton.setOnClickListener {
            val intent = Intent(context, TaskDetailsActivity::class.java)
            intent.putExtra("job", currentItem)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = taskList.size

    interface ItemClickListener {
        fun onItemClicked(job: Job)
    }

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
