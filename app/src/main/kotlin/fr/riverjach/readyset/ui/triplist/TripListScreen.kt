package fr.riverjach.readyset.ui.triplist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import fr.riverjach.readyset.domain.model.PackingTemplate
import fr.riverjach.readyset.domain.model.Trip
import fr.riverjach.readyset.domain.model.TripId
import fr.riverjach.readyset.ui.component.EmptyTripsView
import kotlinx.collections.immutable.ImmutableList

@Composable
fun TripListRoute(
    onNavigateToTripDetail: (TripId) -> Unit,
    viewModel: TripListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    TripListScreen(
        uiState = uiState,
        onAction = viewModel::onAction,
        onTripClick = onNavigateToTripDetail
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripListScreen(
    uiState: TripListUiState,
    onAction: (TripListUiAction) -> Unit,
    onTripClick: (TripId) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "ReadySet 🧳",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onAction(TripListUiAction.OpenCreateTripDialog) },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Créer un nouveau voyage"
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (uiState) {
                TripListUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is TripListUiState.Error -> {
                    Text(
                        text = uiState.message,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is TripListUiState.Success -> {
                    if (uiState.trips.isEmpty()) {
                        EmptyTripsView(modifier = Modifier.align(Alignment.Center))
                    } else {
                        TripListContent(
                            trips = uiState.trips,
                            onTripClick = onTripClick,
                            onDeleteTrip = { onAction(TripListUiAction.DeleteTrip(it)) }
                        )
                    }

                    if (uiState.isCreateTripDialogOpen) {
                        CreateTripDialog(
                            templates = uiState.templates,
                            isCreating = uiState.isCreating,
                            onDismiss = { onAction(TripListUiAction.DismissCreateTripDialog) },
                            onConfirm = { title, destination, tplId ->
                                onAction(
                                    TripListUiAction.ConfirmCreateTrip(
                                        title = title,
                                        destination = destination,
                                        selectedTemplateId = tplId
                                    )
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TripListContent(
    trips: ImmutableList<Trip>,
    onTripClick: (TripId) -> Unit,
    onDeleteTrip: (TripId) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(trips, key = { it.id.value }) { trip ->
            TripItemCard(
                trip = trip,
                onClick = { onTripClick(trip.id) },
                onDelete = { onDeleteTrip(trip.id) }
            )
        }
    }
}

@Composable
private fun TripItemCard(
    trip: Trip,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = trip.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                val destination = trip.destination
                if (!destination.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Place,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.height(16.dp)
                        )
                        Text(
                            text = destination,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }
                }
            }
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Supprimer le voyage",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
private fun CreateTripDialog(
    templates: ImmutableList<PackingTemplate>,
    isCreating: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (String, String?, String?) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var destination by remember { mutableStateOf("") }
    var selectedTemplateId by remember {
        mutableStateOf(templates.firstOrNull()?.id)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Nouveau voyage") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Nom du voyage (ex: Weekend à Rome)*") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = destination,
                    onValueChange = { destination = it },
                    label = { Text("Destination (optionnelle)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = "Modèle de liste de départ :",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )

                templates.forEach { template ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedTemplateId = template.id }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (template.id == selectedTemplateId),
                            onClick = { selectedTemplateId = template.id }
                        )
                        Column(modifier = Modifier.padding(start = 8.dp)) {
                            Text(
                                text = template.title,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = template.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(title, destination.ifBlank { null }, selectedTemplateId) },
                enabled = title.isNotBlank() && !isCreating
            ) {
                Text(text = if (isCreating) "Création..." else "Créer")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !isCreating) {
                Text(text = "Annuler")
            }
        }
    )
}
