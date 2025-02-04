package com.sokoldev.budgo.patient.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.sokoldev.budgo.common.ui.SplashActivity
import com.sokoldev.budgo.common.utils.prefs.PreferenceHelper
import com.sokoldev.budgo.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private lateinit var helper: PreferenceHelper

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val profileViewModel =
            ViewModelProvider(this)[ProfileViewModel::class.java]

        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root


        helper =  PreferenceHelper.getPref(requireContext())

        binding.logoutButton.setOnClickListener {
            helper.clearPreferences()
            helper.setUserLogin(false)
            startActivity(
                Intent(
                    context,
                    SplashActivity::class.java
                )
            )
            requireActivity().finish()
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}