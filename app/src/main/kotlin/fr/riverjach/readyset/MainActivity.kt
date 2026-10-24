package fr.riverjach.readyset

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import dagger.hilt.android.AndroidEntryPoint
import fr.riverjach.readyset.ui.theme.ReadySetTheme
import fr.riverjach.readyset.ui.tripdetail.TripDetailRoute
import fr.riverjach.readyset.ui.triplist.TripListRoute

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ReadySetTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ReadySetNavigation()
                }
            }
        }
    }
}

@Composable
fun ReadySetNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "trips"
    ) {
        composable("trips") {
            TripListRoute(
                onNavigateToTripDetail = { tripId ->
                    navController.navigate("trip_detail/${tripId.value}")
                }
            )
        }
        composable(
            route = "trip_detail/{tripId}",
            arguments = listOf(
                navArgument("tripId") { type = NavType.StringType }
            )
        ) {
            TripDetailRoute(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
