package fr.riverjach.readyset.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import fr.riverjach.readyset.data.local.entity.PackingItemEntity
import fr.riverjach.readyset.data.local.entity.TripEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TripDao {
    @Query("SELECT * FROM trips WHERE isArchived = 0 ORDER BY createdAtEpochMillis DESC")
    fun observeActiveTrips(): Flow<List<TripEntity>>

    @Query("SELECT * FROM trips WHERE id = :tripId")
    fun observeTripById(tripId: String): Flow<TripEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrip(trip: TripEntity)

    @Query("DELETE FROM trips WHERE id = :tripId")
    suspend fun deleteTripById(tripId: String)
}

@Dao
interface PackingItemDao {
    @Query("SELECT * FROM packing_items WHERE tripId = :tripId ORDER BY name COLLATE NOCASE ASC")
    fun observeItemsByTripId(tripId: String): Flow<List<PackingItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItems(items: List<PackingItemEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: PackingItemEntity)

    @Query("UPDATE packing_items SET isPacked = :isPacked WHERE id = :itemId")
    suspend fun updatePackedStatus(itemId: String, isPacked: Boolean)

    @Query("DELETE FROM packing_items WHERE id = :itemId")
    suspend fun deleteItemById(itemId: String)
}
