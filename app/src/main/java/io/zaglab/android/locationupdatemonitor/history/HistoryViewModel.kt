package io.zaglab.android.locationupdatemonitor.history

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import io.zaglab.android.locationupdatemonitor.BR

class HistoryViewModel : BaseObservable() {

    @Bindable
    var locations: List<LocationViewModel> = emptyList()
        set(value) {
            field = value
            notifyPropertyChanged(BR.locations)
        }
}