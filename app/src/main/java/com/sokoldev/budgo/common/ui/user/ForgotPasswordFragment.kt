package com.sokoldev.budgo.common.ui.user

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.sokoldev.budgo.R
import com.sokoldev.budgo.common.data.remote.network.ApiResponse
import com.sokoldev.budgo.common.utils.Constants
import com.sokoldev.budgo.common.utils.Global
import com.sokoldev.budgo.common.utils.prefs.PreferenceHelper
import com.sokoldev.budgo.common.utils.prefs.PreferenceKeys
import com.sokoldev.budgo.common.viewmodels.UserViewModel
import com.sokoldev.budgo.databinding.FragmentForgotPasswordBinding

class ForgotPasswordFragment : Fragment() {

    private lateinit var _binding: FragmentForgotPasswordBinding
    private val binding get() = _binding
    private lateinit var helper: PreferenceHelper
    private val viewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentForgotPasswordBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        helper = PreferenceHelper.getPref(requireContext())
        val token = helper.getStringValue(PreferenceKeys.PREF_USER_TOKEN, "")
        binding.sendButton.setOnClickListener {
            val email = binding.edEmail.text.toString().trim()
            if (email.isEmpty()) {
                binding.edEmail.error = "Please add your email address"
                return@setOnClickListener
            }

            viewModel.forgotUserPassword(email)
        }

        binding.back.setOnClickListener {
            requireActivity().finish()
        }

        initObserver()
    }


    private fun initObserver() {
        viewModel.apiResponseForgotPassword.observe(viewLifecycleOwner, Observer {
            when (it) {
                is ApiResponse.Success -> {
                    binding.loadingView.visibility = View.GONE
                    binding.loadingView.hide()
                    if (it.data.data != null) {
                        Global.showMessage(
                            binding.root.rootView,
                            it.data.message,
                            Snackbar.LENGTH_SHORT
                        )
                        val bundle = Bundle()
                        bundle.putString(Constants.EMAIL, binding.edEmail.text.toString().trim())
                        bundle.putInt(Constants.OTP, it.data.data.code)
                        findNavController().navigate(
                            R.id.action_forgotPasswordFragment_to_otpFragment,
                            bundle
                        )


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
                    binding.loadingView.visibility = View.GONE
                    binding.loadingView.hide()
                }

                ApiResponse.Loading -> {
                    Log.d("LoginActivity", "Loading state triggered")
                    binding.loadingView.visibility = View.VISIBLE
                    binding.loadingView.show()

                }
            }

        })
    }
}