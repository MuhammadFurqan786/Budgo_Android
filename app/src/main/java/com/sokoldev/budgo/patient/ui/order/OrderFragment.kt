package com.sokoldev.budgo.patient.ui.order

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.sokoldev.budgo.common.data.remote.network.ApiResponse
import com.sokoldev.budgo.common.utils.Global
import com.sokoldev.budgo.databinding.FragmentOrderBinding
import com.sokoldev.budgo.patient.adapter.BookingAdapter

class OrderFragment : Fragment() {

    private var _binding: FragmentOrderBinding? = null

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

        _binding = FragmentOrderBinding.inflate(inflater, container, false)
        val root: View = binding.root

        orderViewModel.getMyBookings("16|RN81QkR4q1zLy8zH3NZRiYpeljJlCWVq7s2OyFE6")
        initObserver(orderViewModel)

        return root
    }

    private fun initObserver(orderViewModel: OrderViewModel) {
        orderViewModel.apiResponse.observe(viewLifecycleOwner, Observer { it ->
            when (it) {
                is ApiResponse.Success -> {
                    if (it.data.data.currentBookings != null) {
                        val list = it.data.data.currentBookings
                        val adapter = BookingAdapter(list)
                        binding.rvOrders.adapter = adapter
                    } else {
                        Global.showErrorMessage(
                            binding.root.rootView,
                            it.data.message,
                            Snackbar.LENGTH_SHORT
                        )
                    }
                }

                is ApiResponse.Error -> {
                    Global.showErrorMessage(
                        binding.root.rootView,
                        it.errorMessage,
                        Snackbar.LENGTH_SHORT
                    )
                    //                    binding.spinKit.visibility = View.GONE
                }

                ApiResponse.Loading -> {
                    //                    binding.spinKit.visibility = View.VISIBLE
                }

            }

        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}