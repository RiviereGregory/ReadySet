package fr.riverjach.readyset.ui.triplist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import fr.riverjach.readyset.domain.model.PackingTemplate
import fr.riverjach.readyset.domain.model.TripId
import fr.riverjach.readyset.domain.usecase.CreateTripWithTemplateUseCase
import fr.riverjach.readyset.domain.usecase.DeleteTripUseCase
import fr.riverjach.readyset.domain.usecase.GetTemplatesUseCase
import fr.riverjach.readyset.domain.usecase.ObserveTripsUseCase
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TripListViewModel @Inject constructor(
    private val observeTripsUseCase: ObserveTripsUseCase,
    private val getTemplatesUseCase: GetTemplatesUseCase,
    private val createTripWithTemplateUseCase: CreateTripWithTemplateUseCase,
    private val deleteTripUseCase: DeleteTripUseCase
) : ViewModel() {

    private val isCreateDialogOpen = MutableStateFlow(false)
    private val isCreating = MutableStateFlow(false)
    private val templatesState = MutableStateFlow<List<PackingTemplate>>(emptyList())

    init {
        viewModelScope.launch {
            templatesState.value = getTemplatesUseCase()
        }
    }

    val uiState: StateFlow<TripListUiState> = combine(
        observeTripsUseCase(),
        templatesState,
        isCreateDialogOpen,
        isCreating
    ) { trips, templates, isDialogOpen, creating ->
        TripListUiState.Success(
            trips = trips.toImmutableList(),
            templates = templates.toImmutableList(),
            isCreateTripDialogOpen = isDialogOpen,
            isCreating = creating
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = TripListUiState.Loading
    )

    fun onAction(action: TripListUiAction) {
        when (action) {
            TripListUiAction.OpenCreateTripDialog -> isCreateDialogOpen.update { true }
            TripListUiAction.DismissCreateTripDialog -> isCreateDialogOpen.update { false }
            is TripListUiAction.ConfirmCreateTrip -> createTrip(action)
            is TripListUiAction.DeleteTrip -> deleteTrip(action.tripId)
        }
    }

    private fun createTrip(action: TripListUiAction.ConfirmCreateTrip) {
        viewModelScope.launch {
            isCreating.update { true }
            try {
                val template = templatesState.value.find { it.id == action.selectedTemplateId }
                createTripWithTemplateUseCase(
                    title = action.title,
                    destination = action.destination,
                    template = template
                )
                isCreateDialogOpen.update { false }
            } finally {
                isCreating.update { false }
            }
        }
    }

    private fun deleteTrip(tripId: TripId) {
        viewModelScope.launch {
            deleteTripUseCase(tripId)
        }
    }
}
