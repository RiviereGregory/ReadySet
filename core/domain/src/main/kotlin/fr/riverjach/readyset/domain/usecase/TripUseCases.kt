package fr.riverjach.readyset.domain.usecase

import fr.riverjach.readyset.domain.model.ItemCategory
import fr.riverjach.readyset.domain.model.ItemId
import fr.riverjach.readyset.domain.model.PackingItem
import fr.riverjach.readyset.domain.model.PackingTemplate
import fr.riverjach.readyset.domain.model.Trip
import fr.riverjach.readyset.domain.model.TripDetail
import fr.riverjach.readyset.domain.model.TripId
import fr.riverjach.readyset.domain.repository.TripRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID

class ObserveTripsUseCase(private val tripRepository: TripRepository) {
    operator fun invoke(): Flow<List<Trip>> = tripRepository.observeAllTrips()
}

class ObserveTripDetailUseCase(private val tripRepository: TripRepository) {
    operator fun invoke(tripId: TripId, searchQuery: String = ""): Flow<TripDetail?> {
        return tripRepository.observeTripDetail(tripId).map { detail ->
            if (detail == null) null
            else {
                val filteredAndSortedItems = detail.items
                    .filter { item ->
                        if (searchQuery.isBlank()) true
                        else item.name.contains(searchQuery.trim(), ignoreCase = true)
                    }
                    .sortedBy { it.name.lowercase() }

                detail.copy(items = filteredAndSortedItems)
            }
        }
    }
}

class GetTemplatesUseCase(private val tripRepository: TripRepository) {
    suspend operator fun invoke(): List<PackingTemplate> = tripRepository.getAvailableTemplates()
}

class CreateTripWithTemplateUseCase(private val tripRepository: TripRepository) {
    suspend operator fun invoke(
        title: String,
        destination: String? = null,
        departureDateEpochDays: Long? = null,
        template: PackingTemplate? = null
    ): TripId {
        val tripId = TripId(UUID.randomUUID().toString())
        val trip = Trip(
            id = tripId,
            title = title.trim(),
            destination = destination?.trim()?.ifBlank { null },
            departureDateEpochDays = departureDateEpochDays
        )

        val items = template?.items?.map { templateItem ->
            PackingItem(
                id = ItemId(UUID.randomUUID().toString()),
                tripId = tripId,
                name = templateItem.name,
                category = templateItem.category,
                quantity = templateItem.defaultQuantity,
                isPacked = false
            )
        } ?: emptyList()

        return tripRepository.createTrip(trip, items)
    }
}

class ToggleItemPackedUseCase(private val tripRepository: TripRepository) {
    suspend operator fun invoke(itemId: ItemId, currentPackedState: Boolean) {
        tripRepository.toggleItemPacked(itemId, !currentPackedState)
    }
}

class AddPackingItemUseCase(private val tripRepository: TripRepository) {
    suspend operator fun invoke(
        tripId: TripId,
        name: String,
        category: ItemCategory = ItemCategory.MISC,
        quantity: Int = 1
    ): ItemId {
        val itemId = ItemId(UUID.randomUUID().toString())
        val item = PackingItem(
            id = itemId,
            tripId = tripId,
            name = name.trim(),
            category = category,
            quantity = quantity,
            isPacked = false
        )
        tripRepository.addPackingItem(item)
        return itemId
    }
}

class DeleteTripUseCase(private val tripRepository: TripRepository) {
    suspend operator fun invoke(tripId: TripId) {
        tripRepository.deleteTrip(tripId)
    }
}

class DeletePackingItemUseCase(private val tripRepository: TripRepository) {
    suspend operator fun invoke(itemId: ItemId) {
        tripRepository.deletePackingItem(itemId)
    }
}
