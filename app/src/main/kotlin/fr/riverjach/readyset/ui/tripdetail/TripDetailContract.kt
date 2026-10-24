package fr.riverjach.readyset.ui.tripdetail

import androidx.compose.runtime.Immutable
import fr.riverjach.readyset.domain.model.ItemCategory
import fr.riverjach.readyset.domain.model.ItemId
import fr.riverjach.readyset.domain.model.PackingItem
import fr.riverjach.readyset.domain.model.Trip
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Immutable
sealed interface TripDetailUiState {
    data object Loading : TripDetailUiState

    data class Success(
        val trip: Trip,
        val items: ImmutableList<PackingItem> = persistentListOf(),
        val searchQuery: String = "",
        val totalCount: Int = 0,
        val packedCount: Int = 0,
        val progressPercentage: Int = 0,
        val isAddItemDialogOpen: Boolean = false
    ) : TripDetailUiState

    data class NotFound(val message: String = "Voyage introuvable") : TripDetailUiState
    data class Error(val message: String) : TripDetailUiState
}

sealed interface TripDetailUiAction {
    data class UpdateSearchQuery(val query: String) : TripDetailUiAction
    data class ToggleItemPacked(val itemId: ItemId, val currentStatus: Boolean) : TripDetailUiAction
    data class DeleteItem(val itemId: ItemId) : TripDetailUiAction
    data object OpenAddItemDialog : TripDetailUiAction
    data object DismissAddItemDialog : TripDetailUiAction
    data class ConfirmAddItem(
        val name: String,
        val category: ItemCategory,
        val quantity: Int
    ) : TripDetailUiAction
}
