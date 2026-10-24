package fr.riverjach.readyset.domain.repository

import fr.riverjach.readyset.domain.model.ItemId
import fr.riverjach.readyset.domain.model.PackingItem
import fr.riverjach.readyset.domain.model.PackingTemplate
import fr.riverjach.readyset.domain.model.Trip
import fr.riverjach.readyset.domain.model.TripDetail
import fr.riverjach.readyset.domain.model.TripId
import kotlinx.coroutines.flow.Flow

interface TripRepository {
    fun observeAllTrips(): Flow<List<Trip>>
    fun observeTripDetail(tripId: TripId): Flow<TripDetail?>
    suspend fun getAvailableTemplates(): List<PackingTemplate>
    suspend fun createTrip(trip: Trip, initialItems: List<PackingItem>): TripId
    suspend fun addPackingItem(item: PackingItem)
    suspend fun toggleItemPacked(itemId: ItemId, isPacked: Boolean)
    suspend fun deletePackingItem(itemId: ItemId)
    suspend fun deleteTrip(tripId: TripId)
}
