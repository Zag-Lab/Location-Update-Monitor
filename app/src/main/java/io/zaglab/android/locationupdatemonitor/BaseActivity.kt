package io.zaglab.android.locationupdatemonitor

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import androidx.appcompat.app.AppCompatActivity
import io.zaglab.android.locationupdatemonitor.callbacks.LocationUpdatesBoundService

abstract class BaseActivity: AppCompatActivity() {

    protected var bound = false
    protected var bindToService = false
    protected var updatesService: LocationUpdatesBoundService? = null

    private val serviceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as LocationUpdatesBoundService.LocalBinder
            updatesService = binder.service
            bound = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            updatesService = null
            bound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (bindToService) {
            bindService()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (bound) unbindService()
    }

    protected fun bindService() {
        bindService(Intent(this, LocationUpdatesBoundService::class.java), serviceConnection, Context.BIND_AUTO_CREATE)
    }

    protected fun unbindService() {
        unbindService(serviceConnection)
    }
}