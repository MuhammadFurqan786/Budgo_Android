package com.sokoldev.budgo.caregiver.ui.account

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatImageView
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
import com.google.android.material.snackbar.Snackbar
import com.sokoldev.budgo.R
import com.sokoldev.budgo.common.data.remote.network.ApiResponse
import com.sokoldev.budgo.common.ui.SplashActivity
import com.sokoldev.budgo.common.ui.notification.NotificationActivity
import com.sokoldev.budgo.common.utils.Global
import com.sokoldev.budgo.common.utils.prefs.PreferenceHelper
import com.sokoldev.budgo.common.utils.prefs.PreferenceKeys
import com.sokoldev.budgo.common.viewmodels.UserViewModel
import com.sokoldev.budgo.databinding.FragmentAccountBinding
import de.hdodenhof.circleimageview.CircleImageView
import java.io.File

class AccountFragment : Fragment() {
    private var _binding: FragmentAccountBinding? = null
    private val binding get() = _binding!!
    private lateinit var imageUri: Uri
    private lateinit var cameraLauncher: ActivityResultLauncher<Uri>
    private lateinit var galleryLauncher: ActivityResultLauncher<String>
    private lateinit var helper: PreferenceHelper
    private val viewModel: UserViewModel by viewModels()


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
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAccountBinding.inflate(inflater, container, false)
        val root: View = binding.root

        helper = PreferenceHelper.getPref(requireContext())

        val user = helper.getCurrentUser()
        user?.profileImage.let {
            loadImageWithBaseUrlFallback(binding.userImage, it.toString())
        }


        binding.apply {
            personalInfo.setOnClickListener {
                findNavController().navigate(R.id.action_navigation_account_to_personalInfoFragment)
            }

            schedule.setOnClickListener {
                findNavController().navigate(R.id.action_navigation_account_to_navigation_schedule)
            }

            notification.setOnClickListener {
                startActivity(
                    Intent(
                        requireContext(),
                        NotificationActivity::class.java
                    )
                )
            }

            logoutButton.setOnClickListener {
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
            binding.addImage.setOnClickListener {
                updateProfileImage()
            }
        }
        initObserver()
        return root
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
                            binding.userImage,
                            user.profileImage.toString()
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
        AlertDialog.Builder(requireContext())
            .setTitle("Select Image Source")
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
            requireContext(),
            "${requireContext().packageName}.provider",
            photoFile
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