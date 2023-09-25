package com.sunnyhapidtest.webServices

import com.sunnyhapidtest.webServices.ApiService
import com.sunnyhapidtest.webServices.UploadResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import javax.inject.Inject

 class Repository @Inject constructor(private val apiService: ApiService) {

    suspend fun uploadImage(
        firstName: RequestBody,
        lastName: RequestBody,
        phone: RequestBody,
        address: RequestBody,
        image: MultipartBody.Part?
    ): Response<UploadResponse> {
        return apiService.uploadImage(firstName, lastName, phone, address, image)
    }
}
