package io.zaglab.android.locationupdatemonitor.history

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import io.zaglab.android.locationupdatemonitor.BaseActivity
import io.zaglab.android.locationupdatemonitor.R
import io.zaglab.android.locationupdatemonitor.data.Database
import io.zaglab.android.locationupdatemonitor.data.Location
import io.zaglab.android.locationupdatemonitor.data.LocationDao
import io.zaglab.android.locationupdatemonitor.databinding.ActivityHistoryBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking

class HistoryActivity : BaseActivity() {

    private lateinit var binding: ActivityHistoryBinding
    private lateinit var locationDao: LocationDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_history)
        binding.vm = HistoryViewModel()
        binding.locations.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        locationDao = Database.getInstance(this)?.locationDao() ?: throw Exception("No db")
    }

    override fun onResume() {
        super.onResume()

        runBlocking {
            val job = async(Dispatchers.Default) {
                getLocations()
            }
            binding.vm?.locations = job.await()
        }
    }

    private fun getLocations(): List<LocationViewModel> {
        return mapLocationEntries(locationDao.getLocations())
    }
}

private fun mapLocationEntries(locations: List<Location>): List<LocationViewModel> {
    return locations.map {
        LocationViewModel(callbackType = it.type,
                longitude = it.longitude,
                latitude = it.latitude,
                batched = it.batched,
                date = it.date,
                time = it.time)
    }
}