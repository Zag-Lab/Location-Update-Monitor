package io.zaglab.android.locationupdatemonitor.data.viewmodels

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import io.zaglab.android.locationupdatemonitor.BR
import io.zaglab.android.locationupdatemonitor.data.CallbackType
import io.zaglab.android.locationupdatemonitor.interfaces.LocationModel
import io.zaglab.android.locationupdatemonitor.interfaces.LocationViewModel
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime

class LocationViewModelImpl(callbackType: CallbackType = CallbackType.CALLBACK,
                            longitude: Double = 0.0,
                            latitude: Double = 0.0,
                            batched: Boolean = false,
                            date: LocalDate = LocalDate.now(),
                            time: LocalTime = LocalTime.now()) : BaseObservable(), LocationViewModel {

    constructor(model: LocationModel) : this(
            callbackType = model.callbackType,
            longitude = model.longitude,
            latitude = model.latitude,
            batched = model.batched,
            date = LocalDate.parse(model.date),
            time = LocalTime.parse(model.time))

    @Bindable
    override var callbackType: CallbackType = CallbackType.BACKGROUND_SERVICE
        set(value) {
            field = value
            notifyPropertyChanged(BR.callbackType)
        }

    @Bindable
    override var longitude: Double = 0.0
        set(value) {
            field = value
            notifyPropertyChanged(BR.longitude)
        }

    @Bindable
    override var latitude: Double = 0.0
        set(value) {
            field = value
            notifyPropertyChanged(BR.latitude)
        }

    @Bindable
    override var batched: Boolean = false
        set(value) {
            field = value
            notifyPropertyChanged(BR.batched)
        }

    @Bindable
    override var date: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.date)
        }

    @Bindable
    override var time: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.time)
        }

    init {
        this.callbackType = callbackType
        this.longitude = longitude
        this.latitude = latitude
        this.batched = batched
        this.date = date.toString()
        this.time = time.toString()
    }
}