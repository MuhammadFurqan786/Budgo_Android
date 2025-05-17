package com.sokoldev.budgo.patient.dialog

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.sokoldev.budgo.R
import com.sokoldev.budgo.caregiver.ui.task.OrderDetailsActivity
import com.sokoldev.budgo.caregiver.viewmodels.JobViewModel
import com.sokoldev.budgo.common.data.models.response.Dispensory
import com.sokoldev.budgo.common.data.remote.network.ApiResponse
import com.sokoldev.budgo.common.utils.OrderStatus
import com.sokoldev.budgo.common.utils.prefs.PreferenceHelper
import com.sokoldev.budgo.common.utils.prefs.PreferenceKeys

class BottomSheetDialog : BottomSheetDialogFragment() {

    private lateinit var markerLocation: LatLng
    private var selectedDispensory: Dispensory? = null
    private val viewModel: JobViewModel by viewModels()
    private lateinit var helper: PreferenceHelper
    private var bookingId: String? = null

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
        helper = PreferenceHelper.getPref(requireContext())
        // Check if arguments are not null and retrieve data
        arguments?.let {
            markerLocation = it.getParcelable(ARG_LOCATION)!!
            dispensaryName.text = it.getString(ARG_TITLE)
            productAvailable.text = it.getString(ARG_PRODUCT_AVAILABLE)
            dispensaryDistance.text = it.getString(ARG_DISTANCE)
            selectedDispensory = it.getParcelable<Dispensory>(ARG_DISPENSORY)
            bookingId = it.getString(ARG_BOOKING_ID)
        }

        navigateButton.setOnClickListener {

            helper.getStringValue(PreferenceKeys.PREF_USER_TOKEN)?.let {
                bookingId?.let { it1 ->
                    viewModel.changeOrderStatus(
                        it,
                        it1, OrderStatus.NAVIGATING
                    )
                }
            }

            // Start OrderDetailsActivity or perform other actions with markerLocation
        }

        initObserver()
        return v
    }

    private fun initObserver() {
        viewModel.orderStatusResponse.observe(this) { response ->
            when (response) {
                is ApiResponse.Success -> {
                    Toast.makeText(requireContext(), "Navigating to Dispensory", Toast.LENGTH_SHORT)
                        .show()
                    helper.saveStringValue(PreferenceKeys.CURRENT_BOOKING_STATUS, OrderStatus.NAVIGATING)
                    helper.saveBooleanValue(PreferenceKeys.Is_FIRST_NAVIGATING, true)
                    startActivity(
                        Intent(activity, OrderDetailsActivity::class.java)
                            .putExtra(PreferenceKeys.BOOKING_ID, bookingId)
                            .putExtra("latitude", markerLocation.latitude)
                            .putExtra("longitude", markerLocation.longitude)
                            .putExtra("dispensory", selectedDispensory)
                    )
                    dismiss()
                    activity?.finish()
                }

                is ApiResponse.Error -> {
                    Toast.makeText(
                        requireContext(),
                        "Error: ${response.errorMessage}",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }

                is ApiResponse.Loading -> {
                    // Optional: Show loading
                }
            }
        }
    }

    companion object {
        private const val ARG_TITLE = "title"
        private const val ARG_PRODUCT_AVAILABLE = "product_available"
        private const val ARG_DISTANCE = "distance"
        private const val ARG_LOCATION = "location"
        private const val ARG_DISPENSORY = "dispensory"
        private const val ARG_BOOKING_ID = "booking_id"

        fun newInstance(
            title: String?,
            productAvailable: String?,
            distance: String?,
            location: LatLng,
            selectedDispensory: Dispensory?,
            bookingId: String?
        ): BottomSheetDialog {
            val fragment = BottomSheetDialog()
            val args = Bundle().apply {
                putString(ARG_TITLE, title)
                putString(ARG_PRODUCT_AVAILABLE, productAvailable)
                putString(ARG_DISTANCE, distance)
                putParcelable(ARG_LOCATION, location)
                putParcelable(ARG_DISPENSORY, selectedDispensory)
                putString(ARG_BOOKING_ID, bookingId)
            }
            fragment.arguments = args
            return fragment
        }
    }
}
