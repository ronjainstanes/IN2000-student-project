package no.uio.ifi.in2000.team11.havvarselapp.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.unit.sp
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
                    .weight(1f),
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

            Spacer(modifier = Modifier.width(3.dp).height(66.dp).background(white))

            // button for weather-screen
            Button(
                modifier = Modifier
                    .weight(1f), onClick = {
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

/**
 * The button to navigate to the map screen
 */
@Composable
fun ButtonMapContext(isSelected: Boolean) {
    val white = Color(100, 110, 125, 100)
    val gray = Color(19, 35, 44, 200)
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(70.dp, 50.dp)
            .clip(CircleShape)
            // changes background color if the button is clicked
            .background(if (isSelected) white else gray)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Default.Place,
                contentDescription = "map"
            )
            Text("Kart", fontSize = (15.sp))
        }
    }
}

/**
 * The button to navigate to the weather screen
 */
@Composable
fun ButtonWeatherContext(isSelected: Boolean) {
    val white = Color(100, 110, 125, 100)
    val gray = Color(19, 35, 44, 200)
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(70.dp,50.dp)
            .clip(CircleShape)
            // changes background color if the button is clicked
            .background(if (isSelected) white else gray)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Outlined.WbCloudy,
                contentDescription = "weather"
            )
            Text("Vær", fontSize = (15.sp))
        }
    }
}
