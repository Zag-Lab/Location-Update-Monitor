package io.zaglab.android.locationupdatemonitor.callbacks

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.google.android.gms.location.LocationResult
import io.zaglab.android.locationupdatemonitor.data.Database
import io.zaglab.android.locationupdatemonitor.data.LocationDao
import io.zaglab.android.locationupdatemonitor.widgets.mapLocations
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import timber.log.Timber

/**
 * Receiver for handling location updates.
 *
 * For apps targeting API level O
 * [android.app.PendingIntent.getBroadcast] should be used when
 * requesting location updates. Due to limits on background services,
 * [android.app.PendingIntent.getService] should not be used.
 *
 * Note: Apps running on "O" devices (regardless of targetSdkVersion) may receive updates
 * less frequently than the interval specified in the
 * [com.google.android.gms.location.LocationRequest] when the app is no longer in the
 * foreground.
 */
class LocationUpdatesBroadcastReceiver : BroadcastReceiver() {

    private lateinit var locationDao: LocationDao

    override fun onReceive(context: Context, intent: Intent?) {
        Timber.i("Location update received")
        if (intent != null) {
            val action = intent.action
            if (ACTION_PROCESS_UPDATES == action) {
                val result = LocationResult.extractResult(intent)
                if (result != null) {
                    locationDao = Database.getInstance(context)?.locationDao() ?: throw Exception("DB instance is null")
                    val locations = result.locations
                    mapLocations(locations).also { persistLocations(it) }
//                    setLocationUpdatesResult(context, locations)
//                    sendNotification(context, getLocationResultTitle(context, locations))
//                    Timber.i(getLocationUpdatesResult(context))
                }
            }
        }
    }

    private fun persistLocations(locations: List<io.zaglab.android.locationupdatemonitor.data.Location>) {
        runBlocking {
            async(Dispatchers.Default) {
                Timber.i("attempting to store ${locations.size} locations")
                locationDao.insertAll(locations)
            }.await()
        }
    }

    companion object {
        internal val ACTION_PROCESS_UPDATES = "com.google.android.gms.location.sample.locationupdatespendingintent.action.PROCESS_UPDATES"
    }
}