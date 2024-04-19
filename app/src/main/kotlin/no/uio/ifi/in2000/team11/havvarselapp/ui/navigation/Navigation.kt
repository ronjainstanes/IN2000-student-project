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
import no.uio.ifi.in2000.team11.havvarselapp.ui.weather.WeatherScreen

/**
 * Handles everything related to navigation in the app.
 */
@Composable
fun SetUpNavigation(
    placesClient: PlacesClient
) {
    val navController = rememberNavController()

    //TODO: initialise a placesClient here?

    NavHost(
        navController = navController,
        startDestination = "start_destination_seamap"
    ) {

        navigation(
            startDestination = "seamap_screen",
            route = "start_destination_seamap"
        ) {

            composable("seamap_screen") { entry ->
                val viewModel = entry.sharedViewModel<SharedViewModel>(navController)
                val state by viewModel.sharedUiState.collectAsStateWithLifecycle()

                SeaMapScreen(
                    sharedUiState = state,
                    navController = navController,
                    placesClient,
                    updateLocation = {
                        viewModel.updateLocation(it)
                    }
                )
            }
            composable("weather_screen") { entry ->
                val viewModel = entry.sharedViewModel<SharedViewModel>(navController)
                val state by viewModel.sharedUiState.collectAsStateWithLifecycle()

                WeatherScreen(
                    sharedUiState = state,
                    navController = navController,
                )
            }
        }
    }
}

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
