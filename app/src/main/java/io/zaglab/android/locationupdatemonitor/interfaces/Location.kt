package io.zaglab.android.locationupdatemonitor.interfaces

import io.zaglab.android.locationupdatemonitor.data.CallbackType

interface LocationModel {
    var callbackType: CallbackType
    var longitude: Double
    var latitude: Double
    var batched: Boolean
    var date: String
    var time: String
}

interface LocationPersistenceModel : LocationModel {
    val id: Int
}
interface LocationViewModel: LocationModel, ViewModel {
    //TODO anything relevant to VM
}