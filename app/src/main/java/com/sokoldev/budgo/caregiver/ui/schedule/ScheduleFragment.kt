package com.sokoldev.budgo.caregiver.ui.schedule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.sokoldev.budgo.common.data.remote.network.ApiResponse
import com.sokoldev.budgo.common.utils.prefs.PreferenceHelper
import com.sokoldev.budgo.common.utils.prefs.PreferenceKeys
import com.sokoldev.budgo.common.viewmodels.UserViewModel
import com.sokoldev.budgo.databinding.FragmentScheduleBinding

class ScheduleFragment : Fragment() {

    private var _binding: FragmentScheduleBinding? = null
    private val viewModel: UserViewModel by viewModels()
    private lateinit var helper: PreferenceHelper
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentScheduleBinding.inflate(inflater, container, false)
        val root: View = binding.root
        helper = PreferenceHelper.getPref(requireContext())

        val isOnline = helper.getStringValue(PreferenceKeys.IS_ONLINE, "")
        if (isOnline == "Online") {
            binding.availabilitySwitch.isChecked = true
        } else {
            binding.availabilitySwitch.isChecked = false
        }

        val token = helper.getStringValue(PreferenceKeys.PREF_USER_TOKEN, "")
        binding.availabilityButton.setOnClickListener {
            var status = 0
            val isOnline = binding.availabilitySwitch.isChecked
            if (isOnline) {
                status = 1
            } else {
                status = 0
            }
            if (token != null) {
                viewModel.changeOnlineStatus(token, status)
            }
        }

        initObserver()
        return root
    }

    private fun initObserver() {
        viewModel.apiResponse.observe(viewLifecycleOwner) {
            when (it) {
                is ApiResponse.Success -> {
                    binding.loadingView.visibility = View.GONE
                    Toast.makeText(requireContext(), it.data.message, Toast.LENGTH_SHORT).show()
                    helper.saveStringValue(PreferenceKeys.IS_ONLINE, it.data.message)
                }

                is ApiResponse.Error -> {
                    binding.loadingView.visibility = View.GONE
                    Toast.makeText(requireContext(), it.errorMessage, Toast.LENGTH_SHORT).show()
                }

                is ApiResponse.Loading -> {
                    binding.loadingView.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}