package com.sunnyhapidtest.utils

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class ApplicationGlobal:Application() {
    override fun onCreate() {
        super.onCreate()
    }
}