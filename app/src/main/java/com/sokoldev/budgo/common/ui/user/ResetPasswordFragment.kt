package com.sokoldev.budgo.common.ui.user

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import com.sokoldev.budgo.common.data.remote.network.ApiResponse
import com.sokoldev.budgo.common.utils.Constants
import com.sokoldev.budgo.common.utils.Global
import com.sokoldev.budgo.common.viewmodels.UserViewModel
import com.sokoldev.budgo.databinding.FragmentResetPasswordBinding


class ResetPasswordFragment : Fragment() {
    private lateinit var _binding: FragmentResetPasswordBinding
    private val binding get() = _binding
    private var email: String? = ""
    private val viewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentResetPasswordBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        email = arguments?.getString(Constants.EMAIL)

        email?.let { Global.showMessage(binding.root.rootView, it, Snackbar.LENGTH_SHORT) }
        binding.back.setOnClickListener {
            requireActivity().onBackPressed()
        }
        binding.resetButton.setOnClickListener {

            val newPassword = binding.edPassword.text.toString().trim()
            val confirmPassword = binding.edConfirmPassword.text.toString().trim()

            if (newPassword == confirmPassword) {
                viewModel.resetUserPassword(email.toString(), newPassword)
            } else {
                binding.edConfirmPassword.error = "Password does not match"
            }

        }

        initObserver()
    }

    private fun initObserver() {
        viewModel.apiResponse.observe(viewLifecycleOwner, Observer {
            when (it) {
                is ApiResponse.Success -> {
                    binding.loadingView.visibility = View.GONE
                    binding.loadingView.hide()
                    if (it.data.status) {
                        Global.showMessage(
                            binding.root.rootView,
                            it.data.message,
                            Snackbar.LENGTH_SHORT
                        )

                        val intent = Intent(requireActivity(), LoginActivity::class.java)
                        startActivity(intent)
                        activity?.finish()


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