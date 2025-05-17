package com.sokoldev.budgo.caregiver.ui.task

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.sokoldev.budgo.common.utils.prefs.PreferenceHelper
import com.sokoldev.budgo.common.utils.prefs.PreferenceKeys
import com.sokoldev.budgo.databinding.ActivityScanBinding
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class ScanActivity : AppCompatActivity() {
    private lateinit var binding: ActivityScanBinding
    private lateinit var cameraExecutor: ExecutorService
    private var imageCapture: ImageCapture? = null
    private var photoFile: File? = null
    private var patientCardFront: String? = null
    private lateinit var helper: PreferenceHelper
    private var patientLat: Double? = null
    private var patientLon: Double? = null
    private var bookingId: String? = null
    private var patientId: String? = null
    private var caregiverId: String? = null

    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        helper = PreferenceHelper.getPref(this)
        patientLat = intent.getDoubleExtra("latitude", 0.0)
        patientLon = intent.getDoubleExtra("longitude", 0.0)
        bookingId = intent.getStringExtra(PreferenceKeys.BOOKING_ID)
        patientId = intent.getStringExtra("patientId")
        caregiverId = intent.getStringExtra("caregiverId")
        patientCardFront = helper.getStringValue(PreferenceKeys.PATIENT_CARD_FRONT)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) ==
            PackageManager.PERMISSION_GRANTED
        ) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 10)
        }

        cameraExecutor = Executors.newSingleThreadExecutor()

        binding.captureButton.setOnClickListener { takePhoto() }
        binding.resetButton.setOnClickListener { resetPreview() }
        binding.doneButton.setOnClickListener {
            startActivity(
                Intent(this@ScanActivity, ToDoActivity::class.java)
                    .putExtra("latitude", patientLat)
                    .putExtra("longitude", patientLon)
                    .putExtra(PreferenceKeys.BOOKING_ID, bookingId)
                    .putExtra("patientId", patientId)
                    .putExtra("caregiverId", caregiverId)
                    .putExtra("isDone", true)
            )
        }

        binding.resetButton.visibility = View.GONE
        binding.capturedImage.visibility = View.GONE
        binding.cardImageFromDB.visibility = View.GONE // Hide fallback initially
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
            }

            imageCapture = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
                .build()

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)
            } catch (e: Exception) {
                Log.e("ScanActivity", "Use case binding failed", e)
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun takePhoto() {
        val imageCapture = imageCapture ?: return

        photoFile = File.createTempFile("IMG_", ".jpg", cacheDir)
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile!!).build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(e: ImageCaptureException) {
                    Log.e("ScanActivity", "Photo capture failed: ${e.message}", e)
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val savedUri = Uri.fromFile(photoFile)
                    Log.d("ScanActivity", "Photo saved: $savedUri")
                    displayImageAndAnalyze(savedUri)
                }
            }
        )
    }

    private fun displayImageAndAnalyze(uri: Uri) {
        val fullBitmap = BitmapFactory.decodeFile(uri.path)
        if (fullBitmap == null) {
            Log.e("ScanActivity", "Failed to decode bitmap.")
            return
        }

        val cropWidth = (fullBitmap.width * 0.8).toInt()
        val cropHeight = (fullBitmap.height * 0.4).toInt()
        val x = (fullBitmap.width - cropWidth) / 2
        val y = (fullBitmap.height - cropHeight) / 2

        val croppedBitmap = Bitmap.createBitmap(fullBitmap, x, y, cropWidth, cropHeight)

        binding.capturedImage.setImageBitmap(croppedBitmap)
        binding.capturedImage.visibility = View.VISIBLE
        binding.viewFinder.visibility = View.GONE
        binding.scannerFrame.visibility = View.GONE
        binding.captureButton.isEnabled = false
        binding.resetButton.visibility = View.VISIBLE

        analyzeText(croppedBitmap)
    }

    private fun analyzeText(bitmap: Bitmap) {
        val image = InputImage.fromBitmap(bitmap, 0)

        recognizer.process(image)
            .addOnSuccessListener { visionText ->
                val rawText = visionText.text
                Log.d("ScanActivity", "OCR Text:\n$rawText")

                if (rawText.contains("PATIENT", true) &&
                    rawText.contains("Medical Marijuana Program", true)
                ) {

                    val regex = Regex(
                        "EXPIRES[\\s:\\n]*([0-9]{2}/[0-9]{2}/[0-9]{4})",
                        RegexOption.IGNORE_CASE
                    )
                    val match = regex.find(rawText)

                    if (match != null) {
                        val dateStr = match.groupValues.getOrNull(1)
                        Log.d("ScanActivity", "Expiry: $dateStr")
                        dateStr?.let {
                            val sdf = SimpleDateFormat("MM/dd/yyyy", Locale.US)
                            try {
                                val expiry = sdf.parse(it)
                                val today = Date()
                                if (expiry != null && expiry.after(today)) {
                                    binding.doneButton.isEnabled = true
                                    Toast.makeText(this, "✅ Valid Card", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(this, "❌ Card Expired", Toast.LENGTH_SHORT)
                                        .show()
                                }
                            } catch (e: Exception) {
                                Log.e("ScanActivity", "Parse Error", e)
                            }
                        }
                    } else {
                        Toast.makeText(this, "❌ Expiry date not found", Toast.LENGTH_SHORT).show()
                        showFallbackImage()
                    }
                } else {
                    Toast.makeText(this, "❌ Required keywords not found", Toast.LENGTH_SHORT).show()
                    showFallbackImage()
                }
            }
            .addOnFailureListener {
                Log.e("ScanActivity", "OCR failed", it)
                Toast.makeText(this, "OCR failed. Showing fallback image.", Toast.LENGTH_SHORT)
                    .show()
                showFallbackImage()
            }
    }

    private fun showFallbackImage() {
        binding.viewFinder.visibility = View.GONE
        binding.scannerFrame.visibility = View.GONE
        binding.capturedImage.visibility = View.GONE
        binding.captureButton.visibility = View.GONE
        binding.resetButton.visibility = View.GONE
        binding.cardImageFromDB.visibility = View.VISIBLE
        binding.doneButton.visibility = View.VISIBLE

        val fallbackImageUrl = patientCardFront
        val modifiedUrl = fallbackImageUrl?.replace(
            "https://budgo.net/budgo/public/",
            "https://admin.budgo.net/"
        )

        // Build the fallback request
        val fallbackRequest = Glide.with(this)
            .load(modifiedUrl)

        // Start primary request with fallback using .error()
        Glide.with(this)
            .load(fallbackImageUrl)
            .error(fallbackRequest) // Automatically tries this if first fails
            .into(binding.cardImageFromDB)

    }

    private fun resetPreview() {
        binding.capturedImage.visibility = View.GONE
        binding.cardImageFromDB.visibility = View.GONE
        binding.viewFinder.visibility = View.VISIBLE
        binding.scannerFrame.visibility = View.VISIBLE
        binding.captureButton.visibility = View.VISIBLE
        binding.captureButton.isEnabled = true
        binding.doneButton.visibility = View.GONE
        binding.resetButton.visibility = View.GONE
        photoFile = null
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }
}
