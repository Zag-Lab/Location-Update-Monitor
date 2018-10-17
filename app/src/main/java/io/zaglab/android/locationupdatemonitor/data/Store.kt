package io.zaglab.android.locationupdatemonitor.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import io.zaglab.android.locationupdatemonitor.data.persistence.LocationPersistenceModelImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import timber.log.Timber

@Database(entities = [Location.PersistenceModel::class/*, Request::class*/], version = 1)
@TypeConverters(Converters::class)
abstract class Store : RoomDatabase() {
    abstract fun locationDao(): LocationDao
//    abstract fun requestDao(): RequestDao
}

object Database {
    private var INSTANCE: Store? = null

    fun getInstance(context: Context): Store? {
        if (INSTANCE == null) {
            synchronized(Store::class) {
                INSTANCE = Room.databaseBuilder(context.applicationContext,
                        Store::class.java, "location.db")
                        .build()
            }
        }
        return INSTANCE
    }

//    fun storeLocations(locations: List<Location>, request: Request) {
//        val locationDao = INSTANCE?.locationDao() ?: throw Exception("DB not initialized")
//        runBlocking {
//            async(Dispatchers.Default) {
//                Timber.i("attempting to store ${locations.size} locations")
//                locationDao.insertAll(locations)
//            }.await()
//        }
//    }
}
