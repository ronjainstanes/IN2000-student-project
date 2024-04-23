package no.uio.ifi.in2000.team11.havvarselapp.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.draw.clip
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
    val white = Color(69, 79, 92, 167) // button is active
    val gray = Color(19, 35, 44, 255)  // button is inactive
    val boardersWidth = 2.dp

    Column(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth() // Ensures that the Row fills up the entire screen width
                .wrapContentHeight(align = Alignment.Bottom)
        ) {

            // button for seamap-screen
            Button(
                modifier = Modifier
                    .weight(1f)
                    .border(
                        width = boardersWidth,
                        // changes color if button is active/inactive
                        color = white,
                        shape = RectangleShape
                    ),
                // when clicked: navigate, change color
                onClick = {
                    selectedButtonMap = true
                    navController.navigate("seamap_screen")
                    selectedButtonWeather = false
                },
                colors = ButtonDefaults.buttonColors(gray),
                shape = RectangleShape,

                ) {
                ButtonMapContext(selectedButtonMap)
            }

            // button for weather-screen
            Button(
                modifier = Modifier
                    .weight(1f)
                    .border(
                        width = boardersWidth,
                        // changes color if button is active/inactive
                        color = white,
                        shape = RectangleShape
                        // when clicked: navigate, change color
                    ), onClick = {
                    selectedButtonWeather = true
                    navController.navigate("weather_screen")
                    selectedButtonMap = false
                }, colors = ButtonDefaults.buttonColors(gray), shape = RectangleShape

            ) {
                ButtonWeatherContext(selectedButtonWeather)
            }
        }
    }
}

@Composable
fun ButtonMapContext(isSelected: Boolean) {
    val white = Color(100, 110, 125, 200)
    val gray = Color(19, 35, 44, 255)
    Box(
        contentAlignment = Alignment.Center, // Senterer innholdet i Box
        modifier = Modifier
            .size(52.dp) // Setter størrelsen på Box, denne må matche størrelsen du setter for Button
            .clip(CircleShape) // Gjør at Box får en rund form
            .background(if (isSelected) white else gray) // Endrer bakgrunnsfarge basert på om knappen er valgt
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Default.Place,
                contentDescription = "map"
            )
            Text("Kart")
        }
    }
}

@Composable
fun ButtonWeatherContext(isSelected: Boolean) {
    val white = Color(100, 110, 125, 200)
    val gray = Color(19, 35, 44, 255)
    Box(
        contentAlignment = Alignment.Center, // Senterer innholdet i Box
        modifier = Modifier
            .size(52.dp) // Setter størrelsen på Box, denne må matche størrelsen du setter for Button
            .clip(CircleShape) // Gjør at Box får en rund form
            .background(if (isSelected) white else gray) // Endrer bakgrunnsfarge basert på om knappen er valgt
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Outlined.WbCloudy,
                contentDescription = "weather"
            )
            Text("Vær")
        }
    }
}
