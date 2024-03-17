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
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import no.uio.ifi.in2000.team11.havvarselapp.ui.SimpleMetAlertScreen
import no.uio.ifi.in2000.team11.havvarselapp.ui.map.SeaMap
import no.uio.ifi.in2000.team11.havvarselapp.ui.profile.Profil
import no.uio.ifi.in2000.team11.havvarselapp.ui.weather.WeatherScreen


// Dataklasse for å representere hvert element i navigasjonsmenyen
data class BottomNavigationItem(
    val titel: String,
    val selectedIcon: ImageVector,
    val unselectedIcon:ImageVector,
)
// En funksjon som representerer hovedskjermen i appen med en navigasjonsbar

@Composable
fun NavScreen(){
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    )
    {
        val items = listOf(
            BottomNavigationItem(titel = "Kart",
                selectedIcon = Icons.Filled.Place,
                unselectedIcon = Icons.Outlined.Place),

            BottomNavigationItem(titel = "Vær",
                selectedIcon = Icons.Filled.Menu,
                unselectedIcon = Icons.Outlined.Menu),

            BottomNavigationItem(titel = "Profil",
                selectedIcon = Icons.Filled.Face,
                unselectedIcon = Icons.Outlined.Face),

            BottomNavigationItem(titel = "Farevarsel",
                selectedIcon = Icons.Filled.Menu,
                unselectedIcon = Icons.Outlined.Menu),)
        var selectedItemIndex by rememberSaveable {
            mutableIntStateOf(0)
        }
        Scaffold(
            bottomBar = {
                NavigationBar{
                    items.forEachIndexed{index,item ->
                        NavigationBarItem(
                            selected = selectedItemIndex == index,
                            onClick = { selectedItemIndex = index
                                //navController.navigate(item.titel)
                            },
                            label = {Text(text = item.titel)},
                            icon = {
                                Icon(
                                imageVector = if(index == selectedItemIndex)
                                {
                                    item.selectedIcon
                                    } else item.unselectedIcon,

                                contentDescription = item.titel
                            )
                            }
                        )
                    }
                }
            }
        )
        {
            innerPadding ->
            // Innholdsområde som endres avhengig av valgt navigasjonselement
            Column(modifier = Modifier.padding(innerPadding)) {

                when (selectedItemIndex) {

                    0 -> SeaMap()
                    1 -> WeatherScreen()
                    2 -> Profil()
                    3 -> SimpleMetAlertScreen()
                    else -> SeaMap()
                }
            }
        }
    }
}
