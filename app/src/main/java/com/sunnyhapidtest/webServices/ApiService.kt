package com.sunnyhapidtest.webServices

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import javax.inject.Inject

interface ApiService  {
    @Multipart
    @POST("your_api_endpoint")
    suspend fun uploadImage(
        @Part("first_name") firstName: RequestBody,
        @Part("last_name") lastName: RequestBody,
        @Part("phone") phone: RequestBody,
        @Part("address") address: RequestBody,
        @Part image: MultipartBody.Part?
    ): Response<UploadResponse>
}