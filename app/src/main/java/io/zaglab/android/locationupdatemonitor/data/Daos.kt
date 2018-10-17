package io.zaglab.android.locationupdatemonitor.data

import androidx.room.*

@Dao
interface LocationDao {

    @Insert
    fun insertAll(locations: List<Location>)

    @Update
    fun update(location: Location)

    @Delete
    fun delete(location: Location)

    @Query("SELECT * FROM locations")
    fun getLocations(): List<Location>
}

//@Dao
//interface RequestDao {
//
//    @Insert
//    fun insert(request: Request)
//
//    @Update
//    fun update(request: Request)
//
//    @Delete
//    fun delete(request: Request)
//
//    @Query("SELECT * FROM requests")
//    fun getRequests(): List<Request>
//}