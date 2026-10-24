package fr.riverjach.readyset.ui.triplist

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import fr.riverjach.readyset.domain.model.Trip
import fr.riverjach.readyset.domain.model.TripId
import fr.riverjach.readyset.ui.theme.ReadySetTheme
import kotlinx.collections.immutable.persistentListOf
import org.assertj.core.api.Assertions.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class TripListScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun should_display_empty_view_when_no_trips() {
        // Given
        val uiState = TripListUiState.Success(
            trips = persistentListOf(),
            templates = persistentListOf()
        )

        // When
        composeTestRule.setContent {
            ReadySetTheme {
                TripListScreen(
                    uiState = uiState,
                    onAction = {},
                    onTripClick = {}
                )
            }
        }

        // Then
        composeTestRule.onNodeWithText("Aucun voyage prévu").assertIsDisplayed()
        composeTestRule.onNodeWithText("Créez votre premier voyage pour préparer votre liste d'affaires !").assertIsDisplayed()
    }

    @Test
    fun should_trigger_open_dialog_when_fab_clicked() {
        // Given
        var capturedAction: TripListUiAction? = null
        val uiState = TripListUiState.Success(
            trips = persistentListOf(),
            templates = persistentListOf()
        )

        composeTestRule.setContent {
            ReadySetTheme {
                TripListScreen(
                    uiState = uiState,
                    onAction = { capturedAction = it },
                    onTripClick = {}
                )
            }
        }

        // When: User clicks the FloatingActionButton
        composeTestRule.onNodeWithContentDescription("Créer un nouveau voyage").performClick()

        // Then
        assertThat(capturedAction).isEqualTo(TripListUiAction.OpenCreateTripDialog)
    }

    @Test
    fun should_display_trips_and_trigger_trip_click() {
        // Given
        var clickedTripId: TripId? = null
        val trip = Trip(id = TripId("trip-abc"), title = "Roadtrip Islande", destination = "Reykjavik")
        val uiState = TripListUiState.Success(
            trips = persistentListOf(trip),
            templates = persistentListOf()
        )

        // When
        composeTestRule.setContent {
            ReadySetTheme {
                TripListScreen(
                    uiState = uiState,
                    onAction = {},
                    onTripClick = { clickedTripId = it }
                )
            }
        }

        // Then
        composeTestRule.onNodeWithText("Roadtrip Islande").assertIsDisplayed()
        composeTestRule.onNodeWithText("Reykjavik").assertIsDisplayed()

        // User clicks the trip card
        composeTestRule.onNodeWithText("Roadtrip Islande").performClick()
        assertThat(clickedTripId).isEqualTo(trip.id)
    }
}
