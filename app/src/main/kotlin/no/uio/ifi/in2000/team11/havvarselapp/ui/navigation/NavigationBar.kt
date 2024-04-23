package no.uio.ifi.in2000.team11.havvarselapp.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.outlined.WbCloudy
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

// if button is clicked
var selectedButtonMap = true
var selectedButtonWeather = false

/**
 * The navigation bar at the bottom of every screen
 */
@Composable
fun NavigationBarWithButtons(navController: NavController) {
    val white = Color(69, 79, 92, 255) // button is active
    val gray = Color(19, 35, 44, 255)  // button is inactive
    val boardersWidth = 2.dp

    Column(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom,
    ) {
        Row(
            horizontalArrangement = Arrangement.Center, // Sentrerer knappene i raden
            modifier = Modifier
                .fillMaxWidth()
                .padding(1.dp)
                .background(gray)
        ) {

            // button for seamap-screen
            Button(
                modifier = Modifier
                    .size(60.dp) // Definerer en eksakt størrelse for knappen

                    .weight(1f)
                    /*.border(
                        width = boardersWidth,
                        color = white,
                        shape = RectangleShape
                    )*/,
                // when clicked: navigate, change color
                onClick = {
                    selectedButtonMap = true
                    navController.navigate("seamap_screen")
                    selectedButtonWeather = false
                },
                colors = ButtonDefaults.buttonColors(
                    if (selectedButtonMap) white else gray),
                shape = CircleShape,

                ) {
                ButtonMapContext()
            }

            // button for weather-screen
            Button(
                modifier = Modifier
                    .size(60.dp) // Definerer en eksakt størrelse for knappen

                    .weight(1f)
                    /*.border(
                        width = boardersWidth,
                        // changes color if button is active/inactive
                        color = white,
                        shape = RectangleShape
                    // when clicked: navigate, change color
                    )*/, onClick = {
                    selectedButtonWeather = true
                    navController.navigate("weather_screen")
                    selectedButtonMap = false
                }, colors = ButtonDefaults.buttonColors(
                    if (selectedButtonWeather) white else gray
                ),
                shape = CircleShape

            ) {
                ButtonWeatherContext()
            }
        }
    }
}

@Composable
fun ButtonMapContext() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            imageVector = Icons.Default.Place,
            contentDescription = "map"
        )
        Text("Kart")
    }
}

@Composable
fun ButtonWeatherContext() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            imageVector = Icons.Outlined.WbCloudy,
            contentDescription = "weather"
        )
        Text("Vær")
    }
}
