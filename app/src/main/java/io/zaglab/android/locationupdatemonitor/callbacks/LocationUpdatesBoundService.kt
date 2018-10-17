package io.zaglab.android.locationupdatemonitor.callbacks

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.location.Location
import android.os.*
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.location.*
import io.zaglab.android.locationupdatemonitor.*
import io.zaglab.android.locationupdatemonitor.R
import io.zaglab.android.locationupdatemonitor.main.MainActivity
import timber.log.Timber


/**
 * A bound and started boundService that is promoted to a foreground boundService when location updates have
 * been requested and all clients unbind.
 *
 * For apps running in the background on "O" devices, location is computed only once every 10
 * minutes and delivered batched every 30 minutes. This restriction applies even to apps
 * targeting "N" or lower which are run on "O" devices.
 *
 * This sample show how to use a long-running boundService for location updates. When an activity is
 * bound to this boundService, frequent location updates are permitted. When the activity is removed
 * from the foreground, the boundService promotes itself to a foreground boundService, and location updates
 * continue. When the activity comes back to the foreground, the foreground boundService stops, and the
 * notification associated with that boundService is removed.
 */
class LocationUpdatesBoundService : Service() {

    private val mBinder = LocalBinder()

    /**
     * Used to check whether the bound activity has really gone away and not unbound as part of an
     * orientation change. We create a foreground boundService notification only if the former takes
     * place.
     */
    private var changingConfiguration = false

    private val notificationManager: NotificationManager by lazy {
        getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    /**
     * Contains parameters used by [com.google.android.gms.location.FusedLocationProviderApi].
     */
    private var mLocationRequest: LocationRequest? = null

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

    private var serviceHandler: Handler? = null

    /**
     * The current location.
     */
    private var location: Location? = null

    /**
     * Returns the [NotificationCompat] used as part of the foreground boundService.
     */
    private// Extra to help us figure out if we arrived in onStartCommand via the notification or not.
    // The PendingIntent that leads to a call to onStartCommand() in this boundService.
    // The PendingIntent to launch activity.
    // Set the Channel ID for Android O.
    // Channel ID
    val notification: Notification
        get() {
            val intent = Intent(this, LocationUpdatesBoundService::class.java)

            val text = getLocationText(location)
            intent.putExtra(EXTRA_STARTED_FROM_NOTIFICATION, true)
            val servicePendingIntent = PendingIntent.getService(this, 0, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT)
            val activityPendingIntent = PendingIntent.getActivity(this, 0,
                    Intent(this, MainActivity::class.java), 0)

            val builder = NotificationCompat.Builder(this)
                    .addAction(R.drawable.ic_launch, getString(R.string.launch_activity),
                            activityPendingIntent)
                    .addAction(R.drawable.ic_cancel, getString(R.string.remove_location_updates),
                            servicePendingIntent)
                    .setContentText(text)
                    .setContentTitle(getLocationTitle(this))
                    .setOngoing(true)
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setTicker(text)
                    .setWhen(System.currentTimeMillis())
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                builder.setChannelId(CHANNEL_ID)
            }

            return builder.build()
        }

    override fun onCreate() {
        val handlerThread = HandlerThread(TAG)
        handlerThread.start()
        serviceHandler = Handler(handlerThread.looper)

        // Android O requires a Notification Channel.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.app_name)
            // Create the channel for the notification
            val mChannel = NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_DEFAULT)

