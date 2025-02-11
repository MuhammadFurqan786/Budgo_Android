package com.sokoldev.budgo.common.ui.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.sokoldev.budgo.R
import com.sokoldev.budgo.common.utils.Constants
import com.sokoldev.budgo.common.utils.Global
import com.sokoldev.budgo.common.utils.Global.hideKeyboard
import com.sokoldev.budgo.databinding.FragmentOtpBinding


class OtpFragment : Fragment() {

    private lateinit var _binding: FragmentOtpBinding
    private val binding get() = _binding
    private var email: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentOtpBinding.inflate(layoutInflater, container, false)

        email = arguments?.getString(Constants.EMAIL)
        val otpCode = arguments?.getInt(Constants.OTP).toString()
        email.let {
            binding.textEmail.text = getString(R.string.otp_text, email)
        }

        binding.otpView.requestFocus()

        binding.otpView.setOtpCompletionListener {
            binding.otpView.setLineColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.primary
                )
            )
            activity?.hideKeyboard()
        }

        binding.verifyButton.setOnClickListener {
            val otp = binding.otpView.text.toString()
            if (otp.isEmpty()) {
                binding.otpView.setLineColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.primary
                    )
                )
                Global.showErrorMessage(binding.root.rootView, "Please Enter Otp")
                binding.otpView.setLineColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.color_red
                    )
                )
                return@setOnClickListener
            }
            if (email?.isEmpty() == true) {
                binding.textEmail.visibility = View.VISIBLE
                binding.textEmail.text = "Email not found"
                return@setOnClickListener
            }
            if (otpCode == otp) {
                Global.showMessage(binding.root.rootView, "OTP Verified Successfully")
                val bundle = Bundle()
                bundle.putString(Constants.EMAIL, email.toString())
                findNavController().navigate(
                    R.id.action_otpFragment_to_resetPasswordFragment,
                    bundle
                )
            } else {
                Global.showErrorMessage(binding.root.rootView, "OTP Verification Failed")
                binding.otpView.setLineColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.color_red
                    )
                )
            }
        }

        binding.back.setOnClickListener {
            findNavController().popBackStack()
        }

        return binding.root
    }


}