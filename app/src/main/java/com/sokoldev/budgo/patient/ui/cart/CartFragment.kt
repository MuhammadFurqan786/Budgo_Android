package com.sokoldev.budgo.patient.ui.cart

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.sokoldev.budgo.R
import com.sokoldev.budgo.common.data.local.AppDatabase
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
        val cartDao = AppDatabase.getDatabase(requireContext()).cartDao()
        val cartViewModel =
            ViewModelProvider(this, CartViewModelFactory(cartDao))[CartViewModel::class.java]

        _binding = FragmentCartBinding.inflate(inflater, container, false)
        val root: View = binding.root

        cartViewModel.getCartItems()
        initObserver(cartViewModel)


        binding.apply {
            checkoutButton.setOnClickListener {
                findNavController().navigate(R.id.action_navigation_cart_to_cancellationFragment)
            }
        }
        return root
    }

    @SuppressLint("SetTextI18n")
    private fun initObserver(cartViewModel: CartViewModel) {
        // Initialize the adapter outside of the observer
        val adapter = CartAdapter(emptyList(), cartViewModel) // Initialize with an empty list
        binding.recyclerviewCart.adapter = adapter

        // Observe the cart items
        cartViewModel.listCart.observe(viewLifecycleOwner) { cartItems ->
            // Update the adapter with the new list of cart items

            if (cartItems != null) {
                adapter.updateCartItems(cartItems)
                var price = 0
                for (item in cartItems) {
                    price += item.productPrice * item.quantity
                }
                binding.total.text = "$$price"
                binding.recyclerviewCart.visibility = View.VISIBLE
                binding.linearCheckout.visibility = View.VISIBLE
                binding.imageEmptyCart.visibility = View.GONE
                binding.textEmptyCart.visibility = View.GONE
            } else {
                binding.recyclerviewCart.visibility = View.GONE
                binding.linearCheckout.visibility = View.GONE
                binding.imageEmptyCart.visibility = View.VISIBLE
                binding.textEmptyCart.visibility = View.VISIBLE
            }

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}