            // Set the Notification Channel for the Notification Manager.
            notificationManager.createNotificationChannel(mChannel)
        }
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Timber.i("Service started")
        val startedFromNotification = intent.getBooleanExtra(EXTRA_STARTED_FROM_NOTIFICATION,
                false)

        // We got here because the user decided to remove location updates from the notification.
        if (startedFromNotification) {
            removeLocationUpdates()
            stopSelf()
        }
        // Tells the system to not try to recreate the boundService after it has been killed.
        return Service.START_NOT_STICKY
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        changingConfiguration = true
    }

    override fun onBind(intent: Intent): IBinder? {
        // Called when a client (MainActivity in case of this sample) comes to the foreground
        // and binds with this boundService. The boundService should cease to be a foreground boundService
        // when that happens.
        Timber.i("in onBind()")
        stopForeground(true)
        changingConfiguration = false
        return mBinder
    }

    override fun onRebind(intent: Intent) {
        // Called when a client (MainActivity in case of this sample) returns to the foreground
        // and binds once again with this boundService. The boundService should cease to be a foreground
        // boundService when that happens.
        Timber.i("in onRebind()")
        stopForeground(true)
        changingConfiguration = false
        super.onRebind(intent)
    }

    override fun onUnbind(intent: Intent): Boolean {
        Timber.i("Last client unbound from boundService")

        // Called when the last client (MainActivity in case of this sample) unbinds from this
        // boundService. If this method is called due to a configuration change in MainActivity, we
        // do nothing. Otherwise, we make this boundService a foreground boundService.
        if (!changingConfiguration && requestingLocationUpdates(this)) {
            Timber.i("Starting foreground boundService")
            /*
            // TODO(developer). If targeting O, use the following code.
            if (Build.VERSION.SDK_INT == Build.VERSION_CODES.O) {
                mNotificationManager.startServiceInForeground(new Intent(this,
                        LocationUpdatesBoundService.class), NOTIFICATION_ID, getNotification());
            } else {
                startForeground(NOTIFICATION_ID, getNotification());
            }
             */
            startForeground(NOTIFICATION_ID, notification)
        }
        return true // Ensures onRebind() is called when a client re-binds.
    }

    override fun onDestroy() {
        serviceHandler?.removeCallbacksAndMessages(null)
    }

    /**
     * Makes a request for location updates. Note that in this sample we merely log the
     * [SecurityException].
     */
    fun requestLocationUpdates(request: LocationRequest) {
        Timber.i("Requesting location updates")
        setRequestingLocationUpdates(this, true)
        startService(Intent(applicationContext, LocationUpdatesBoundService::class.java))
        try {
            fusedLocationClient.requestLocationUpdates(request,
                    locationCallback, Looper.myLooper())
        } catch (unlikely: SecurityException) {
            setRequestingLocationUpdates(this, false)
            Timber.e("Lost location permission. Could not request updates. $unlikely")
        }

    }

    /**
     * Removes location updates. Note that in this sample we merely log the
     * [SecurityException].
     */
    fun removeLocationUpdates() {
        Timber.i("Removing location updates")
        try {
            fusedLocationClient.removeLocationUpdates(locationCallback)
            setRequestingLocationUpdates(this, false)
            stopSelf()
        } catch (unlikely: SecurityException) {
            setRequestingLocationUpdates(this, true)
            Timber.e("Lost location permission. Could not remove updates. $unlikely")
        }

    }

    private fun onNewLocation(location: Location?) {
        Timber.i("New location: $location")

        this.location = location

        // Notify anyone listening for broadcasts about the new location.
        val intent = Intent(ACTION_BROADCAST)
        intent.putExtra(EXTRA_LOCATION, location)
        LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)

        // Update notification content if running as a foreground boundService.
        if (serviceIsRunningInForeground(this)) {
            notificationManager.notify(NOTIFICATION_ID, notification)
        }
    }

    /**
     * Class used for the client Binder.  Since this boundService runs in the same process as its
     * clients, we don't need to deal with IPC.
     */
    inner class LocalBinder : Binder() {
        val service: LocationUpdatesBoundService
            get() = this@LocationUpdatesBoundService
    }

    /**
     * Returns true if this is a foreground boundService.
     *
     * @param context The [Context].
     */
    private fun serviceIsRunningInForeground(context: Context): Boolean {
        val manager = context.getSystemService(
                Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(
                Integer.MAX_VALUE)) {
            if (javaClass.name == service.service.className) {
                if (service.foreground) {
                    return true
                }
            }
        }
        return false
    }

    companion object {

        private val PACKAGE_NAME = "com.google.android.gms.location.sample.locationupdatesforegroundservice"

        private val TAG = LocationUpdatesBoundService::class.java.simpleName

        /**
         * The name of the channel for notifications.
         */
        private val CHANNEL_ID = "channel_01"

        internal val ACTION_BROADCAST = "$PACKAGE_NAME.broadcast"

        internal val EXTRA_LOCATION = "$PACKAGE_NAME.location"
        private val EXTRA_STARTED_FROM_NOTIFICATION = "$PACKAGE_NAME.started_from_notification"

        /**
         * The desired interval for location updates. Inexact. Updates may be more or less frequent.
         */
        private val UPDATE_INTERVAL_IN_MILLISECONDS: Long = 10000

        /**
         * The fastest rate for active location updates. Updates will never be more frequent
         * than this value.
         */
        private val FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2

        /**
         * The identifier for the notification displayed for the foreground boundService.
         */
        private val NOTIFICATION_ID = 12345678
    }
}