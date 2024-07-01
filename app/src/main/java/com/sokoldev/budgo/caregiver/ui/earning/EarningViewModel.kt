package com.sokoldev.budgo.caregiver.ui.earning

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class EarningViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is Earning Fragment"
    }
    val text: LiveData<String> = _text
}