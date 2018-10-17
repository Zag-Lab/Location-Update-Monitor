package io.zaglab.android.locationupdatemonitor.main

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.google.android.gms.location.LocationRequest
import io.zaglab.android.locationupdatemonitor.BR
import timber.log.Timber

class RequestBuilderViewModel(private val requester: Requester,
                              private val navigator: Navigator) : BaseObservable() {

    interface Requester {
        enum class CallbackType { CALLBACK, FG_SERVICE, BG_SERVICE, JOB }
        enum class PriorityType(val code: Int) {
            HIGH_ACCURACY(100), BALANCED_POWER_ACCURACY(102), LOW_POWER(104), NO_POWER(105)
        }

        fun issueRequest(request: LocationRequest, callbackType: CallbackType)
        fun cancelRequest(callbackType: CallbackType)
    }

    interface Navigator {
        fun openHistory()
    }

    @Bindable
    var updateInterval: String = ""
        set(value) {
            field = value
            Timber.e("update freq: $field")
            notifyPropertyChanged(BR.updateInterval)
        }

    @Bindable
    var fastestUpdateInterval: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.fastestUpdateInterval)
        }

    @Bindable
    var priorityType: Requester.PriorityType = Requester.PriorityType.HIGH_ACCURACY
        set(value) {
            field = value
            Timber.e("priority: $value")
            notifyPropertyChanged(BR.priorityType)
        }

    @Bindable
    var batching: Boolean = false
        set(value) {
            field = value
            notifyPropertyChanged(BR.batching)
        }

    @Bindable
    var batchingFrequency: String = ""
        set(value) {
            field = value
            Timber.e("batching freq: $field")
            notifyPropertyChanged(BR.batchingFrequency)
        }

    @Bindable
    var log: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.log)
        }

    fun toggleUpdates(isChecked: Boolean, callbackType: Requester.CallbackType) {
        if (isChecked) {
            buildRequest(callbackType)
        } else {
            requester.cancelRequest(callbackType)
        }
    }

    fun buildRequest(callbackType: Requester.CallbackType) {
        val request = LocationRequest().apply {
            interval = updateInterval.toLong() * 1000
            fastestInterval = fastestUpdateInterval.toLong() * 1000
            priority = priorityType.code
            if (batching) {
                maxWaitTime = batchingFrequency.toLong() * 1000
            }
        }
        requester.issueRequest(request, callbackType)
    }

    fun navToHistory() {
        navigator.openHistory()
    }
}