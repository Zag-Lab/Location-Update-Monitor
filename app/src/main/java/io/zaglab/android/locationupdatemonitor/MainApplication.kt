package io.zaglab.android.locationupdatemonitor

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen
import io.zaglab.android.locationupdatemonitor.data.Database
import io.zaglab.android.locationupdatemonitor.data.Store
import timber.log.Timber

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        AndroidThreeTen.init(this)
        initDatabase()
    }

    private fun initDatabase() {
        Database.getInstance(this)
    }
}