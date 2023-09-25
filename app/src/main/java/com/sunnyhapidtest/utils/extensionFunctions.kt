package com.sunnyhapidtest.utils

import android.view.View
import com.google.android.material.snackbar.Snackbar

fun String.getFirstAndLastTwo(): String {
    if (length < 4) {
        throw IllegalArgumentException("String length should be at least 2")
    }

    val firstTwo = substring(0, 2)
    val lastTwo = substring(length - 2, length)

    return "$firstTwo$lastTwo"
}
fun View.showSnackBar(message: String, duration: Int = Snackbar.LENGTH_SHORT) {
    Snackbar.make(this, message, duration).show()
}