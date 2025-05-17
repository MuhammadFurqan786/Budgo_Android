package com.sokoldev.budgo.common.data.models.response

data class DispensoryResponse(
    val success: Boolean,
    val message: String,
    val data: List<Dispensory>
)
