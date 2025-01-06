package com.sokoldev.budgo.patient.ui.user

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
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
        binding.edDob.setOnClickListener {
            DatePickerUtils.showDatePicker(this) { date ->
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
        viewModel.apiResponse.observe(this, Observer {
            when (it) {
                is ApiResponse.Success -> {
                    binding.spinKit.visibility = View.GONE
                    if (it.data.status) {
                        Global.showMessage(
                            binding.root.rootView,
                            it.data.message,
                            Snackbar.LENGTH_SHORT
                        )

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
                    binding.spinKit.visibility = View.GONE
                }

                ApiResponse.Loading -> {
                    binding.spinKit.visibility = View.VISIBLE
                }
            }

        })
    }


    private fun checkPermissionsAndSelectImageOption(imageType: Int) {
        if (hasRequiredPermissions()) {
            selectImageOption(imageType)
        } else {
            requestPermissions()
        }
    }

    private fun hasRequiredPermissions(): Boolean {
        val cameraPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
        val readStoragePermission =
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)

        return cameraPermission == PackageManager.PERMISSION_GRANTED &&
                readStoragePermission == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions() {
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


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PERMISSIONS) {
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                Toast.makeText(this, "Permissions granted.", Toast.LENGTH_SHORT).show()
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
        val galleryIntent = Intent(Intent.ACTION_PICK)
        galleryIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
        startActivityForResult(galleryIntent, imageType + REQUEST_IMAGE_PICK)
    }

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
    private fun getFileFromIntent(data: Intent): File? {
        val selectedImageUri = data.data
        selectedImageUri?.let { uri ->
            val filePath = getFilePathFromUri(uri)
            return File(filePath)
        }
        return null
    }

    // Method to get the file path from the URI
    private fun getFilePathFromUri(uri: Uri): String {
        var path = ""
        contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor.moveToFirst()
            path = cursor.getString(columnIndex)
        }
        return path
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