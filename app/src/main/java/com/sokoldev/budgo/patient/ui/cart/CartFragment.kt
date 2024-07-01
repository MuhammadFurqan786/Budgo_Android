package com.sokoldev.budgo.patient.ui.cart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.sokoldev.budgo.R
import com.sokoldev.budgo.databinding.FragmentCartBinding
import com.sokoldev.budgo.patient.adapter.CartAdapter

class CartFragment : Fragment() {

    private var _binding: FragmentCartBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val cartViewModel =
            ViewModelProvider(this)[CartViewModel::class.java]

        _binding = FragmentCartBinding.inflate(inflater, container, false)
        val root: View = binding.root

        cartViewModel.getCartList()
        initObserver(cartViewModel)


        binding.apply {
            checkoutButton.setOnClickListener {
                findNavController().navigate(R.id.action_navigation_cart_to_cancellationFragment)
            }

        }
        return root
    }

    private fun initObserver(cartViewModel: CartViewModel) {
        cartViewModel.listCart.observe(viewLifecycleOwner) {
            val adapter = CartAdapter(it)
            binding.recyclerviewCart.adapter = adapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}