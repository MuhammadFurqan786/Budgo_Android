package com.sokoldev.budgo.common.ui.orders

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.sokoldev.budgo.common.data.remote.network.ApiResponse
import com.sokoldev.budgo.common.utils.Global
import com.sokoldev.budgo.common.utils.prefs.PreferenceHelper
import com.sokoldev.budgo.common.utils.prefs.PreferenceKeys
import com.sokoldev.budgo.databinding.FragmentCompleteOrderBinding
import com.sokoldev.budgo.patient.adapter.BookingAdapter
import com.sokoldev.budgo.patient.ui.order.OrderViewModel

class CompleteOrderFragment : Fragment() {

    private var _binding: FragmentCompleteOrderBinding? = null
    private lateinit var helper: PreferenceHelper

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val orderViewModel =
            ViewModelProvider(this)[OrderViewModel::class.java]

        helper = PreferenceHelper.getPref(requireContext())

        _binding = FragmentCompleteOrderBinding.inflate(inflater, container, false)
        val root: View = binding.root


        helper.getStringValue(PreferenceKeys.PREF_USER_TOKEN).let {
            if (it != null) {
                orderViewModel.getMyBookings(it)
            }
        }
        binding.back.setOnClickListener { findNavController().navigateUp() }
        initObserver(orderViewModel)

        return root
    }

    private fun initObserver(orderViewModel: OrderViewModel) {
        orderViewModel.apiResponse.observe(viewLifecycleOwner, Observer { it ->
            when (it) {
                is ApiResponse.Success -> {

                    if (it.data.data != null) {
                        val isPatient = if (helper.getCurrentUser()?.type == "1") true else false
                        val list2 = it.data.data.completedBookings
                        val adapter2 = BookingAdapter(list2, isPatient)
                        binding.rvCompletedOrders.adapter = adapter2

                        binding.rvCompletedOrders.visibility = View.VISIBLE
                        binding.dataLayout.visibility = View.VISIBLE
                        binding.loadingView.visibility = View.GONE
                    } else {
                        Global.showErrorMessage(
                            binding.root.rootView,
                            it.data.message,
                            Snackbar.LENGTH_SHORT
                        )
                        binding.dataLayout.visibility = View.GONE
                        binding.loadingView.visibility = View.GONE
                    }
                }

                is ApiResponse.Error -> {
                    binding.loadingView.hide()
                    binding.loadingView.visibility = View.GONE
                    Global.showErrorMessage(
                        binding.root.rootView,
                        it.errorMessage,
                        Snackbar.LENGTH_SHORT
                    )
                    binding.dataLayout.visibility = View.GONE
                    binding.loadingView.visibility = View.GONE
                }

                ApiResponse.Loading -> {
                    binding.loadingView.show()
                    binding.loadingView.visibility = View.VISIBLE
                    binding.dataLayout.visibility = View.GONE
                }
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}