package io.zaglab.android.locationupdatemonitor.history

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import io.zaglab.android.locationupdatemonitor.BR
import io.zaglab.android.locationupdatemonitor.data.CallbackType
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime


class LocationViewModel(callbackType: CallbackType,
                        longitude: Double,
                        latitude: Double,
                        batched: Boolean,
                        date: LocalDate,
                        time: LocalTime) : BaseObservable() {

    @Bindable
    var callbackType: CallbackType = CallbackType.BACKGROUND_SERVICE
        set(value) {
            field = value
            notifyPropertyChanged(BR.callbackType)
        }

    @Bindable
    var longitude: Double = 0.0
        set(value) {
            field = value
            notifyPropertyChanged(BR.longitude)
        }

    @Bindable
    var latitude: Double = 0.0
        set(value) {
            field = value
            notifyPropertyChanged(BR.latitude)
        }

    @Bindable
    var batched: Boolean = false
        set(value) {
            field = value
            notifyPropertyChanged(BR.batched)
        }

    @Bindable
    var date: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.date)
        }

    @Bindable
    var time: String = ""
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