package com.sokoldev.budgo.common.utils

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import androidx.fragment.app.FragmentActivity
import com.google.android.material.datepicker.MaterialDatePicker
import com.sokoldev.budgo.R
import java.text.SimpleDateFormat
import java.util.*

object DatePickerUtils {

    fun showDatePicker(context: Context, callback: (String) -> Unit) {
        // Get the current date as the default date in the dialog
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val themeRes = R.style.CustomDatePickerTheme  // Replace with your desired theme
        // Create a DatePickerDialog and pass the selected date to the callback
        DatePickerDialog(context,themeRes, { _, selectedYear, selectedMonth, selectedDay ->
            val selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
            callback(selectedDate)
        }, year, month, day).show()
    }

    fun showMaterialDatePicker(context: FragmentActivity, callback: (String) -> Unit) {
        val picker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select Date")
            .build()

        picker.addOnPositiveButtonClickListener {
            // Format and return the selected date
            val selectedDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(it))
            callback(selectedDate)
        }

        picker.show(context.supportFragmentManager, picker.toString())
    }


}
