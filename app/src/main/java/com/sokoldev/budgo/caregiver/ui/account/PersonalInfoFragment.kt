package com.sokoldev.budgo.caregiver.ui.account

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.material.button.MaterialButton
import com.sokoldev.budgo.R
import com.sokoldev.budgo.common.data.remote.network.ApiResponse
import com.sokoldev.budgo.common.ui.user.LoginActivity
import com.sokoldev.budgo.common.utils.prefs.PreferenceHelper
import com.sokoldev.budgo.common.utils.prefs.PreferenceKeys
import com.sokoldev.budgo.common.viewmodels.UserViewModel
import com.sokoldev.budgo.databinding.FragmentPersonalInfoBinding
import de.hdodenhof.circleimageview.CircleImageView

class PersonalInfoFragment : Fragment() {

    private lateinit var _binding: FragmentPersonalInfoBinding
    private val binding get() = _binding
    private lateinit var helper: PreferenceHelper
    private val viewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPersonalInfoBinding.inflate(inflater, container, false)
        helper = PreferenceHelper.getPref(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val user = helper.getCurrentUser()

        binding.apply {
            user?.name.let {
                binding.legalName.text = it
            }
            user?.email.let {
                binding.emailAddress.text = it
            }
            user?.phone.let {
                binding.phoneNo.text = it
            }
            user?.location.let {
                binding.address.text = it
            }
            user?.caregiverType.let {
                binding.levelstatus.text = it
            }

            user?.profileImage.let {
                if (!it.isNullOrEmpty()) {
                    loadImageWithBaseUrlFallback(binding.userImage, it)
                }
            }
            back.setOnClickListener {
                findNavController().navigateUp()
            }

            deleteAccount.setOnClickListener {
                showDeleteAccountDialog()
            }

        }
    }

    @SuppressLint("MissingInflatedId")
    private fun showDeleteAccountDialog() {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_delete_account, null)

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(true)
            .create()

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()

        val btnYes = dialogView.findViewById<MaterialButton>(R.id.button_yes)
        val btnClose = dialogView.findViewById<ImageView>(R.id.cross)
        val progressBar = dialogView.findViewById<ProgressBar>(R.id.progressBar)

        btnClose.setOnClickListener {
            dialog.dismiss()
        }

        btnYes.setOnClickListener {
            helper.getStringValue(PreferenceKeys.PREF_USER_TOKEN)?.let {
                viewModel.deleteAccount(it)
            }
            viewModel.apiResponse.observe(viewLifecycleOwner) { response ->
                when (response) {
                    is ApiResponse.Success -> {
                        Toast.makeText(context, "Account Deleted", Toast.LENGTH_SHORT)
                            .show()
                        progressBar.visibility = View.GONE
                        helper.saveCurrentUser(null)
                        helper.clearPreferences()
                        dialog.dismiss()
                        startActivity(Intent(context, LoginActivity::class.java))
                        requireActivity().finish()
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