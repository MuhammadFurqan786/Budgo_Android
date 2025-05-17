package com.sokoldev.budgo.patient.ui.user

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.InputType
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.sokoldev.budgo.R
import com.sokoldev.budgo.common.data.remote.network.ApiResponse
import com.sokoldev.budgo.common.ui.user.LoginActivity
import com.sokoldev.budgo.common.utils.DatePickerUtils
import com.sokoldev.budgo.common.utils.GPSTracker
import com.sokoldev.budgo.common.utils.Global
import com.sokoldev.budgo.common.utils.prefs.PreferenceHelper
import com.sokoldev.budgo.common.utils.prefs.PreferenceKeys
import com.sokoldev.budgo.common.viewmodels.UserViewModel
import com.sokoldev.budgo.databinding.ActivityUserRegistrationBinding
import com.sokoldev.budgo.patient.ui.home.HomeActivity
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

class UserRegistrationActivity : AppCompatActivity() {
    private lateinit var binding:ActivityUserRegistrationBinding
    private lateinit var viewModel: UserViewModel
    private val REQUEST_IMAGE_CAPTURE = 300
    private val REQUEST_IMAGE_PICK = 100
    private val REQUEST_PERMISSIONS = 200

    private val REQUEST_IMAGE_PICK_FRONT_CARD = 101
    private val REQUEST_IMAGE_PICK_BACK_CARD = 102
    private var lastSelectedImageType: Int = 1

    private var frontCardFile: File? = null
    private var backCardFile: File? = null
    private lateinit var helper: PreferenceHelper
    private var latitude: String = ""
    private var longitude: String = ""



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityUserRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        viewModel = ViewModelProvider(this)[UserViewModel::class.java]
        helper = PreferenceHelper.getPref(this)
        getLatLon()
        binding.edDob.apply {
            inputType = InputType.TYPE_NULL
            isFocusable = false
            isClickable = true
        }
        binding.edDob.setOnClickListener {

            DatePickerUtils.showMaterialDatePicker(this) { date ->
                binding.edDob.setText(date)
            }
        }

        binding.login.setOnClickListener { startActivity(Intent(this, LoginActivity::class.java)) }

        binding.patientCardFront.setOnClickListener { checkPermissionsAndSelectImageOption(1) }
        binding.patientCardBack.setOnClickListener { checkPermissionsAndSelectImageOption(2) }


        binding.continueButton.setOnClickListener {
            registerUser()
        }


