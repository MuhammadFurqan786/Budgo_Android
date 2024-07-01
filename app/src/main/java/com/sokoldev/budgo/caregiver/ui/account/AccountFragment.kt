package com.sokoldev.budgo.caregiver.ui.account

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.sokoldev.budgo.R
import com.sokoldev.budgo.common.ui.SplashActivity
import com.sokoldev.budgo.databinding.FragmentAccountBinding

class AccountFragment : Fragment() {
    private var _binding: FragmentAccountBinding? = null
    private val binding get() = _binding!!

    companion object {
        fun newInstance() = AccountFragment()
    }

    private val viewModel: AccountViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAccountBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.apply {
            personalInfo.setOnClickListener {
                findNavController().navigate(R.id.action_navigation_account_to_personalInfoFragment)
            }
            logoutButton.setOnClickListener {
                startActivity(
                    Intent(
                        context,
                        SplashActivity::class.java
                    )
                )
                requireActivity().finish()
            }

        }


        return root
    }
}