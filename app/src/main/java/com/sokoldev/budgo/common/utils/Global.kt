package com.sokoldev.learnicity.utils

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.provider.MediaStore
import android.view.Gravity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import java.io.File
import java.io.IOException
import java.util.Locale


object Global {
    fun getAddress(
        context: Context,
        latitude: String,
        longitude: String
    ): Address? {
        try {
            val geocoder = Geocoder(context, Locale.getDefault())
            val addresses: List<Address> =
                geocoder.getFromLocation(latitude.toDouble(), longitude.toDouble(), 1) as List<Address>
            if (addresses.isEmpty()) return null
            return addresses[0]
        } catch (e: IOException) {
            return null

        } catch (e: NumberFormatException) {
            return null
        }
    }




    fun hasPermissions(context: Context, permissions: Array<String>): Boolean {
        var allGranted = false

        for (permission in permissions) {
            allGranted = (ContextCompat.checkSelfPermission(
                context,
                permission
            ) == PackageManager.PERMISSION_GRANTED)

        }

        return allGranted
    }


    fun showMessage(rootView: View, message: String, length: Int = Snackbar.LENGTH_SHORT) {
        val sb = Snackbar.make(rootView, message, length)
        sb.setBackgroundTint(Color.WHITE)
        sb.setTextColor(Color.BLACK)
        val snackBarView: View = sb.view
        val params = snackBarView.layoutParams as FrameLayout.LayoutParams
        params.gravity = Gravity.BOTTOM
        params.setMargins(16,0,16,100)
        sb.show()
    }

    fun showErrorMessage(rootView: View, message: String, length: Int = Snackbar.LENGTH_SHORT) {
        val sb = Snackbar.make(rootView, message, length)
        sb.setBackgroundTint(Color.WHITE)
        sb.setTextColor(Color.RED)
        val snackBarView: View = sb.view
        val params = snackBarView.layoutParams as FrameLayout.LayoutParams
        params.gravity = Gravity.TOP
        params.setMargins(16,100,16,16)
        snackBarView.layoutParams = params
        sb.show()
    }

    fun uriToFile(context: Context, uri: Uri): File? {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = context.contentResolver.query(uri, projection, null, null, null)
        cursor?.let {
            val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor.moveToFirst()
            val filePath = cursor.getString(columnIndex)
            cursor.close()
            return File(filePath)
        }
        return null
    }



    fun Activity.hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        // Find the currently focused view, so we can grab the correct window token from it.
        var view = currentFocus
        if (view == null) {
            view = View(this)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }



}