        initObserver()

    }

    private fun initObserver() {
        viewModel.apiResponseLogin.observe(this, Observer {
            when (it) {
                is ApiResponse.Success -> {
                    binding.loadingView.visibility = View.GONE
                    binding.loadingView.hide()
                    if (it.data.data != null) {
                        val user = it.data.data.user
                        helper.saveCurrentUser(user)
                        helper.saveStringValue(PreferenceKeys.PREF_USER_TOKEN, it.data.data.token)
                        helper.setUserLogin(true)
                        helper.setPatientUser(true)
                        Log.d("TOOKEN", "" + it.data.data.token)

                        Global.showMessage(
                            binding.root.rootView,
                            it.data.message,
                            Snackbar.LENGTH_SHORT
                        )
                        val userType = it.data.data.user.type
                        if (userType == "1") {
                            val intent =
                                Intent(this@UserRegistrationActivity, HomeActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            val intent =
                                Intent(this@UserRegistrationActivity, LoginActivity::class.java)
                            startActivity(intent)
                            finish()
                        }


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


    private fun checkPermissionsAndSelectImageOption(imageType: Int) {
        lastSelectedImageType = imageType
        if (hasRequiredPermissions()) {
            selectImageOption(imageType)
        } else {
            requestPermissions()
        }
    }


    private fun hasRequiredPermissions(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) { // Android 14+
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
            ) == PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.READ_MEDIA_IMAGES
                    ) == PackageManager.PERMISSION_GRANTED
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_MEDIA_IMAGES
            ) == PackageManager.PERMISSION_GRANTED
        } else { // Below Android 13
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        }
    }


    private fun requestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) { // Android 14+
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.READ_MEDIA_VIDEO,
                    Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
                ),
                REQUEST_PERMISSIONS
            )
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.READ_MEDIA_VIDEO
                ),
                REQUEST_PERMISSIONS
            )
        } else { // Below Android 13
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ),
                REQUEST_PERMISSIONS
            )
        }
    }



    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PERMISSIONS) {
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                Toast.makeText(this, "Permissions granted.", Toast.LENGTH_SHORT).show()
                selectImageOption(lastSelectedImageType)
            } else {
                Toast.makeText(this, "Permissions are required to proceed.", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }


    private fun selectImageOption(imageType: Int) {
        val options = arrayOf("Take Photo", "Choose from Gallery")
        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
        builder.setTitle("Select Option")
        builder.setItems(options) { _, which ->
            when (which) {
                0 -> openCamera(imageType)
                1 -> openGallery(imageType)
            }
        }
        builder.show()
    }

    private fun openCamera(imageType: Int) {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(cameraIntent, imageType + REQUEST_IMAGE_CAPTURE)
    }

    private fun openGallery(imageType: Int) {
        val pickIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13+
            Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                type = "image/*"
                addCategory(Intent.CATEGORY_OPENABLE)
                putExtra(
                    Intent.EXTRA_ALLOW_MULTIPLE,
                    false
                ) // Change to true for multiple selection
            }
        } else {
            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).apply {
                type = "image/*"
            }
        }
        startActivityForResult(pickIntent, imageType + REQUEST_IMAGE_PICK)
    }


    @Deprecated("This method has been deprecated in favor of using the Activity Result API\n      which brings increased type safety via an {@link ActivityResultContract} and the prebuilt\n      contracts for common intents available in\n      {@link androidx.activity.result.contract.ActivityResultContracts}, provides hooks for\n      testing, and allow receiving results in separate, testable classes independent from your\n      activity. Use\n      {@link #registerForActivityResult(ActivityResultContract, ActivityResultCallback)}\n      with the appropriate {@link ActivityResultContract} and handling the result in the\n      {@link ActivityResultCallback#onActivityResult(Object) callback}.")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_IMAGE_PICK_FRONT_CARD -> {
                    frontCardFile = data?.let { getFileFromIntent(it) }
                }

                REQUEST_IMAGE_PICK_BACK_CARD -> {
                    backCardFile = data?.let { getFileFromIntent(it) }
                }


                REQUEST_IMAGE_CAPTURE + 1 -> {
                    val bitmap = data?.extras?.get("data") as Bitmap
                    frontCardFile = convertBitmapToFile(bitmap, "front_card.jpg")
                    binding.patientCardFront.text = frontCardFile!!.name.toString()
                }

                REQUEST_IMAGE_CAPTURE + 2 -> {
                    val bitmap = data?.extras?.get("data") as Bitmap
                    backCardFile = convertBitmapToFile(bitmap, "back_card.jpg")
                    binding.patientCardBack.text = backCardFile!!.name.toString()
                }

            }
        }
    }

    // Utility function to extract the file from the Intent data
    // Utility function to extract the file from the Intent data
    private fun getFileFromIntent(data: Intent): File? {
        val selectedImageUri: Uri? = when {
            data.clipData != null -> data.clipData?.getItemAt(0)?.uri
            data.data != null -> data.data
            else -> null
        }

        selectedImageUri?.let { uri ->
            try {
                // Request persistable permission if needed
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    contentResolver.takePersistableUriPermission(
                        uri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                }

                // Use DocumentFile to access the URI
                val docFile = androidx.documentfile.provider.DocumentFile.fromSingleUri(this, uri)
                docFile?.let { file ->
                    val inputStream = contentResolver.openInputStream(uri)
                    val tempFile = File.createTempFile("temp_image", ".jpg", cacheDir)
                    val outputStream = FileOutputStream(tempFile)
                    inputStream?.copyTo(outputStream)
                    inputStream?.close()
                    outputStream.close()
                    return tempFile
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return null
    }


    private fun registerUser() {
        val name = binding.edName.text.toString().trim()
        val email = binding.edEmail.text.toString().trim()
        val phone = binding.edPhone.text.toString().trim()
        val dob = binding.edDob.text.toString().trim()
        val password = binding.edPassword.text.toString().trim()

        if (name.isEmpty()) {
            binding.edName.error = "Please add your name"
            return
        } else if (email.isEmpty()) {
            binding.edEmail.error = "Please add your email address"
            return
        } else if (phone.isEmpty()) {
            binding.edPhone.error = "Please add your phone"
            return
        } else if (dob.isEmpty()) {
            binding.edDob.error = "Please add your date of birth"
            return
        } else if (password.isEmpty()) {
            binding.edPassword.error = "Please add your password"
            return
        } else if (password.length < 8) {
            binding.edPassword.error = "Password length should be 8 characters"
            return
        } else if (frontCardFile == null || backCardFile == null) {
            Global.showMessage(binding.root.rootView, "Please add images first", Toast.LENGTH_LONG)
            return
        }

        if (latitude.isNotEmpty() && longitude.isNotEmpty()) {
            viewModel.registerPatient(
                name,
                email,
                phone,
                dob,
                latitude,
                longitude,
                password,
                "1",
                frontCardFile!!,
                backCardFile!!
            )
        }


    }


    private fun convertBitmapToFile(bitmap: Bitmap, fileName: String): File {
        val file = File(cacheDir, fileName)
        file.createNewFile()
        val bos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos)
        val bitmapData = bos.toByteArray()
        val fos = FileOutputStream(file)
        fos.write(bitmapData)
        fos.flush()
        fos.close()
        return file
    }


    private fun getLatLon() {
        val gpsTracker = GPSTracker(this)

        if (gpsTracker.isGPSTrackingEnabled) {
            latitude = gpsTracker.getLatitude().toString()
            longitude = gpsTracker.getLongitude().toString()

            helper.saveStringValue(PreferenceKeys.KEY_LATITUDE, latitude)
            helper.saveStringValue(PreferenceKeys.KEY_LONGITUDE, longitude)
            Global.showMessage(
                binding.root.rootView,
                "Latitude : $latitude == Longitude:$longitude",
                Toast.LENGTH_LONG
            )

        } else {
            gpsTracker.showSettingsAlert()
        }
    }


}