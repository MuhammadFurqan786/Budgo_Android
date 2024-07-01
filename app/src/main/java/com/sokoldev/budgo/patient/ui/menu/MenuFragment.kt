package com.sokoldev.budgo.patient.ui.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.sokoldev.budgo.databinding.FragmentMenuBinding
import com.sokoldev.budgo.patient.adapter.BrandAdapter
import com.sokoldev.budgo.patient.adapter.EdiblesAdapter
import com.sokoldev.budgo.patient.adapter.ProductAdapter

class MenuFragment : Fragment() {

    private var _binding: FragmentMenuBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val menuViewModel =
            ViewModelProvider(this)[MenuViewModel::class.java]

        _binding = FragmentMenuBinding.inflate(inflater, container, false)
        val root: View = binding.root

        menuViewModel.getCategoriesList()
        menuViewModel.getNewProductList()
        menuViewModel.getEdibles()

        initObserver(menuViewModel)
        return root
    }

    private fun initObserver(menuViewModel: MenuViewModel) {
        menuViewModel.listCategory.observe(viewLifecycleOwner) {
            val adapter = BrandAdapter(it)
            binding.recyclerviewCategories.adapter = adapter
        }
        menuViewModel.listNewProduct.observe(viewLifecycleOwner) {
            val adapter = ProductAdapter(it)
            binding.recyclerviewProducts.adapter = adapter
            binding.recyclerviewFlowers.adapter = adapter
            binding.recyclerviewTopSellers.adapter = adapter
        }
        menuViewModel.listEdibles.observe(viewLifecycleOwner) {
            val adapter = EdiblesAdapter(it)
            binding.recyclervieweEdibles.adapter = adapter
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}