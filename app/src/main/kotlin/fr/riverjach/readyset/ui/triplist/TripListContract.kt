package fr.riverjach.readyset.ui.triplist

import androidx.compose.runtime.Immutable
import fr.riverjach.readyset.domain.model.PackingTemplate
import fr.riverjach.readyset.domain.model.Trip
import fr.riverjach.readyset.domain.model.TripId
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Immutable
sealed interface TripListUiState {
    data object Loading : TripListUiState

    data class Success(
        val trips: ImmutableList<Trip> = persistentListOf(),
        val templates: ImmutableList<PackingTemplate> = persistentListOf(),
        val isCreateTripDialogOpen: Boolean = false,
        val isCreating: Boolean = false
    ) : TripListUiState

    data class Error(val message: String) : TripListUiState
}

sealed interface TripListUiAction {
    data object OpenCreateTripDialog : TripListUiAction
    data object DismissCreateTripDialog : TripListUiAction
    data class ConfirmCreateTrip(
        val title: String,
        val destination: String?,
        val selectedTemplateId: String?
    ) : TripListUiAction
    data class DeleteTrip(val tripId: TripId) : TripListUiAction
}
