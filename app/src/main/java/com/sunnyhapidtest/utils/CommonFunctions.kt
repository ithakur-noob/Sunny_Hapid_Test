package com.sunnyhapidtest.utils

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Environment
import android.util.Log
import android.view.Window
import com.sunnyhapidtest.R
import java.io.File
import java.io.IOException

object CommonFunctions {
    private const val JPEG_FILE_PREFIX = "IMG_"
    private const val JPEG_FILE_SUFFIX = ".jpg"
    private var dialog: Dialog? = null
    @Throws(IOException::class)
    fun setUpImageFile(imageDirectory: String): File? {
        var imageFile: File? = null
        if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()) {
            val storageDir = File(imageDirectory)
            if (!storageDir.mkdirs()) {
                if (!storageDir.exists()) {
                    Log.d("CameraSample", "failed to create directory")
                    return null
                }
            }
            imageFile = File.createTempFile(
                JPEG_FILE_PREFIX + System.currentTimeMillis() + "_",
                JPEG_FILE_SUFFIX, storageDir
            )
        }
        return imageFile
    }

    fun isNougatDevice(): Boolean {
        return Build.VERSION.SDK_INT >= 24
    }

    fun showProgress(activity: Context) {
        if (dialog != null) {
            if (dialog!!.isShowing) {
                return

            }
        }
        try {
            dialog = Dialog(activity)
            dialog!!.window!!.requestFeature(Window.FEATURE_NO_TITLE)
            dialog!!.setContentView(R.layout.progress_bar)
            dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog!!.setCanceledOnTouchOutside(false)
            dialog!!.setCancelable(false)

            dialog!!.show()
        } catch (e: Exception) {
            e.printStackTrace()
            Log.i("ProgessError", e.message!!)
        }

    }

    fun dismissProgress() {
        try {
            if (dialog != null) {
                if (dialog!!.isShowing) {
                    dialog!!.dismiss()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
}