package com.sokoldev.budgo.common.data.models.response

data class PaymentTokenResponse(
    val client_secret: String? = null,
    val customer: String? = null,
    val ephemeral_key: String? = null,
    val publishable_key: String? = null,
    val error: String? = null
)
