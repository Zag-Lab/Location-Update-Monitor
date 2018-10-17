package io.zaglab.android.locationupdatemonitor.data.persistence

import androidx.room.Entity
import androidx.room.PrimaryKey
import io.zaglab.android.locationupdatemonitor.data.CallbackType
import io.zaglab.android.locationupdatemonitor.interfaces.LocationModel
import io.zaglab.android.locationupdatemonitor.interfaces.LocationPersistenceModel

//@Entity(tableName = "locations")
data class LocationPersistenceModelImpl(
        @PrimaryKey(autoGenerate = true)
        override val id: Int = 0,
        override var callbackType: CallbackType = CallbackType.FOREGROUND_SERVICE,
        override var longitude: Double = 0.0,
        override var latitude: Double = 0.0,
        override var batched: Boolean = true,
        override var date: String = "",
        override var time: String = "") : LocationPersistenceModel {

    constructor(model: LocationModel) : this(
            callbackType = model.callbackType,
            longitude = model.longitude,
            latitude = model.latitude,
            batched = model.batched,
            date = model.date,
            time = model.time)
}