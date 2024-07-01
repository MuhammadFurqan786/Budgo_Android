package com.sokoldev.budgo.patient.models

data class Payment(
    val cardImage: Int,
    val cardName: String,
    val cardDetails: String,
    var isSelected:Boolean = false
)
