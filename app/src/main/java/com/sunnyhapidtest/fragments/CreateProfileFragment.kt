package com.sunnyhapidtest.fragments

import LocationUtils
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.Window
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.location.component1
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.sunnyhapidtest.R
import com.sunnyhapidtest.databinding.FragmentCreateProfileBinding
import com.sunnyhapidtest.databinding.ItemAddPhotoBinding
import com.sunnyhapidtest.utils.CommonFunctions
import com.sunnyhapidtest.utils.LocationUpdateListener
import com.sunnyhapidtest.utils.showSnackBar
import com.sunnyhapidtest.webServices.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class CreateProfileFragment :
    BaseFragment<FragmentCreateProfileBinding>(FragmentCreateProfileBinding::inflate),
    View.OnClickListener, LocationUpdateListener {
    private val LOCAL_STORAGE_BASE_PATH_FOR_MEDIA = Environment
        .getExternalStorageDirectory().toString() + "/" + "SunnyHapidTest"
    private val LOCAL_STORAGE_BASE_PATH_FOR_POSTED_IMAGES =
        "$LOCAL_STORAGE_BASE_PATH_FOR_MEDIA/User/Images/"
    private lateinit var alertDialog: androidx.appcompat.app.AlertDialog
    private lateinit var viewModel: MainViewModel
    private var mPicturePath = ""
    private var imageFile: File? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rlToolbar.tvTitle.text = resources.getString(R.string.create_profile)
        LocationUtils.initPermissionLauncher(this)
        LocationUtils.updateLocation(this)
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        binding.etPhone.setText("+91 ${requireArguments().getString("phone", "")}")

        viewModel.uploadResponse.observe(requireActivity(), Observer {
            // Handle the API response here
        })


        /*All Clicks*/
        binding.ivProfileImage.setOnClickListener(this)
        binding.btnSubmit.setOnClickListener(this)
        binding.rlToolbar.ivBackArrow.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.tvLocation.setOnClickListener(this)

    }

    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val selectedImage = result.data?.data
                val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
                val cursor = requireActivity().contentResolver.query(
                    selectedImage!!,
                    filePathColumn, null, null, null
                )
                cursor?.moveToFirst()
                val columnIndex = cursor?.getColumnIndex(filePathColumn[0])
                val picturePath = cursor?.getString(columnIndex!!)

                imageFile = File(picturePath.toString())
                Glide.with(requireActivity())
                    .load(imageFile)
                    .circleCrop()
                    .into(binding.ivProfileImage)
            }
        }

    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                imageFile = File(mPicturePath)
                Glide.with(requireActivity())
                    .load(imageFile)
                    .circleCrop()
                    .into(binding.ivProfileImage)
            }
        }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryLauncher.launch(intent)
    }

    private fun startCameraIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        var f: File? = null
        try {
            f = CommonFunctions.setUpImageFile(LOCAL_STORAGE_BASE_PATH_FOR_POSTED_IMAGES)
            mPicturePath = f!!.absolutePath
            /* add provider in xml and
             * manifest then add following code for Nougat devices
             * to overcome file uri exposed app crash
             */
            if (CommonFunctions.isNougatDevice()) {
                takePictureIntent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                val contentUri = FileProvider.getUriForFile(
                    requireContext(), "com.sunnyhapidtest", f
                )
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri)
            } else {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            f = null
            mPicturePath = ""
        }
        cameraLauncher.launch(takePictureIntent)
    }

    private val requestMultiplePermissionsLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            // Check if all permissions are granted
            val allGranted = permissions.all { it.value }
            if (allGranted) {
                alertPictureOptions()
            } else {
                requestMultiplePermissions()
            }
        }

    private val permissionsToRequest = arrayOf(
        android.Manifest.permission.READ_EXTERNAL_STORAGE,
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
        android.Manifest.permission.CAMERA
    )

    private fun requestMultiplePermissions() {
        requestMultiplePermissionsLauncher.launch(permissionsToRequest)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.ivProfileImage -> {
                if (arePermissionsGranted())
                    alertPictureOptions()
                else
                    requestMultiplePermissions()
            }

            R.id.btnSubmit -> {
                if (binding.etFirstName.text.toString().trim().isEmpty())
                    binding.root.showSnackBar("Please Enter First Name")
                else if (binding.etLastName.text.toString().trim().isEmpty())
                    binding.root.showSnackBar("Please Enter Last Name")
                else if (binding.etPhone.text.toString().trim().isEmpty())
                    binding.root.showSnackBar("Please Enter Phone")
                else if (binding.etPostalCode.text.toString().trim().isEmpty())
                    binding.root.showSnackBar("Please Enter Address")
                else {
                    /*Change of baseurl and api end point required*/
                    viewModel.uploadImage(
                        binding.etFirstName.text.toString(),
                        binding.etLastName.text.toString(),
                        binding.etPhone.text.toString(),
                        binding.etPostalCode.text.toString(),
                        imageFile
                    )
                    /*Showing api hitting effect */
                    CommonFunctions.showProgress(requireActivity())
                    Handler(Looper.getMainLooper()).postDelayed({
                        CommonFunctions.dismissProgress()
                        binding.root.showSnackBar("Demo Api Hit Successfully")
                    }, 3000)
                }
            }

            R.id.tvLocation -> {
                LocationUtils.requestLocationUpdates(this)
                binding.root.showSnackBar("Fetching location.....")
            }
        }
    }

    private fun alertPictureOptions() {
        val builder = androidx.appcompat.app.AlertDialog.Builder(requireActivity())
        val dialogView = ItemAddPhotoBinding.inflate(layoutInflater)
        builder.setView(dialogView.root)
        alertDialog = builder.create()
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialogView.tvGallery.setOnClickListener {
            pickImageFromGallery()
            alertDialog.dismiss()
        }
        dialogView.tvCamera.setOnClickListener {
            startCameraIntent()
            alertDialog.dismiss()
        }
        dialogView.tvCancel.setOnClickListener { alertDialog.dismiss() }
        alertDialog.show()
    }

    private fun arePermissionsGranted(): Boolean {
        return permissionsToRequest.all {
            ContextCompat.checkSelfPermission(
                requireActivity(),
                it
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    override fun onLocationUpdate(location: Location?) {
        if (LocationUtils.isGeocoderAvailable(requireContext())) {
            val address = LocationUtils.getAddressFromLocation(
                requireContext(),
                location!!.latitude,
                location.longitude
            )
            binding.etPostalCode.setText("$address")
        } else {
            Log.e("Geocoder", "Geocoder is not available on this device.")
        }
    }
}