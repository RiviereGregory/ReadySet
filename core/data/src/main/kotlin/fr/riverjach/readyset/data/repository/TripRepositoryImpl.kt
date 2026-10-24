package fr.riverjach.readyset.data.repository

import fr.riverjach.readyset.data.local.dao.PackingItemDao
import fr.riverjach.readyset.data.local.dao.TripDao
import fr.riverjach.readyset.data.local.entity.PackingItemEntity
import fr.riverjach.readyset.data.local.entity.TripEntity
import fr.riverjach.readyset.data.template.DefaultTemplates
import fr.riverjach.readyset.domain.model.ItemCategory
import fr.riverjach.readyset.domain.model.ItemId
import fr.riverjach.readyset.domain.model.PackingItem
import fr.riverjach.readyset.domain.model.PackingTemplate
import fr.riverjach.readyset.domain.model.Trip
import fr.riverjach.readyset.domain.model.TripDetail
import fr.riverjach.readyset.domain.model.TripId
import fr.riverjach.readyset.domain.repository.TripRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

class TripRepositoryImpl(
    private val tripDao: TripDao,
    private val packingItemDao: PackingItemDao
) : TripRepository {

    override fun observeAllTrips(): Flow<List<Trip>> {
        return tripDao.observeActiveTrips().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun observeTripDetail(tripId: TripId): Flow<TripDetail?> {
        return combine(
            tripDao.observeTripById(tripId.value),
            packingItemDao.observeItemsByTripId(tripId.value)
        ) { tripEntity, itemEntities ->
            if (tripEntity == null) null
            else {
                TripDetail(
                    trip = tripEntity.toDomain(),
                    items = itemEntities.map { it.toDomain() }
                )
            }
        }
    }

    override suspend fun getAvailableTemplates(): List<PackingTemplate> {
        return DefaultTemplates.allTemplates
    }

    override suspend fun createTrip(trip: Trip, initialItems: List<PackingItem>): TripId {
        tripDao.insertTrip(trip.toEntity())
        if (initialItems.isNotEmpty()) {
            packingItemDao.insertItems(initialItems.map { it.toEntity() })
        }
        return trip.id
    }

    override suspend fun addPackingItem(item: PackingItem) {
        packingItemDao.insertItem(item.toEntity())
    }

    override suspend fun toggleItemPacked(itemId: ItemId, isPacked: Boolean) {
        packingItemDao.updatePackedStatus(itemId.value, isPacked)
    }

    override suspend fun deletePackingItem(itemId: ItemId) {
        packingItemDao.deleteItemById(itemId.value)
    }

    override suspend fun deleteTrip(tripId: TripId) {
        tripDao.deleteTripById(tripId.value)
    }

    private fun TripEntity.toDomain(): Trip = Trip(
        id = TripId(id),
        title = title,
        destination = destination,
        departureDateEpochDays = departureDateEpochDays,
        isArchived = isArchived
    )

    private fun Trip.toEntity(): TripEntity = TripEntity(
        id = id.value,
        title = title,
        destination = destination,
        departureDateEpochDays = departureDateEpochDays,
        isArchived = isArchived
    )

    private fun PackingItemEntity.toDomain(): PackingItem = PackingItem(
        id = ItemId(id),
        tripId = TripId(tripId),
        name = name,
        category = try {
            ItemCategory.valueOf(category)
        } catch (_: Exception) {
            ItemCategory.MISC
        },
        quantity = quantity,
        isPacked = isPacked
    )

    private fun PackingItem.toEntity(): PackingItemEntity = PackingItemEntity(
        id = id.value,
        tripId = tripId.value,
        name = name,
        category = category.name,
        quantity = quantity,
        isPacked = isPacked
    )
}
