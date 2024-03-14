package no.uio.ifi.in2000.team11.havvarselapp.test

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.team11.havvarselapp.data.weather_current_waves.GripfilesDataSource

@Composable
fun TestGribfilesDataSource() {
    val dataSource = GripfilesDataSource()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(key1 = Unit) {
        coroutineScope.launch {
            try {
                val weatherList = dataSource.fetchWeatherCurrentWaves()

                weatherList.groupedWeatherCurrentWaves.forEach { (area, dataList) ->
                    dataList.forEach { data ->
                        Log.e("WEATHER_DATA: ", " \nArea: $area, \nContent: ${data.params.content}, \nUpdated: ${data.updated}, \nURI: ${data.uri}\n ")
                    }
                }

            } catch (e: Exception) {
                // Logg eventuelle feil
                Log.e("ERROR WEATHER_DATA", "feilmelding: ${e.message}")
                e.printStackTrace()
            }
        }
    }
}