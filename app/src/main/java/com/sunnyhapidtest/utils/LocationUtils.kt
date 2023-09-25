import android.Manifest
import android.content.Context
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Looper
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsStatusCodes
import com.sunnyhapidtest.utils.LocationUpdateListener
import java.io.IOException
import java.util.Locale

object LocationUtils {

    private const val REQUEST_CHECK_SETTINGS = 101
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var locationCallback: LocationCallback? = null

    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    private var lastLocation: Location? = null
    private lateinit var locationUpdateListener: LocationUpdateListener

    fun initPermissionLauncher(fragment: Fragment) {
        permissionLauncher =
            fragment.registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                if (isGranted) {
                    // Permission granted, proceed to request location updates
                    requestLocationUpdates(fragment)
                }
            }
    }

    fun stopLocationUpdates() {
        if (::fusedLocationClient.isInitialized && locationCallback != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback!!)
        }
    }

    fun requestLocationUpdates(
        fragment: Fragment
    ) {
        locationCallback = getLocationCallback()

        if (!::fusedLocationClient.isInitialized) {
            fusedLocationClient =
                LocationServices.getFusedLocationProviderClient(fragment.requireContext())
        }

        if (checkLocationPermission(fragment.requireContext())) {
            val locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(5000)
                .setFastestInterval(3000)

            val builder = LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest)

            val result = LocationServices.getSettingsClient(fragment.requireContext())
                .checkLocationSettings(builder.build())

            result.addOnCompleteListener { task ->
                try {
                    task.getResult(ApiException::class.java)
                    fusedLocationClient.requestLocationUpdates(
                        locationRequest,
                        locationCallback!!,
                        Looper.getMainLooper()
                    )
                } catch (exception: ApiException) {
                    if (exception.statusCode == LocationSettingsStatusCodes.RESOLUTION_REQUIRED) {
                        try {
                            val resolvable = exception as ResolvableApiException
                            resolvable.startResolutionForResult(
                                fragment.requireActivity(),
                                REQUEST_CHECK_SETTINGS
                            )
                        } catch (e: IntentSender.SendIntentException) {
                            // Ignore the error.
                        }
                    }
                }
            }
        } else {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    fun checkLocationPermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun updateLocation(locationUpdateListener: LocationUpdateListener) {
        this.locationUpdateListener = locationUpdateListener
    }

    private fun getLocationCallback(): LocationCallback {
        return object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                // Handle location updates here
                locationUpdateListener.onLocationUpdate(locationResult.lastLocation)
                stopLocationUpdates()
            }
        }
    }

    fun isGeocoderAvailable(context: Context): Boolean {
        return Geocoder.isPresent()
    }

    fun getAddressFromLocation(context: Context, latitude: Double, longitude: Double): String {
        val geocoder = Geocoder(context, Locale.getDefault())
        var addressText = ""

        try {
            val addresses: List<Address>? = geocoder.getFromLocation(latitude, longitude, 1)

            addressText = if (!addresses.isNullOrEmpty()) {
                (addresses[0].adminArea ?: "")+", " + (addresses[0].countryName
                    ?: "")+", " + (addresses[0].postalCode ?: "")
            } else {
                "No address found"
            }
        } catch (e: IOException) {
            Log.e("Geocoder", "Error getting address: ${e.message}")
        }

        return addressText
    }

}
