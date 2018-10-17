package io.zaglab.android.locationupdatemonitor.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.LocalTime


//@Entity(tableName = "locations")
//todo rename back to Location
//data class PersistenceModel(
//        @PrimaryKey(autoGenerate = true)
//        val id: Int = 0,
//        val type: CallbackType,
//        val longitude: Double,
//        val latitude: Double,
//        val batched: Boolean,
//        val date: LocalDate,
//        val time: LocalTime
//)

enum class CallbackType { CALLBACK, FOREGROUND_SERVICE, BACKGROUND_SERVICE }

//@Entity(tableName = "requests")
//data class Request(
//        @PrimaryKey(autoGenerate = true)
//        val id: Int = 0,
//        val type: CallbackType,
//        val updateInterval: Int,
//        val fastestInterval: Int,
//        val batched: Boolean,
//        val startDateTime: LocalDateTime,
//        val endDateTime: LocalDateTime,
//        val totalLocations: Int
////        val locations: List<Location>
//)