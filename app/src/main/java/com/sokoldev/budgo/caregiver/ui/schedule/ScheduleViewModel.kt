package com.sokoldev.budgo.caregiver.ui.schedule

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ScheduleViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is Schedule Fragment"
    }
    val text: LiveData<String> = _text
}