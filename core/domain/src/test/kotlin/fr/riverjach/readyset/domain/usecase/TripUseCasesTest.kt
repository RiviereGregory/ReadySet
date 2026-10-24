package fr.riverjach.readyset.domain.usecase

import fr.riverjach.readyset.domain.model.ItemCategory
import fr.riverjach.readyset.domain.model.ItemId
import fr.riverjach.readyset.domain.model.PackingItem
import fr.riverjach.readyset.domain.model.PackingTemplate
import fr.riverjach.readyset.domain.model.TemplateItem
import fr.riverjach.readyset.domain.model.Trip
import fr.riverjach.readyset.domain.model.TripDetail
import fr.riverjach.readyset.domain.model.TripId
import fr.riverjach.readyset.domain.repository.TripRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class TripUseCasesTest {

    private lateinit var fakeRepository: FakeTripRepository
    private lateinit var observeTripDetailUseCase: ObserveTripDetailUseCase
    private lateinit var createTripWithTemplateUseCase: CreateTripWithTemplateUseCase
    private lateinit var toggleItemPackedUseCase: ToggleItemPackedUseCase

    @BeforeEach
    fun setUp() {
        fakeRepository = FakeTripRepository()
        observeTripDetailUseCase = ObserveTripDetailUseCase(fakeRepository)
        createTripWithTemplateUseCase = CreateTripWithTemplateUseCase(fakeRepository)
        toggleItemPackedUseCase = ToggleItemPackedUseCase(fakeRepository)
    }

    @Test
    fun `should create trip and populate items when template is provided`() = runBlocking {
        // Given
        val template = PackingTemplate(
            id = "weekend",
            title = "Weekend Escapade",
            description = "Essentiels pour 2 jours",
            items = listOf(
                TemplateItem("Brosse à dents", ItemCategory.TOILETRIES, 1),
                TemplateItem("Chargeur téléphone", ItemCategory.TECH, 1)
            )
        )

        // When
        val tripId = createTripWithTemplateUseCase(
            title = "Weekend à Rome",
            destination = "Italie",
            template = template
        )

        // Then
        val detail = observeTripDetailUseCase(tripId).first()
        assertThat(detail).isNotNull
        assertThat(detail!!.trip.title).isEqualTo("Weekend à Rome")
        assertThat(detail.items).hasSize(2)
        assertThat(detail.items.map { it.name }).containsExactlyInAnyOrder("Brosse à dents", "Chargeur téléphone")
        assertThat(detail.packedItemsCount).isEqualTo(0)
    }

    @Test
    fun `should sort items alphabetically and filter by search query`() = runBlocking {
        // Given
        val tripId = TripId("trip-1")
        val trip = Trip(id = tripId, title = "Randonnée")
        val items = listOf(
            PackingItem(ItemId("1"), tripId, "Tente", ItemCategory.MISC),
            PackingItem(ItemId("2"), tripId, "Anorak", ItemCategory.CLOTHES),
            PackingItem(ItemId("3"), tripId, "Gourde", ItemCategory.MISC),
            PackingItem(ItemId("4"), tripId, "Aliments", ItemCategory.MISC)
        )
        fakeRepository.createTrip(trip, items)

        // When: Search query is "an"
        val detailFiltered = observeTripDetailUseCase(tripId, searchQuery = "an").first()

        // Then: Should filter and sort alphabetically ("Aliments", "Anorak")
        assertThat(detailFiltered).isNotNull
        assertThat(detailFiltered!!.items.map { it.name })
            .containsExactly("Aliments", "Anorak")
    }

    @Test
    fun `should toggle item packed status`() = runBlocking {
        // Given
        val tripId = TripId("trip-1")
        val itemId = ItemId("item-1")
        val trip = Trip(id = tripId, title = "Voyage")
        val item = PackingItem(itemId, tripId, "Passeport", ItemCategory.DOCUMENTS, isPacked = false)
        fakeRepository.createTrip(trip, listOf(item))

        // When
        toggleItemPackedUseCase(itemId, currentPackedState = false)

        // Then
        val detail = observeTripDetailUseCase(tripId).first()
        assertThat(detail).isNotNull
        assertThat(detail!!.items.first().isPacked).isTrue()
        assertThat(detail.progressPercentage).isEqualTo(100)
    }
}

private class FakeTripRepository : TripRepository {
    private val tripsFlow = MutableStateFlow<List<Trip>>(emptyList())
    private val itemsFlow = MutableStateFlow<Map<TripId, List<PackingItem>>>(emptyMap())

    override fun observeAllTrips(): Flow<List<Trip>> = tripsFlow

    override fun observeTripDetail(tripId: TripId): Flow<TripDetail?> {
        return itemsFlow.map { map ->
            val trip = tripsFlow.value.find { it.id == tripId } ?: return@map null
            val items = map[tripId] ?: emptyList()
            TripDetail(trip, items)
        }
    }

    override suspend fun getAvailableTemplates(): List<PackingTemplate> = emptyList()

    override suspend fun createTrip(trip: Trip, initialItems: List<PackingItem>): TripId {
        tripsFlow.update { it + trip }
        itemsFlow.update { current ->
            current + (trip.id to initialItems)
        }
        return trip.id
    }

    override suspend fun addPackingItem(item: PackingItem) {
        itemsFlow.update { current ->
            val existing = current[item.tripId] ?: emptyList()
            current + (item.tripId to (existing + item))
        }
    }

    override suspend fun toggleItemPacked(itemId: ItemId, isPacked: Boolean) {
        itemsFlow.update { current ->
            current.mapValues { (_, items) ->
                items.map { if (it.id == itemId) it.copy(isPacked = isPacked) else it }
            }
        }
    }

    override suspend fun deletePackingItem(itemId: ItemId) {
        itemsFlow.update { current ->
            current.mapValues { (_, items) ->
                items.filterNot { it.id == itemId }
            }
        }
    }

    override suspend fun deleteTrip(tripId: TripId) {
        tripsFlow.update { list -> list.filterNot { it.id == tripId } }
        itemsFlow.update { map -> map - tripId }
    }
}
