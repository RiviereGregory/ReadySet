package fr.riverjach.readyset.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import fr.riverjach.readyset.data.local.dao.PackingItemDao
import fr.riverjach.readyset.data.local.dao.TripDao
import fr.riverjach.readyset.data.local.entity.PackingItemEntity
import fr.riverjach.readyset.data.local.entity.TripEntity

@Database(
    entities = [
        TripEntity::class,
        PackingItemEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class ReadySetDatabase : RoomDatabase() {
    abstract fun tripDao(): TripDao
    abstract fun packingItemDao(): PackingItemDao
}
