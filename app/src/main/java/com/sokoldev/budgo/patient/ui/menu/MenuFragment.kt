package com.sokoldev.budgo.patient.ui.menu

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
import com.sokoldev.budgo.databinding.FragmentMenuBinding
import com.sokoldev.budgo.patient.adapter.EdiblesAdapter
import com.sokoldev.budgo.patient.adapter.ProductAdapter

class MenuFragment : Fragment() {

    private var _binding: FragmentMenuBinding? = null
    private lateinit var helper: PreferenceHelper

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
        helper = PreferenceHelper.getPref(requireContext())

        _binding = FragmentMenuBinding.inflate(inflater, container, false)
        val root: View = binding.root

        helper.getStringValue(PreferenceKeys.PREF_USER_TOKEN).let {
            if (it != null) {
                menuViewModel.menuScreenApi(it)
            }
        }

        initObserver(menuViewModel)
        return root
    }

    private fun initObserver(menuViewModel: MenuViewModel) {

        menuViewModel.apiResponse.observe(viewLifecycleOwner, Observer {
            when (it) {
                is ApiResponse.Success -> {
                    if (it.data.data != null) {

                        val topSeller = it.data.data.topSeller

                        val adapterTopSeller = ProductAdapter(topSeller)
                        binding.recyclerviewTopSellers.adapter = adapterTopSeller

                        val listProduct = it.data.data.newProducts
                        val newProductAdapter = ProductAdapter(listProduct)
                        binding.recyclerviewProducts.adapter = newProductAdapter


                        val popularFlower = it.data.data.popularFlowers
                        val popularFlowerAdapter = ProductAdapter(popularFlower)
                        binding.recyclerviewFlowers.adapter = popularFlowerAdapter


                        val popularEdibles = it.data.data.popularEdibles
                        val popularEdiblesAdapter = EdiblesAdapter(popularEdibles)
                        binding.recyclervieweEdibles.adapter = popularEdiblesAdapter

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

                is ApiResponse.Loading -> {
//                    binding.spinKit.visibility = View.GONE
                }

            }
        })

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}