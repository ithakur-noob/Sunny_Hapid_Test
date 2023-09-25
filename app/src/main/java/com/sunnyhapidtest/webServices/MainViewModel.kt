package com.sunnyhapidtest.webServices

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.scopes.FragmentScoped
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val repository: Repository) : ViewModel() {

    private val _uploadResponse = MutableLiveData<UploadResponse>()
    val uploadResponse: LiveData<UploadResponse> get() = _uploadResponse

    fun uploadImage(
        firstName: String,
        lastName: String,
        phone: String,
        address: String,
        imageFile: File?
    ) {
        val firstNamePart = createPartFromString(firstName)
        val lastNamePart = createPartFromString(lastName)
        val phonePart = createPartFromString(phone)
        val addressPart = createPartFromString(address)
        val imagePart = if (imageFile != null) createImagePart("image", imageFile) else null

        viewModelScope.launch {
            try {
                val response = repository.uploadImage(
                    firstNamePart,
                    lastNamePart,
                    phonePart,
                    addressPart,
                    imagePart
                )
                _uploadResponse.postValue(response.body())
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    private fun createPartFromString(value: String): RequestBody {
        return value.toRequestBody(MultipartBody.FORM)
    }

    private fun createImagePart(partName: String, file: File): MultipartBody.Part {
        val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData(partName, file.name, requestFile)
    }
}
