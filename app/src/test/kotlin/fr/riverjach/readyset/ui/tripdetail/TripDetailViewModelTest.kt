package fr.riverjach.readyset.ui.tripdetail

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import fr.riverjach.readyset.domain.model.ItemCategory
import fr.riverjach.readyset.domain.model.ItemId
import fr.riverjach.readyset.domain.model.PackingItem
import fr.riverjach.readyset.domain.model.PackingTemplate
import fr.riverjach.readyset.domain.model.Trip
import fr.riverjach.readyset.domain.model.TripDetail
import fr.riverjach.readyset.domain.model.TripId
import fr.riverjach.readyset.domain.repository.TripRepository
import fr.riverjach.readyset.domain.usecase.AddPackingItemUseCase
import fr.riverjach.readyset.domain.usecase.DeletePackingItemUseCase
import fr.riverjach.readyset.domain.usecase.ObserveTripDetailUseCase
import fr.riverjach.readyset.domain.usecase.ToggleItemPackedUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TripDetailViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakeRepository: FakeTripRepository

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeRepository = FakeTripRepository()
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `should filter and sort items when user types in search bar`() = runTest {
        // Given
        val tripId = TripId("trip-123")
        val trip = Trip(id = tripId, title = "Voyage à Londres")
        val items = listOf(
            PackingItem(ItemId("1"), tripId, "Valise", ItemCategory.MISC),
            PackingItem(ItemId("2"), tripId, "Adaptateur UK", ItemCategory.TECH),
            PackingItem(ItemId("3"), tripId, "Appareil photo", ItemCategory.TECH)
        )
        fakeRepository.setTripDetail(trip, items)

        val savedStateHandle = SavedStateHandle(mapOf("tripId" to tripId.value))
        val viewModel = TripDetailViewModel(
            savedStateHandle = savedStateHandle,
            observeTripDetailUseCase = ObserveTripDetailUseCase(fakeRepository),
            toggleItemPackedUseCase = ToggleItemPackedUseCase(fakeRepository),
            addPackingItemUseCase = AddPackingItemUseCase(fakeRepository),
            deletePackingItemUseCase = DeletePackingItemUseCase(fakeRepository)
        )

        // When & Then
        viewModel.uiState.test {
            // Initial loading
            assertThat(awaitItem()).isEqualTo(TripDetailUiState.Loading)

            // Emit success with all items sorted alphabetically
            val successState = awaitItem() as TripDetailUiState.Success
            assertThat(successState.items.map { it.name })
                .containsExactly("Adaptateur UK", "Appareil photo", "Valise")

            // User types "app"
            viewModel.onAction(TripDetailUiAction.UpdateSearchQuery("app"))

            val filteredState = awaitItem() as TripDetailUiState.Success
            assertThat(filteredState.items.map { it.name })
                .containsExactly("Appareil photo")

            cancelAndIgnoreRemainingEvents()
        }
    }
}

private class FakeTripRepository : TripRepository {
    private val detailFlow = MutableStateFlow<TripDetail?>(null)

    fun setTripDetail(trip: Trip, items: List<PackingItem>) {
        detailFlow.value = TripDetail(trip, items)
    }

    override fun observeAllTrips(): Flow<List<Trip>> = throw UnsupportedOperationException()

    override fun observeTripDetail(tripId: TripId): Flow<TripDetail?> = detailFlow

    override suspend fun getAvailableTemplates(): List<PackingTemplate> = emptyList()

    override suspend fun createTrip(trip: Trip, initialItems: List<PackingItem>): TripId = trip.id

    override suspend fun addPackingItem(item: PackingItem) {}

    override suspend fun toggleItemPacked(itemId: ItemId, isPacked: Boolean) {}

    override suspend fun deletePackingItem(itemId: ItemId) {}

    override suspend fun deleteTrip(tripId: TripId) {}
}
