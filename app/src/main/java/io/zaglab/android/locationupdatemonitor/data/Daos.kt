package io.zaglab.android.locationupdatemonitor.data

import androidx.room.*

@Dao
interface LocationDao {

    @Insert
    fun insertAll(locations: List<Location.PersistenceModel>)

    @Update
    fun update(location: Location.PersistenceModel)

    @Delete
    fun delete(location: Location.PersistenceModel)

    @Query("SELECT * FROM locations")
    fun getLocations(): List<Location.PersistenceModel>

//    @Query("SELECT * FROM locations WHERE type LIKE :type")
//    fun getLocationsByType(type: CallbackType): List<Location>

//    @Insert
//    fun insertAll(locations: List<Location>)

//    @Delete
//    fun deleteAll()

//    @Query("SELECT * FROM locations")
//    fun getLocations(): List<Location>

//    @Query("SELECT * FROM locations WHERE type LIKE :requestId")
//    fun getLocationsForRequestId(requestId: Int): List<Location>
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