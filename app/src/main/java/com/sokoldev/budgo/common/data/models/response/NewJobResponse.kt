package com.sokoldev.budgo.common.data.models.response


import com.google.gson.annotations.SerializedName

data class NewJobResponse(
    @SerializedName("message")
    val message: List<Job>,
    @SerializedName("status")
    val status: Boolean
)