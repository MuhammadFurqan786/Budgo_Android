package com.sokoldev.budgo.patient.dialog

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.sokoldev.budgo.R
import com.sokoldev.budgo.caregiver.ui.task.OrderDetailsActivity

class BottomSheetDialog : BottomSheetDialogFragment() {

    private lateinit var markerLocation: LatLng

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.bottom_sheet_layout, container, false)
        val dispensaryName = v.findViewById<TextView>(R.id.dispenary_name)
        val productAvailable = v.findViewById<TextView>(R.id.product_available)
        val dispensaryDistance = v.findViewById<TextView>(R.id.dispensary_distance)
        val navigateButton = v.findViewById<RelativeLayout>(R.id.button_check)

        // Check if arguments are not null and retrieve data
        arguments?.let {
            markerLocation = it.getParcelable(ARG_LOCATION)!!
            dispensaryName.text = it.getString(ARG_TITLE)
            productAvailable.text = it.getString(ARG_PRODUCT_AVAILABLE)
            dispensaryDistance.text = it.getString(ARG_DISTANCE)
        }

        navigateButton.setOnClickListener {
            // Start OrderDetailsActivity or perform other actions with markerLocation
            startActivity(
                Intent(activity, OrderDetailsActivity::class.java)
                    .putExtra("latitude", markerLocation.latitude)
                    .putExtra("longitude", markerLocation.longitude)
            )
            dismiss()
        }
        return v
    }

    companion object {
        private const val ARG_TITLE = "title"
        private const val ARG_PRODUCT_AVAILABLE = "product_available"
        private const val ARG_DISTANCE = "distance"
        private const val ARG_LOCATION = "location"

        fun newInstance(
            title: String?,
            productAvailable: String?,
            distance: String?,
            location: LatLng
        ): BottomSheetDialog {
            val fragment = BottomSheetDialog()
            val args = Bundle().apply {
                putString(ARG_TITLE, title)
                putString(ARG_PRODUCT_AVAILABLE, productAvailable)
                putString(ARG_DISTANCE, distance)
                putParcelable(ARG_LOCATION, location)
            }
            fragment.arguments = args
            return fragment
        }
    }
}
