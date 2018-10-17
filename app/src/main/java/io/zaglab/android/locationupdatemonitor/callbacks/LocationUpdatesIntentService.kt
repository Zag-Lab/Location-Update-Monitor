package io.zaglab.android.locationupdatemonitor.callbacks

import android.app.IntentService
import android.content.Intent
import com.google.android.gms.location.LocationResult
import io.zaglab.android.locationupdatemonitor.getLocationResultTitle
import io.zaglab.android.locationupdatemonitor.getLocationUpdatesResult
import io.zaglab.android.locationupdatemonitor.sendNotification
import io.zaglab.android.locationupdatemonitor.setLocationUpdatesResult
import timber.log.Timber


/**
 * Handles incoming location updates and displays a notification with the location data.
 *
 * For apps targeting API level 25 ("Nougat") or lower, location updates may be requested
 * using [android.app.PendingIntent.getService] or
 * [android.app.PendingIntent.getBroadcast]. For apps targeting
 * API level O, only `getBroadcast` should be used.
 *
 * Note: Apps running on "O" devices (regardless of targetSdkVersion) may receive updates
 * less frequently than the interval specified in the
 * [com.google.android.gms.location.LocationRequest] when the app is no longer in the
 * foreground.
 */
class LocationUpdatesIntentService : IntentService(TAG) {

    override fun onHandleIntent(intent: Intent?) {
        if (intent != null) {
            val action = intent.action
            if (ACTION_PROCESS_UPDATES == action) {
                val result = LocationResult.extractResult(intent)
                if (result != null) {
                    val locations = result.locations
                    setLocationUpdatesResult(this, locations)
                    sendNotification(this, getLocationResultTitle(this, locations))
                    Timber.i(getLocationUpdatesResult(this))
                }
            }
        }
    }

    companion object {

        private val ACTION_PROCESS_UPDATES = "com.google.android.gms.location.sample.locationupdatespendingintent.action" + ".PROCESS_UPDATES"
        private val TAG = LocationUpdatesIntentService::class.java.simpleName
    }
}