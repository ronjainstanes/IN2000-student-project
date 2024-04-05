package no.uio.ifi.in2000.team11.havvarselapp.ui.weather

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import no.uio.ifi.in2000.team11.havvarselapp.ui.locationForecast.LocationForecastViewModel

/**
 * Henter riktig ikon for værskjermen.
 * NB: Funksjonen har "SuppressLint" annotering fordi ikonnavn
 * og ID er dynamiske i stedet for statiske, noe som gir en warning.
 */
@SuppressLint("DiscouragedApi")
@Composable
fun ValidImageWeather(forecastViewModel: LocationForecastViewModel){
    val iconName = forecastViewModel.getWeatherIcon(0)
    val context = LocalContext.current
    val resId = context.resources.getIdentifier(iconName, "drawable", context.packageName)
    val weatherIcon: ImageVector = ImageVector.vectorResource(id = resId)

    Image(imageVector = weatherIcon, contentDescription = "image",
        Modifier
            .size(110.dp)
            .padding(top = 3.dp, bottom = 10.dp))
}