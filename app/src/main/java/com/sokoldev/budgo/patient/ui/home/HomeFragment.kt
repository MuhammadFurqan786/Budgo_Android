package com.sokoldev.budgo.patient.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.sokoldev.budgo.databinding.FragmentHomeBinding
import com.sokoldev.budgo.patient.adapter.BrandAdapter
import com.sokoldev.budgo.patient.adapter.CategoryAdapter

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this)[HomeViewModel::class.java]

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        homeViewModel.getCategoriesList()
        homeViewModel.getBrandsList()

        initObserver(homeViewModel)
        return root
    }

    private fun initObserver(homeViewModel: HomeViewModel) {
        homeViewModel.listCategory.observe(viewLifecycleOwner) {
            val adapter = CategoryAdapter(it)
            binding.recyclerviewCategories.adapter = adapter
        }
        homeViewModel.listBrands.observe(viewLifecycleOwner) {
            val adapter = BrandAdapter(it)
            binding.recyclerviewBrands.adapter = adapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}