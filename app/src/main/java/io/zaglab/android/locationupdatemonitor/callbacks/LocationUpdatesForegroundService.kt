package io.zaglab.android.locationupdatemonitor.callbacks

import android.app.*
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.*
import io.zaglab.android.locationupdatemonitor.main.MainActivity
import io.zaglab.android.locationupdatemonitor.R
import timber.log.Timber


/**
 * A started service that is promoted to a foreground service.
 *
 * For apps running in the background on "O" devices, location is computed only once every 10
 * minutes and delivered batched every 30 minutes. This restriction applies even to apps
 * targeting "N" or lower which are run on "O" devices.
 */
class LocationUpdatesForegroundService : Service() {

    /**
     * The name of the channel for notifications.
     */
    private val CHANNEL_ID = "channel_01"

    /**
     * The identifier for the notification displayed for the foreground boundService.
     */
    private val NOTIFICATION_ID = 12345678


    private val notificationManager: NotificationManager by lazy {
        getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    /**
     * Provides access to the Fused Location Provider API.
     */
    private val fusedLocationClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(this)
    }

    /**
     * Callback for changes in location.
     */
    private val locationCallback: LocationCallback by lazy {
        object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                super.onLocationResult(locationResult)
                onNewLocation(locationResult?.lastLocation)
            }
        }
    }

    /**
     * Returns the [NotificationCompat] used as part of the foreground boundService.
     */
    private val notification: Notification
        get() {
            val activityPendingIntent = PendingIntent.getActivity(this, 0,
                    Intent(this, MainActivity::class.java), 0)

            val builder = NotificationCompat.Builder(this)
                    .addAction(R.drawable.ic_launch, getString(R.string.launch_activity),
                            activityPendingIntent)
                    .setContentText("Location Update Monitor service is running")
                    .setOngoing(true)
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setTicker("Location Update Monitor service is running")
                    .setWhen(System.currentTimeMillis())
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                builder.setChannelId(CHANNEL_ID)
            }

            return builder.build()
        }

    override fun onCreate() {
        Timber.i("Service created")
        startForeground(NOTIFICATION_ID, notification)

        // Android O requires a Notification Channel.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.app_name)
            // Create the channel for the notification
            val channel = NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_DEFAULT)

            // Set the Notification Channel for the Notification Manager.
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Timber.i("Service started")

        val locationRequest = intent.getParcelableExtra<LocationRequest>("location_request")
        if (locationRequest != null) {
            try {
                fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper())
            } catch (unlikely: SecurityException) {
                Timber.e("Lost location permission. Could not request updates. $unlikely")
            }
        } else {
            Timber.d("Can't start/restart service. Location request is null")
        }

        //If system killed service for resources, recreate it and feed it same intent (location request)
        return Service.START_REDELIVER_INTENT
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onDestroy() {
        Timber.e("Destroying service")
        stopForeground(true)
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    private fun onNewLocation(location: Location?) {
        Timber.i("New location: $location")
        //TODO: store location somewhere
    }
}

//Intent extra used here and in MainActivity
val EXTRA_LOCATION_REQUEST = "location_request"
