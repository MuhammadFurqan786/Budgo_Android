package com.sokoldev.budgo.patient.ui.profile

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import com.sokoldev.budgo.R
import com.sokoldev.budgo.common.data.remote.network.ApiResponse
import com.sokoldev.budgo.common.ui.SplashActivity
import com.sokoldev.budgo.common.ui.notification.NotificationActivity
import com.sokoldev.budgo.common.utils.Constants
import com.sokoldev.budgo.common.utils.Global
import com.sokoldev.budgo.common.utils.prefs.PreferenceHelper
import com.sokoldev.budgo.common.utils.prefs.PreferenceKeys
import com.sokoldev.budgo.common.viewmodels.UserViewModel
import com.sokoldev.budgo.databinding.FragmentProfileBinding
import de.hdodenhof.circleimageview.CircleImageView
import java.io.File
import java.io.IOException
import java.util.Locale

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private lateinit var imageUri: Uri
    private lateinit var cameraLauncher: ActivityResultLauncher<Uri>
    private lateinit var galleryLauncher: ActivityResultLauncher<String>
    private lateinit var helper: PreferenceHelper
    private val viewModel: UserViewModel by viewModels()
    private val viewModelProfile: ProfileViewModel by viewModels()
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cameraLauncher =
            registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
                if (success) {
                    uploadImage(getFileFromUri(requireContext(), imageUri))
                }
            }
        // For gallery
        galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                uploadImage(getFileFromUri(requireContext(), it))
            }
        }
    }

    private fun uploadImage(fileFromUri: File) {
        helper.getStringValue(PreferenceKeys.PREF_USER_TOKEN)?.let {
            viewModel.updateProfileImage(it, fileFromUri)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root


        helper =  PreferenceHelper.getPref(requireContext())
        val user = helper.getCurrentUser()
        user?.profileImage.let {
            loadImageWithBaseUrlFallback(binding.userImage, it.toString())
        }
        binding.completeOrder.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_profile_to_completeOrderFragment)
        }
        binding.personalInfo.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_profile_to_updateProfileFragment)
        }

        binding.changePassword.setOnClickListener {
            val bundle = Bundle()
            bundle.putBoolean(Constants.IS_CHANGE_PASSWORD, true)
            findNavController().navigate(
                R.id.action_navigation_profile_to_resetPasswordFragment2, bundle
            )
        }

        binding.notification.setOnClickListener {
            startActivity(
                Intent(
                    context, NotificationActivity::class.java
                )
            )

        }

        binding.location.setOnClickListener {
            showUpdateLocationDialog()
        }

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

        binding.cameraImage.setOnClickListener {
            updateProfileImage()
        }

        initObserver()

        return root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    @SuppressLint("SetTextI18n")
    private fun showUpdateLocationDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_update_location, null)
        val dialog = AlertDialog.Builder(requireContext()).setView(dialogView).create()

        val tvResult = dialogView.findViewById<AppCompatTextView>(R.id.tvLocationResult)
        val progressBar = dialogView.findViewById<ProgressBar>(R.id.progressBar)
        val btnSend = dialogView.findViewById<Button>(R.id.btnSendLocation)

        btnSend.isEnabled = false

        if (ActivityCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1001)
            return
        }

        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            progressBar.visibility = View.GONE
            location?.let {
                val lat = it.latitude
                val lon = it.longitude

                val address = getAddressFromCoordinates(lat, lon)
                tvResult.text = "Lat: $lat\nLon: $lon\nAddress: $address"
                btnSend.isEnabled = true

                btnSend.setOnClickListener {
                    helper.getStringValue(PreferenceKeys.PREF_USER_TOKEN)?.let { it1 ->
                        viewModelProfile.updateLocation(
                            it1, address, lat.toString(), lon.toString()
                        )
                    }
                    viewModelProfile.updateLocationResponse.observe(viewLifecycleOwner) { response ->
                        when (response) {
                            is ApiResponse.Success -> {
                                Toast.makeText(context, "Location Updated", Toast.LENGTH_SHORT)
                                    .show()
                                progressBar.visibility = View.GONE
                                dialog.dismiss()
                            }

                            is ApiResponse.Error -> {
                                progressBar.visibility = View.GONE
                                Toast.makeText(context, response.errorMessage, Toast.LENGTH_SHORT)
                                    .show()
                            }

                            is ApiResponse.Loading -> {
                                progressBar.visibility = View.VISIBLE
                            }
                        }

                    }

                }
            } ?: run {
                tvResult.text = "Failed to get location."
            }
        }

        dialog.show()
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

    private fun initObserver() {
        viewModel.apiResponseProfile.observe(viewLifecycleOwner, Observer {
            when (it) {
                is ApiResponse.Success -> {
                    binding.loadingView.visibility = View.GONE
                    binding.loadingView.hide()
                    if (it.data.data != null) {
                        val user = it.data.data
                        helper.saveCurrentUser(user)
                        loadImageWithBaseUrlFallback(
                            binding.userImage, user.profileImage.toString()
                        )
                        Global.showMessage(
                            binding.root.rootView, it.data.message, Snackbar.LENGTH_SHORT
                        )

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

    private fun updateProfileImage() {
        val options = arrayOf("Camera", "Gallery")
        AlertDialog.Builder(requireContext()).setTitle("Select Image Source")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> openCamera()
                    1 -> openGallery()
                }
            }.show()
    }

    private fun openCamera() {
        val photoFile = File.createTempFile("IMG_", ".jpg", requireContext().cacheDir)
        imageUri = FileProvider.getUriForFile(
            requireContext(), "${requireContext().packageName}.provider", photoFile
        )
        cameraLauncher.launch(imageUri)
    }

    private fun openGallery() {
        galleryLauncher.launch("image/*")
    }

    fun getFileFromUri(context: Context, uri: Uri): File {
        val inputStream = context.contentResolver.openInputStream(uri)
        val tempFile = File.createTempFile("temp_image", ".jpg", context.cacheDir)
        tempFile.outputStream().use {
            inputStream?.copyTo(it)
        }
        return tempFile
    }

    private fun loadImageWithBaseUrlFallback(imageView: CircleImageView, originalUrl: String) {
        // Sanitize the original URL by replacing spaces
        val safeOriginalUrl = originalUrl.replace(" ", "%20")

        // Replace base URL and sanitize the fallback as well
        val modifiedUrl = safeOriginalUrl.replace(
            "https://budgo.net/budgo/public/",
            "https://admin.budgo.net/"
        )

        // Build the fallback request with the safe original URL
        val fallbackRequest = Glide.with(imageView.context)
            .load(safeOriginalUrl)

        // Start primary request with fallback
        Glide.with(imageView.context)
            .load(modifiedUrl)
            .error(fallbackRequest) // Try this if the primary fails
            .into(imageView)
    }

}