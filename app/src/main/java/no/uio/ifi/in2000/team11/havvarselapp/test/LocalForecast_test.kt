package no.uio.ifi.in2000.team11.havvarselapp.test

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.team11.havvarselapp.data.locationForecast.LocatinForecastRepositoryImpl
import no.uio.ifi.in2000.team11.havvarselapp.data.locationForecast.LocationForecastDataSource
import no.uio.ifi.in2000.team11.havvarselapp.ui.LocationForecast.LocationForecastViewModel


@Composable
fun TestLocationForecastDataSource() {
    val dataSource = LocationForecastDataSource()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(key1 = Unit) {
        coroutineScope.launch {
            try {
                val locationForecast = dataSource.fetchLocationForecast_LatAndLon( "59.9", "10.7")
                Log.e("LOCATIONFORECAST_DATA: ", " \n\nDATA-SOURCE LOCATIONFORECAST: CURRENT WHEATER DATA\n " +
                        "\nCordinates: ${locationForecast.geometry.coordinates} " +
                        "\nTemperature: ${locationForecast.properties.timeseries.firstOrNull()?.data?.instant?.details?.air_temperature} ${locationForecast.properties.meta.units.air_temperature}" +
                        "\nUV-Index: ${locationForecast.properties.timeseries.firstOrNull()?.data?.instant?.details?.ultraviolet_index_clear_sky} ${locationForecast.properties.meta.units.ultraviolet_index_clear_sky}" +
                        "\nWind speed: ${locationForecast.properties.timeseries.firstOrNull()?.data?.instant?.details?.wind_speed} ${locationForecast.properties.meta.units.wind_speed}" +
                        "\nWind direction: ${locationForecast.properties.timeseries.firstOrNull()?.data?.instant?.details?.wind_from_direction} ${locationForecast.properties.meta.units.wind_from_direction}\n\n")



            } catch (e: Exception) {
                // Logg eventuelle feil
                Log.e("ERROR LOCATIONFORECAST_DATA", "feilmelding: ${e.message}")
                e.printStackTrace()
            }
        }
    }
}

@Composable
fun TestLocationRepository() {
    val repository = LocatinForecastRepositoryImpl()

    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(key1 = Unit) {
        coroutineScope.launch {
            try {
                val locationForecast = repository.getLocationForecast("59.9", "10.7")
                if (locationForecast != null) {
                    Log.e("REPOSITORY-LOCATIONFORECAST: ", " \n\nREPOSOTORY LOCATIONFORECAST: CURRENT WHEATER DATA\n " +
                            "\nCordinates: ${locationForecast.geometry.coordinates} " +
                            "\nTemperature: ${locationForecast.properties.timeseries.firstOrNull()?.data?.instant?.details?.air_temperature} ${locationForecast.properties.meta.units.air_temperature}" +
                            "\nUV-Index: ${locationForecast.properties.timeseries.firstOrNull()?.data?.instant?.details?.ultraviolet_index_clear_sky} ${locationForecast.properties.meta.units.ultraviolet_index_clear_sky}" +
                            "\nWind speed: ${locationForecast.properties.timeseries.firstOrNull()?.data?.instant?.details?.wind_speed} ${locationForecast.properties.meta.units.wind_speed}" +
                            "\nWind direction: ${locationForecast.properties.timeseries.firstOrNull()?.data?.instant?.details?.wind_from_direction} ${locationForecast.properties.meta.units.wind_from_direction}\n\n")
                }


            } catch (e: Exception) {
                Log.e("ERROR forecast REPOSITORY", "Feil ved LocatinForecastRepositoryImpl()...", e)
            }
        }
    }
}



@Composable
fun TestLocationForecastViewModel() {
    val forecastViewModel = LocationForecastViewModel()
    val state = forecastViewModel.forecastInfo_UiState.collectAsState()

    val coordinater = Pair("59.9", "10.7")
    LaunchedEffect(coordinater) {
        forecastViewModel.loadForecast(coordinater.first, coordinater.second)
    }
    try {
        while (true) {
        state.value?.let { locationForecast ->
                Log.e(
                    "ViewModel-LOCATIONFORECAST: ",
                    " \n\nVIEW-MODEL LOCATIONFORECAST: CURRENT WHEATER DATA\n " +
                            "\nCordinates: ${locationForecast.geometry.coordinates} " +
                            "\nTemperature: ${locationForecast.properties.timeseries.firstOrNull()?.data?.instant?.details?.air_temperature} ${locationForecast.properties.meta.units.air_temperature}" +
                            "\nUV-Index: ${locationForecast.properties.timeseries.firstOrNull()?.data?.instant?.details?.ultraviolet_index_clear_sky} ${locationForecast.properties.meta.units.ultraviolet_index_clear_sky}" +
                            "\nWind speed: ${locationForecast.properties.timeseries.firstOrNull()?.data?.instant?.details?.wind_speed} ${locationForecast.properties.meta.units.wind_speed}" +
                            "\nWind direction: ${locationForecast.properties.timeseries.firstOrNull()?.data?.instant?.details?.wind_from_direction} ${locationForecast.properties.meta.units.wind_from_direction}\n\n "
                )
            }
            break
        }

    } catch (e: Exception) {
        // Logg eventuelle feil
        Log.e("ERROR VIEWMODEL FORECAST", "feilmelding: ${e.message}")
        e.printStackTrace()
    }


}



