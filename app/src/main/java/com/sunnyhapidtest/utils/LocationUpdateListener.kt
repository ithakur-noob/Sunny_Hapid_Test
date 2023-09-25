package com.sunnyhapidtest.utils

import android.location.Location

interface LocationUpdateListener {
    fun onLocationUpdate(location: Location?)
}
