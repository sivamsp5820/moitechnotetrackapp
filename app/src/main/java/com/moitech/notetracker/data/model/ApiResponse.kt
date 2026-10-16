package com.moitech.notetracker.data.model

import com.google.gson.annotations.SerializedName

data class ApiResponse<T>(
    @SerializedName("success") val success: Boolean,
    @SerializedName("count") val count: Int? = null,
    @SerializedName("data") val data: T? = null,
    @SerializedName("message") val message: String? = null
)
