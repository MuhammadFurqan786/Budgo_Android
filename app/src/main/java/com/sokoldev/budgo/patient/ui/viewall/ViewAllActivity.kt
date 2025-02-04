package com.sokoldev.budgo.patient.ui.viewall

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.sokoldev.budgo.common.data.remote.network.ApiResponse
import com.sokoldev.budgo.common.utils.Constants
import com.sokoldev.budgo.common.utils.Global
import com.sokoldev.budgo.common.utils.prefs.PreferenceHelper
import com.sokoldev.budgo.common.utils.prefs.PreferenceKeys
import com.sokoldev.budgo.databinding.ActivityViewAllBinding
import com.sokoldev.budgo.patient.adapter.ProductAdapter

class ViewAllActivity : AppCompatActivity() {
    private lateinit var binding: ActivityViewAllBinding
    private lateinit var helper: PreferenceHelper
    private val viewModel: ViewAllViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityViewAllBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        helper = PreferenceHelper.getPref(this)

        val type = intent.getStringExtra(Constants.TYPE)
        if (type != null) {
            if (type == Constants.CATEGORY) {
                val categoryId = intent.getIntExtra(Constants.CATEGORY_ID, 0)
                val categoryName = intent.getStringExtra(Constants.CATEGORY_NAME)
                binding.title.text = categoryName
                helper.getStringValue(PreferenceKeys.PREF_USER_TOKEN).let {
                    if (it != null) {
                        viewModel.getProductsByCategoryId(it, categoryId.toString())
                    }
                }

            } else if (type == Constants.BRAND) {
                val brandId = intent.getIntExtra(Constants.BRAND_ID, 0)
                val brandName = intent.getStringExtra(Constants.BRAND_NAME)
                binding.title.text = brandName
                helper.getStringValue(PreferenceKeys.PREF_USER_TOKEN).let {
                    if (it != null) {
                        viewModel.getProductsByCategoryId(it, brandId.toString())
                    }
                }

            }
        }
        binding.back.setOnClickListener { finish() }
        initObserver()
    }

    private fun initObserver() {
        viewModel.apiResponse.observe(this, Observer {
            when (it) {
                is ApiResponse.Success -> {
                    if (it.data.data != null) {
                        val listCategory = it.data.data
                        val adapterCategory = ProductAdapter(listCategory)
                        binding.rvProducts.adapter = adapterCategory
                        binding.rvProducts.layoutManager = GridLayoutManager(this, 2)
                        binding.rvProducts.setHasFixedSize(true)
                        binding.loadingView.visibility = View.GONE
                        binding.dataLayout.visibility = View.VISIBLE

                    } else {
                        binding.loadingView.visibility = View.GONE
                        binding.dataLayout.visibility = View.GONE
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
                    binding.loadingView.visibility = View.GONE
                    binding.dataLayout.visibility = View.GONE
                }

                ApiResponse.Loading -> {
                    binding.loadingView.visibility = View.VISIBLE
                    binding.dataLayout.visibility = View.GONE
                }
            }
        })
    }
}