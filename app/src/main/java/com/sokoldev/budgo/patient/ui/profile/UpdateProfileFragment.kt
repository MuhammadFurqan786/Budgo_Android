package com.sokoldev.budgo.patient.ui.profile

import android.annotation.SuppressLint
import android.location.Geocoder
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import com.sokoldev.budgo.common.data.remote.network.ApiResponse
import com.sokoldev.budgo.common.utils.DatePickerUtils
import com.sokoldev.budgo.common.utils.Global
import com.sokoldev.budgo.common.utils.prefs.PreferenceHelper
import com.sokoldev.budgo.common.utils.prefs.PreferenceKeys
import com.sokoldev.budgo.common.viewmodels.UserViewModel
import com.sokoldev.budgo.databinding.FragmentUpdateProfileBinding
import java.io.IOException
import java.util.Locale


class UpdateProfileFragment : Fragment() {

    private lateinit var _binding: FragmentUpdateProfileBinding
    private val binding get() = _binding
    private lateinit var helper: PreferenceHelper
    private lateinit var viewModel: UserViewModel
    private var latitude: String = ""
    private var longitude: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentUpdateProfileBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    @SuppressLint("MissingPermission")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[UserViewModel::class.java]
        helper = PreferenceHelper.getPref(requireContext())
        val user = helper.getCurrentUser()
        binding.edName.setText(user?.name)
        binding.edPhone.setText(user?.phone)
        binding.edDob.setText(user?.dob)
        binding.edLocation.setText(user?.location)
        latitude = user?.latitude.toString()
        longitude = user?.longitude.toString()

        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            location?.let {
                latitude = it.latitude.toString()
                longitude = it.longitude.toString()
                val address = getAddressFromCoordinates(latitude.toDouble(), longitude.toDouble())
                binding.edLocation.setText(address)
            }

        }

        binding.edDob.apply {
            inputType = InputType.TYPE_NULL
            isFocusable = false
            isClickable = true
        }
        binding.edDob.setOnClickListener {

            DatePickerUtils.showMaterialDatePicker(requireActivity()) { date ->
                binding.edDob.setText(date)
            }
        }

        binding.back.setOnClickListener { findNavController().navigateUp() }

        binding.continueButton.setOnClickListener {
            updateProfile()
        }

        initObserver()
    }

    private fun initObserver() {
        viewModel.apiResponseProfile.observe(viewLifecycleOwner, Observer {
            when (it) {
                is ApiResponse.Success -> {
                    binding.loadingView.visibility = View.GONE
                    binding.loadingView.hide()
                    if (it.data.data != null) {
                        val user = it.data.data
                        helper.saveCurrentUser(user)
                        Log.d("TOOKEN", "" + it.data.data.token)
                        Global.showMessage(
                            binding.root.rootView, it.data.message, Snackbar.LENGTH_SHORT
                        )
                        findNavController().navigateUp()

                    } else {
                        Global.showErrorMessage(
                            binding.root.rootView, it.data.message, Snackbar.LENGTH_SHORT
                        )
                    }
                }

                is ApiResponse.Error -> {
                    Global.showErrorMessage(
                        binding.root.rootView, it.errorMessage, Snackbar.LENGTH_SHORT
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

    private fun updateProfile() {
        val name = binding.edName.text.toString().trim()
        val phone = binding.edPhone.text.toString().trim()
        val dob = binding.edDob.text.toString().trim()

        if (name.isEmpty()) {
            binding.edName.error = "Please add your name"
            return
        } else if (phone.isEmpty()) {
            binding.edPhone.error = "Please add your phone"
            return
        } else if (dob.isEmpty()) {
            binding.edDob.error = "Please add your date of birth"
            return
        }

        viewModel.updateProfile(
            token = helper.getStringValue(PreferenceKeys.PREF_USER_TOKEN).toString(),
            name, phone, dob, latitude, longitude
        )
    }

    private fun getAddressFromCoordinates(latitude: Double, longitude: Double): String {
        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        return try {
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
            if (addresses != null && addresses.isNotEmpty()) {
                val address = addresses[0]
                address.getAddressLine(0) ?: "Address not available"
            } else {
                "Address not found"
            }
        } catch (e: IOException) {
            "Geocoder error: ${e.localizedMessage}"
        }
    }


}