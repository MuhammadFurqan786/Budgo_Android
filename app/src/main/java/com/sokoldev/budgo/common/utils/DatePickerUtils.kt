package com.sokoldev.budgo.patient.utils

import android.app.DatePickerDialog
import android.content.Context
import java.util.*

object DatePickerUtils {

    fun showDatePicker(context: Context, callback: (String) -> Unit) {
        // Get the current date as the default date in the dialog
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        // Create a DatePickerDialog and pass the selected date to the callback
        DatePickerDialog(context, { _, selectedYear, selectedMonth, selectedDay ->
            val selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
            callback(selectedDate)
        }, year, month, day).show()
    }
}
