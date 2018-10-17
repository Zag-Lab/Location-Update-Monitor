package io.zaglab.android.locationupdatemonitor.data

import androidx.room.Entity
import io.zaglab.android.locationupdatemonitor.data.persistence.LocationPersistenceModelImpl
import io.zaglab.android.locationupdatemonitor.data.viewmodels.LocationViewModelImpl
import io.zaglab.android.locationupdatemonitor.interfaces.LocationModel
import kotlin.reflect.KClass
import io.zaglab.android.locationupdatemonitor.interfaces.LocationPersistenceModel as PersistenceControls
import io.zaglab.android.locationupdatemonitor.interfaces.LocationViewModel as ViewModelControls

sealed class Location : LocationModel {
    data class Model(override var callbackType: CallbackType = CallbackType.BACKGROUND_SERVICE,
                     override var longitude: Double,
                     override var latitude: Double,
                     override var batched: Boolean = true,
                     override var date: String = "",
                     override var time: String = "") : Location() {
        constructor(location: android.location.Location) : this(
                longitude = location.longitude,
                latitude = location.latitude)
    }

    @Entity(tableName = "locations")
    data class PersistenceModel(val persistenceModel: LocationPersistenceModelImpl) : Location(), PersistenceControls by persistenceModel
    class ViewModel(private val viewModel: LocationViewModelImpl) : Location(), ViewModelControls by viewModel
}

infix fun <T : Location> LocationModel.`as`(type: KClass<T>): T {
    return when (type) {
        Location.PersistenceModel::class -> Location.PersistenceModel(LocationPersistenceModelImpl(this)) as T
        Location.ViewModel::class -> Location.ViewModel(LocationViewModelImpl(this)) as T
        else -> throw Exception("")
    }
}