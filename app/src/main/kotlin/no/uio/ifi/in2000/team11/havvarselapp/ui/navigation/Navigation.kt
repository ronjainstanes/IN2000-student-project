package no.uio.ifi.in2000.team11.havvarselapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.google.android.libraries.places.api.net.PlacesClient
import no.uio.ifi.in2000.team11.havvarselapp.SharedViewModel
import no.uio.ifi.in2000.team11.havvarselapp.ui.map.SeaMapScreen
import no.uio.ifi.in2000.team11.havvarselapp.ui.networkConnection.ConnectivityObserver
import no.uio.ifi.in2000.team11.havvarselapp.ui.weather.WeatherScreen

/**
 * Handles everything related to navigation in the app.
 */
@Composable
fun SetUpNavigation(
    placesClient: PlacesClient,
    connectivityObserver: ConnectivityObserver
) {
    val navController = rememberNavController()

    // Builds a navigation graph, setting up all ways to navigate in the app
    NavHost(
        navController = navController,
        startDestination = "start_destination_seamap"
    ) {

        // Start destination: seamap screen
        navigation(
            startDestination = "seamap_screen",
            route = "start_destination_seamap"
        ) {

            // The seamap screen
            composable("seamap_screen") { entry ->
                val viewModel = entry.sharedViewModel<SharedViewModel>(navController)
                val state by viewModel.sharedUiState.collectAsStateWithLifecycle()

                SeaMapScreen(
                    sharedUiState = state,
                    navController = navController,
                    placesClient,
                    updateLocation = {
                        viewModel.updateLocation(it)
                    },
                    connectivityObserver,
                    updateSearchHistory = {
                        viewModel.updateHistoryItems(it)
                    },
                )
            }

            // The weather screen
            composable("weather_screen") { entry ->
                val viewModel = entry.sharedViewModel<SharedViewModel>(navController)
                val state by viewModel.sharedUiState.collectAsStateWithLifecycle()

                WeatherScreen(
                    sharedUiState = state,
                    navController = navController,
                    connectivityObserver = connectivityObserver

                )
            }
        }
    }
}

/**
 * Makes it possible for all screens in the app to access the same
 * SharedViewModel and SharedUiState, which is necessary when data updated in one
 * part of the app needs to be shared with all screens.
 *
 * NOTE: The SharedViewModel is not sent into the screens, only the sharedUiState
 * is taken as a parameter. There is only one ViewModel per screen.
 */
@Composable
inline fun <reified T : ViewModel> NavBackStackEntry.sharedViewModel(
    navController: NavHostController,
): T {
    val navGraphRoute = destination.parent?.route ?: return viewModel()
    val parentEntry = remember(this) {
        navController.getBackStackEntry(navGraphRoute)
    }
    return viewModel(parentEntry)
}
