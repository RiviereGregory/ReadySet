package fr.riverjach.readyset.ui.tripdetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import fr.riverjach.readyset.domain.model.TripId
import fr.riverjach.readyset.domain.usecase.AddPackingItemUseCase
import fr.riverjach.readyset.domain.usecase.DeletePackingItemUseCase
import fr.riverjach.readyset.domain.usecase.ObserveTripDetailUseCase
import fr.riverjach.readyset.domain.usecase.ToggleItemPackedUseCase
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class TripDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val observeTripDetailUseCase: ObserveTripDetailUseCase,
    private val toggleItemPackedUseCase: ToggleItemPackedUseCase,
    private val addPackingItemUseCase: AddPackingItemUseCase,
    private val deletePackingItemUseCase: DeletePackingItemUseCase
) : ViewModel() {

    private val tripIdValue: String = checkNotNull(savedStateHandle["tripId"]) {
        "tripId parameter is mandatory"
    }
    val tripId = TripId(tripIdValue)

    private val searchQuery = MutableStateFlow("")
    private val isAddDialogOpen = MutableStateFlow(false)

    val uiState: StateFlow<TripDetailUiState> = searchQuery.flatMapLatest { query ->
        combine(
            observeTripDetailUseCase(tripId, searchQuery = query),
            isAddDialogOpen
        ) { detail, isDialogOpen ->
            if (detail == null) {
                TripDetailUiState.NotFound()
            } else {
                TripDetailUiState.Success(
                    trip = detail.trip,
                    items = detail.items.toImmutableList(),
                    searchQuery = query,
                    totalCount = detail.totalItemsCount,
                    packedCount = detail.packedItemsCount,
                    progressPercentage = detail.progressPercentage,
                    isAddItemDialogOpen = isDialogOpen
                )
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = TripDetailUiState.Loading
    )

    fun onAction(action: TripDetailUiAction) {
        when (action) {
            is TripDetailUiAction.UpdateSearchQuery -> searchQuery.value = action.query
            is TripDetailUiAction.ToggleItemPacked -> togglePacked(action)
            is TripDetailUiAction.DeleteItem -> deleteItem(action)
            TripDetailUiAction.OpenAddItemDialog -> isAddDialogOpen.update { true }
            TripDetailUiAction.DismissAddItemDialog -> isAddDialogOpen.update { false }
            is TripDetailUiAction.ConfirmAddItem -> addItem(action)
        }
    }

    private fun togglePacked(action: TripDetailUiAction.ToggleItemPacked) {
        viewModelScope.launch {
            toggleItemPackedUseCase(action.itemId, action.currentStatus)
        }
    }

    private fun deleteItem(action: TripDetailUiAction.DeleteItem) {
        viewModelScope.launch {
            deletePackingItemUseCase(action.itemId)
        }
    }

    private fun addItem(action: TripDetailUiAction.ConfirmAddItem) {
        viewModelScope.launch {
            addPackingItemUseCase(
                tripId = tripId,
                name = action.name,
                category = action.category,
                quantity = action.quantity
            )
            isAddDialogOpen.update { false }
        }
    }
}
