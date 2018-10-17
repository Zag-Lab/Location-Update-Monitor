package io.zaglab.android.locationupdatemonitor.main

import android.Manifest
import android.app.Activity
import android.app.PendingIntent
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.material.snackbar.Snackbar
import io.zaglab.android.locationupdatemonitor.BaseActivity
import io.zaglab.android.locationupdatemonitor.BuildConfig
import io.zaglab.android.locationupdatemonitor.R
import io.zaglab.android.locationupdatemonitor.callbacks.EXTRA_LOCATION_REQUEST
import io.zaglab.android.locationupdatemonitor.callbacks.LocationUpdatesBroadcastReceiver
import io.zaglab.android.locationupdatemonitor.callbacks.LocationUpdatesForegroundService
import io.zaglab.android.locationupdatemonitor.databinding.ActivityMainBinding
import io.zaglab.android.locationupdatemonitor.history.HistoryActivity
import io.zaglab.android.locationupdatemonitor.main.RequestBuilderViewModel.Navigator
import io.zaglab.android.locationupdatemonitor.main.RequestBuilderViewModel.Requester
import timber.log.Timber
import java.text.DateFormat
import java.util.*

class MainActivity : BaseActivity(),
        Requester, Navigator {

    /**
     * Provides access to the Fused Location Provider API.
     */
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    /**
     * Provides access to the Location Settings API.
     */
    private lateinit var settingsClient: SettingsClient


    private val log = StringBuilder()

    /**
     * Callback for Location events.
     */
//    private var locationCallback = object : LocationCallback() {
//        override fun onLocationResult(locationResult: LocationResult?) {
//            //todo: persist location
//            val currentLocation = locationResult?.lastLocation
//            val longitude = currentLocation?.longitude
//            val latitude = currentLocation?.latitude
////            val time = currentLocation?.time
//            val time = DateFormat.getTimeInstance().format(Date())
//            log.append("Last location: $longitude, $latitude. Retrieved at $time \n")
//            locationMonitorViewModel.log = log.toString()
//        }
//    }

//    private var locationCallback = object : LocationCallback() {
//        override fun onLocationResult(locationResult: LocationResult?) {
//            //todo: persist location
//            val currentLocation = locationResult?.lastLocation
//            val longitude = currentLocation?.longitude
//            val latitude = currentLocation?.latitude
////            val time = currentLocation?.time
//            val time = DateFormat.getTimeInstance().format(Date())
//            log.append("Last location: $longitude, $latitude. Retrieved at $time \n")
//            locationMonitorViewModel.log = log.toString()
//        }
//    }

    private var locationCallback: LocationCallbackWithRequest? = null

    private fun buildLocationCallback(request: LocationRequest): LocationCallbackWithRequest {
        return object : LocationCallbackWithRequest(request) {
            override fun onLocationResult(locationResult: LocationResult?) {
                //todo: persist location
//                locationResult?.let {
                //                    Database.storeLocations(mapLocations(it.locations))
//                }
                val currentLocation = locationResult?.lastLocation
                val longitude = currentLocation?.longitude
                val latitude = currentLocation?.latitude
//            val time = currentLocation?.time
                val time = DateFormat.getTimeInstance().format(Date())
                log.append("Last location: $longitude, $latitude. Retrieved at $time \n")
                locationMonitorViewModel.log = log.toString()
            }
        }
    }

    open inner class LocationCallbackWithRequest(val request: LocationRequest?) : LocationCallback()

    private val foregroundServiceIntent by lazy {
        Intent(this, LocationUpdatesForegroundService::class.java)
    }

    private val locationPendingIntent by lazy {
        val intent = Intent(this, LocationUpdatesBroadcastReceiver::class.java)
                .apply {
                    action = LocationUpdatesBroadcastReceiver.ACTION_PROCESS_UPDATES
                }
        PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    //TODO: pack request details
//    private fun buildLocationPendingIntent(request: LocationRequest): PendingIntent {
//        val intent = Intent(this, LocationUpdatesBroadcastReceiver::class.java)
//                .apply {
//                    action = LocationUpdatesBroadcastReceiver.ACTION_PROCESS_UPDATES
//                    putLocationRequest(request)
//                }
//        return PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
//    }

    private fun Intent.putLocationRequest(request: LocationRequest) {
        putExtra(EXTRA_LOCATION_REQUEST, request)
    }

    private lateinit var binding: ActivityMainBinding
    private val locationMonitorViewModel = RequestBuilderViewModel(this, this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.vm = locationMonitorViewModel

        if (!checkPermissions()) {
            requestPermissions()
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        settingsClient = LocationServices.getSettingsClient(this)
    }

    override fun issueRequest(request: LocationRequest, callbackType: Requester.CallbackType) {
        startLocationUpdates(request, callbackType)
    }

    override fun cancelRequest(callbackType: Requester.CallbackType) {
        stopLocationUpdates(callbackType)
    }

    @SuppressWarnings("MissingPermission")
    private fun callback(request: LocationRequest) {
        locationCallback = buildLocationCallback(request)
        fusedLocationClient.requestLocationUpdates(request, locationCallback, Looper.myLooper())
    }

    private fun fgService(request: LocationRequest) {
        Timber.e("FG service parcelable request: $request")
        foregroundServiceIntent.putLocationRequest(request)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(foregroundServiceIntent)
        } else {
            startService(foregroundServiceIntent)
        }
    }

    @SuppressWarnings("MissingPermission")
    private fun bgService(request: LocationRequest) {
//        locationPendingIntent = buildLocationPendingIntent(request)
        fusedLocationClient.requestLocationUpdates(request, locationPendingIntent)
    }

    private fun boundService(request: LocationRequest) {
        bindService()
        updatesService?.requestLocationUpdates(request)
    }

//    private fun job(request: LocationRequest) {
//
//    }

    @SuppressWarnings("MissingPermission")
    private fun startLocationUpdates(request: LocationRequest, callbackType: Requester.CallbackType) {
        // Begin by checking if the device has the necessary location settings.
        val locationSettingsRequest = LocationSettingsRequest.Builder()
                .addLocationRequest(request).build()

        settingsClient.checkLocationSettings(locationSettingsRequest)
                .addOnSuccessListener(this) {
                    Timber.i("All location settings are satisfied.")

                    when (callbackType) {
                        Requester.CallbackType.CALLBACK -> callback(request)
                        Requester.CallbackType.FG_SERVICE -> boundService(request)
                        Requester.CallbackType.BG_SERVICE -> bgService(request)
//                        Requester.CallbackType.JOB -> job(request)
                    }
                }
                .addOnFailureListener(this) { e ->
                    val statusCode = (e as ApiException).statusCode
                    when (statusCode) {
                        LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                            Timber.i("Location settings are not satisfied. Attempting to upgrade location settings ")
                            try {
                                // Show the dialog by calling startResolutionForResult(), and check the
                                // result in onActivityResult().
                                val rae = e as ResolvableApiException
                                rae.startResolutionForResult(this, REQUEST_CHECK_SETTINGS)
                            } catch (sie: IntentSender.SendIntentException) {
                                Timber.i("PendingIntent unable to execute issueRequest.")
                            }

                        }
                        LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                            val errorMessage = "Location settings are inadequate, and cannot be fixed here. Fix in Settings."
                            Timber.e(errorMessage)
                            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
//                            mRequestingLocationUpdates = false
                        }
                    }
                }
    }

    private fun stopLocationUpdates(callbackType: Requester.CallbackType) {
//        if (!mRequestingLocationUpdates) {
//            Timber.d("stopLocationUpdates: updates never requested, no-op.")
//            return
//        }

        // It is a good practice to remove location requests when the activity is in a paused or
        // stopped state. Doing so helps battery performance and is especially
        // recommended in applications that issueRequest frequent location updates.
        when (callbackType) {
            Requester.CallbackType.CALLBACK -> fusedLocationClient.removeLocationUpdates(locationCallback)
            Requester.CallbackType.BG_SERVICE -> fusedLocationClient.removeLocationUpdates(locationPendingIntent)
//            Requester.CallbackType.FG_SERVICE -> stopService(foregroundServiceIntent)
            Requester.CallbackType.FG_SERVICE -> {
                updatesService?.removeLocationUpdates()
                unbindService()
            }
            else -> return
        }
    }

    // PERMISSIONS

    // TODO: Evict
    private fun showSnackbar(mainTextStringId: Int, actionStringId: Int,
                             listener: View.OnClickListener) {
        Snackbar.make(
                findViewById(android.R.id.content),
                getString(mainTextStringId),
                Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(actionStringId), listener).show()
    }

    private fun checkPermissions(): Boolean {
        val permissionState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
        return permissionState == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions() {
        val shouldProvideRationale = ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_FINE_LOCATION)

        // Provide an additional rationale to the user. This would happen if the user denied the
        // issueRequest previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            Timber.i("Displaying permission rationale to provide additional context.")
            showSnackbar(R.string.permission_rationale,
                    android.R.string.ok, View.OnClickListener {
                // Request permission
                ActivityCompat.requestPermissions(this,
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        REQUEST_PERMISSIONS_REQUEST_CODE)
            })
        } else {
            Timber.i("Requesting permission")
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_PERMISSIONS_REQUEST_CODE)
        }
    }

    /**
     * Callback received when a permissions issueRequest has been completed.
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {
        Timber.i("onRequestPermissionResult")
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.isEmpty()) {
                // If user interaction was interrupted, the permission issueRequest is cancelled and you
                // receive empty arrays.
                Timber.i("User interaction was cancelled.")
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                if (mRequestingLocationUpdates) {
                Timber.i("Permission granted/*, updates requested, starting location updates*/")
