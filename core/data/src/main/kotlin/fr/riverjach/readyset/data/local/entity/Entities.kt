package fr.riverjach.readyset.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "trips")
data class TripEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val destination: String?,
    val departureDateEpochDays: Long?,
    val isArchived: Boolean = false,
    val createdAtEpochMillis: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "packing_items",
    foreignKeys = [
        ForeignKey(
            entity = TripEntity::class,
            parentColumns = ["id"],
            childColumns = ["tripId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["tripId"])]
)
data class PackingItemEntity(
    @PrimaryKey
    val id: String,
    val tripId: String,
    val name: String,
    val category: String,
    val quantity: Int = 1,
    val isPacked: Boolean = false,
    val createdAtEpochMillis: Long = System.currentTimeMillis()
)
