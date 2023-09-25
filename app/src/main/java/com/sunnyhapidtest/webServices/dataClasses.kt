package com.sunnyhapidtest.webServices

import com.google.gson.annotations.SerializedName


data class UploadResponse(
    @SerializedName("message") val message: String,
    @SerializedName("imageUrl") val imageUrl: String
)
