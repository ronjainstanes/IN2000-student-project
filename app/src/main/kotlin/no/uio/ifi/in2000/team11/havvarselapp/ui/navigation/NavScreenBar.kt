package no.uio.ifi.in2000.team11.havvarselapp.ui.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.outlined.Face
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.libraries.places.api.net.PlacesClient
import no.uio.ifi.in2000.team11.havvarselapp.data.location.LocationRepository
import no.uio.ifi.in2000.team11.havvarselapp.ui.map.SeaMapScreen
import no.uio.ifi.in2000.team11.havvarselapp.ui.metalert.AppUiState
import no.uio.ifi.in2000.team11.havvarselapp.ui.metalert.CurrentLocationAlert
import no.uio.ifi.in2000.team11.havvarselapp.ui.profile.Profil
import no.uio.ifi.in2000.team11.havvarselapp.ui.weather.WeatherScreen

/**
 * Dataklasse for å representere hvert element i navigasjonsmenyen
 */
data class BottomNavigationItem(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val badgeCount: Int,
)

/**
 * Denne skjermen har en navigasjonslinje nederst, og dynamisk innhold
 * som fornyes hver gang brukeren navigerer til en annen skjerm
 */
@Composable
fun NavScreen(
    region: String,
    placesClient: PlacesClient,
    navScreenViewModel: NavScreenViewModel = viewModel()
) {

    val currentLocation: String = region
    // Observe the UI state object from the ViewModel
    val appUiState: AppUiState by navScreenViewModel.appUiState.collectAsState()

    // Bruker funksjonen for å filtrere 'allMetAlert' listen basert på 'areal' feltet.
    // Funksjon som sjekker om en streng inneholder ordet "oslo" i en case-insensitive måte.
    fun String.containsIgnoreCase(other: String): Boolean {
        return this.contains(other, ignoreCase = true)
    }

    val filteredMetAlerts = appUiState.allMetAlerts.filter {
        it.area.containsIgnoreCase(currentLocation)
    }
    var amountOfAlerts = filteredMetAlerts.size

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    )
    {
        val items = listOf(
            BottomNavigationItem(
                title = "Kart",
                selectedIcon = Icons.Filled.Place,
                unselectedIcon = Icons.Outlined.Place,
                badgeCount = 0
            ),

            BottomNavigationItem(
                title = "Vær",
                selectedIcon = Icons.Filled.Menu,
                unselectedIcon = Icons.Outlined.Menu,
                badgeCount = amountOfAlerts
            ),


            BottomNavigationItem(
                title = "Profil",
                selectedIcon = Icons.Filled.Face,
                unselectedIcon = Icons.Outlined.Face,
                badgeCount = 0
            ),

            BottomNavigationItem(
                title = "Farevarsel",
                selectedIcon = Icons.Filled.Menu,
                unselectedIcon = Icons.Outlined.Menu,
                badgeCount = 0
            )
        )
        var selectedItemIndex by rememberSaveable {
            mutableIntStateOf(0)
        }
        Scaffold(
            bottomBar = {
                NavigationBar(
                    // First value = alpha and later RGB
                    containerColor = Color(0xFF_13_23_2C)
                ) {
                    items.forEachIndexed { index, item ->
                        NavigationBarItem(
                            selected = selectedItemIndex == index,
                            onClick = {
                                selectedItemIndex = index
                                //navController.navigate(item.title)
                            },
                            label = { Text(text = item.title) },
                            icon = {
                                BadgedBox(
                                    badge = {
                                        if (item.badgeCount != 0) {
                                            Badge {
                                                Text(text = item.badgeCount.toString())
                                            }
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = if (index == selectedItemIndex) {
                                            item.selectedIcon
                                        } else item.unselectedIcon,

                                        contentDescription = item.title
                                    )
                                }
                            },
                            colors = NavigationBarItemColors(
                                //2F4156
                                selectedIconColor = Color(0xFF_D9_D9_D9),
                                selectedTextColor = Color(0xFF_D9_D9_D9),
                                selectedIndicatorColor = Color(0xFF_2F_41_56),
                                unselectedIconColor = Color(0xFF_D9_D9_D9),
                                unselectedTextColor = Color(0xFF_D9_D9_D9),
                                disabledIconColor = Color(0xFF_13_23_2C),
                                disabledTextColor = Color(0xFF_13_23_2C)
                            )
                        )
                    }
                }
            }
        ) { innerPadding ->
            // Innholdsområde som endres avhengig av valgt navigasjonselement
            Column(modifier = Modifier.padding(innerPadding)) {

                when (selectedItemIndex) {

                    0 -> SeaMapScreen(placesClient)
                    1 -> WeatherScreen()
                    2 -> Profil()
                    // 3 -> SimpleMetAlertScreen()
                    3 -> CurrentLocationAlert("")
                    else -> SeaMapScreen(placesClient)
                }
            }
        }
    }
}
