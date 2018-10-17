package io.zaglab.android.locationupdatemonitor.widgets

import android.location.Location
import io.zaglab.android.locationupdatemonitor.data.CallbackType
import org.threeten.bp.Instant
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId

fun mapLocations(locations: List<Location>): List<io.zaglab.android.locationupdatemonitor.data.Location> {
    return locations.map {
        val dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(it.time), ZoneId.systemDefault())
        io.zaglab.android.locationupdatemonitor.data.Location(
        type = CallbackType.BACKGROUND_SERVICE,
        longitude = it.longitude,
        latitude = it.latitude,
        batched = true,
        date = dateTime.toLocalDate(),
        time = dateTime.toLocalTime())
    }
}

//fun mapRequest(request: LocationRequest): Request {
//    with(request.) {
//        Request(
//                type =
//        )
//    }
//        val dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(it.time), ZoneId.systemDefault())
//        io.zaglab.android.locationupdatemonitor.data.Location(
//                type = CallbackType.BACKGROUND_SERVICE,
//                longitude = it.longitude,
//                latitude = it.latitude,
//                batched = true,
//                date = dateTime.toLocalDate(),
//                time = dateTime.toLocalTime())
//}
//}