//                    startLocationUpdates()
            }
        } else {
            // Permission denied.

            // Notify the user via a SnackBar that they have rejected a core permission for the
            // app, which makes the Activity useless. In a real app, core permissions would
            // typically be best requested during a welcome-screen flow.

            // Additionally, it is important to remember that a permission might have been
            // rejected without asking the user for permission (device policy or "Never ask
            // again" prompts). Therefore, a user interface affordance is typically implemented
            // when permissions are denied. Otherwise, your app could appear unresponsive to
            // touches or interactions which have required permissions.
            showSnackbar(R.string.permission_denied_explanation,
                    R.string.settings, View.OnClickListener {
                // Build intent that displays the App settings screen.
                val intent = Intent()
                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                val uri = Uri.fromParts("package",
                        BuildConfig.APPLICATION_ID, null)
                intent.data = uri
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
            })
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            // Check for the integer issueRequest code originally supplied to startResolutionForResult().
            when (resultCode) {
                Activity.RESULT_OK -> Timber.i("User agreed to make required location settings changes.")
                Activity.RESULT_CANCELED -> {
                    Timber.i("User chose not to make required location settings changes.")
//                    mRequestingLocationUpdates = false
                }
            }// Nothing to do. startLocationupdates() gets called in onResume again.
        }
    }

    override fun openHistory() {
        startActivity(Intent(this, HistoryActivity::class.java))
    }

    /**
     * Code used in requesting runtime permissions.
     */
    private val REQUEST_PERMISSIONS_REQUEST_CODE = 34

    /**
     * Constant used in the location settings dialog.
     */
    private val REQUEST_CHECK_SETTINGS = 0x1

}
