package com.sokoldev.budgo.patient.ui.order

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
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

        orderViewModel.getBookingList()
        initObserver(orderViewModel)

        return root
    }

    private fun initObserver(orderViewModel: OrderViewModel) {
        orderViewModel.listBooking.observe(viewLifecycleOwner) {
            val adapter = BookingAdapter(it)
            binding.rvOrders.adapter = adapter
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}