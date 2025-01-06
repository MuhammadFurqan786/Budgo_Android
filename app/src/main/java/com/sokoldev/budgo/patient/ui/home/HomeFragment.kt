package com.sokoldev.budgo.patient.ui.home

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
import com.sokoldev.budgo.common.utils.prefs.PreferenceHelper
import com.sokoldev.budgo.common.utils.prefs.PreferenceKeys
import com.sokoldev.budgo.databinding.FragmentHomeBinding
import com.sokoldev.budgo.patient.adapter.BrandAdapter
import com.sokoldev.budgo.patient.adapter.CategoryAdapter

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private lateinit var helper : PreferenceHelper

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

        helper = PreferenceHelper.getPref(requireContext())

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        helper.getStringValue(PreferenceKeys.PREF_USER_TOKEN).let {
            if (it != null) {
                homeViewModel.homeScreenApi(it)
            }
        }

        initObserver(homeViewModel)
        return root
    }


    private fun initObserver(homeViewModel: HomeViewModel) {
        homeViewModel.apiResponse.observe(viewLifecycleOwner, Observer {
            when (it) {
                is ApiResponse.Success -> {
//                    binding.spinKit.visibility = View.GONE
                    if (it.data.data != null) {

                       val listCategory = it.data.data.categories
                        val adapterCategory = CategoryAdapter(listCategory)
                        binding.recyclerviewCategories.adapter = adapterCategory


                       val listBrands = it.data.data.brands
                        val adapterBrand = BrandAdapter(listBrands)
                        binding.recyclerviewBrands.adapter = adapterBrand

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