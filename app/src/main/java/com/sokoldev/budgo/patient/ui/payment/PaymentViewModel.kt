package com.sokoldev.budgo.patient.ui.payment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sokoldev.budgo.R
import com.sokoldev.budgo.patient.models.Payment

class PaymentViewModel : ViewModel() {

    private val _listPaymentMethod: MutableLiveData<List<Payment>> = MutableLiveData()
    val listPaymentMethod: LiveData<List<Payment>>
        get() = _listPaymentMethod


    fun getPaymentMethods() {
        val arraylist = ArrayList<Payment>()
        arraylist.add(Payment(R.drawable.ic_visa, "Visa Card", "**** **** **** 1452"))
        arraylist.add(Payment(R.drawable.ic_master, "Master Card", "**** **** **** 4354"))

        _listPaymentMethod.value = arraylist
    }

}