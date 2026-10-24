package fr.riverjach.readyset.ui.tripdetail

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import fr.riverjach.readyset.domain.model.ItemCategory
import fr.riverjach.readyset.domain.model.ItemId
import fr.riverjach.readyset.domain.model.PackingItem
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
class TripDetailScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun should_display_trip_title_items_and_progress_bar() {
        // Given
        val trip = Trip(id = TripId("1"), title = "Weekend à Amsterdam")
        val items = persistentListOf(
            PackingItem(ItemId("item-1"), trip.id, "Brosse à dents", ItemCategory.TOILETRIES, isPacked = true),
            PackingItem(ItemId("item-2"), trip.id, "Chargeur", ItemCategory.TECH, isPacked = false)
        )
        val uiState = TripDetailUiState.Success(
            trip = trip,
            items = items,
            searchQuery = "",
            totalCount = 2,
            packedCount = 1,
            progressPercentage = 50
        )

        // When
        composeTestRule.setContent {
            ReadySetTheme {
                TripDetailScreen(
                    uiState = uiState,
                    onAction = {},
                    onNavigateBack = {}
                )
            }
        }

        // Then
        composeTestRule.onNodeWithText("Weekend à Amsterdam").assertIsDisplayed()
        composeTestRule.onNodeWithText("Brosse à dents").assertIsDisplayed()
        composeTestRule.onNodeWithText("Chargeur").assertIsDisplayed()
        composeTestRule.onNodeWithText("1 sur 2 emballés").assertIsDisplayed()
        composeTestRule.onNodeWithText("50 %").assertIsDisplayed()
    }

    @Test
    fun should_trigger_search_action_when_user_types_in_search_bar() {
        // Given
        var capturedAction: TripDetailUiAction? = null
        val trip = Trip(id = TripId("1"), title = "Voyage")
        val uiState = TripDetailUiState.Success(
            trip = trip,
            items = persistentListOf(),
            searchQuery = ""
        )

        composeTestRule.setContent {
            ReadySetTheme {
                TripDetailScreen(
                    uiState = uiState,
                    onAction = { capturedAction = it },
                    onNavigateBack = {}
                )
            }
        }

        // When: User types "brosse" in search bar
        composeTestRule.onNodeWithText("Rechercher une affaire...")
            .performTextInput("brosse")

        // Then: Action UpdateSearchQuery is emitted
        assertThat(capturedAction).isInstanceOf(TripDetailUiAction.UpdateSearchQuery::class.java)
        assertThat((capturedAction as TripDetailUiAction.UpdateSearchQuery).query).isEqualTo("brosse")
    }

    @Test
    fun should_trigger_toggle_action_when_item_clicked() {
        // Given
        var capturedAction: TripDetailUiAction? = null
        val trip = Trip(id = TripId("1"), title = "Voyage")
        val item = PackingItem(ItemId("item-10"), trip.id, "Passeport", ItemCategory.DOCUMENTS, isPacked = false)
        val uiState = TripDetailUiState.Success(
            trip = trip,
            items = persistentListOf(item)
        )

        composeTestRule.setContent {
            ReadySetTheme {
                TripDetailScreen(
                    uiState = uiState,
                    onAction = { capturedAction = it },
                    onNavigateBack = {}
                )
            }
        }

        // When: User clicks on the item
        composeTestRule.onNodeWithText("Passeport").performClick()

        // Then
        assertThat(capturedAction).isInstanceOf(TripDetailUiAction.ToggleItemPacked::class.java)
        val action = capturedAction as TripDetailUiAction.ToggleItemPacked
        assertThat(action.itemId).isEqualTo(item.id)
        assertThat(action.currentStatus).isFalse()
    }
}
