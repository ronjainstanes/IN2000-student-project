package no.uio.ifi.in2000.team11.havvarselapp.ui.LocationForecast

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.team11.havvarselapp.data.locationForecast.LocatinForecastRepositoryImpl
import no.uio.ifi.in2000.team11.havvarselapp.model.locationForecast.LocationForecast
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

data class IsAPiCalled( // for å unngå for mange API kall
    var iscalled: Boolean = false
)


class LocationForecastViewModel(
    private val repository: LocatinForecastRepositoryImpl = LocatinForecastRepositoryImpl()
) : ViewModel() {
    private val _forecastInfo_UiState = MutableStateFlow<LocationForecast?>(null)
    val forecastInfo_UiState: StateFlow<LocationForecast?> = _forecastInfo_UiState.asStateFlow()
    var isAPiCalled by mutableStateOf(IsAPiCalled())


    init {
        loadForecast("59.911491", "10.757933") // starter opp med Latitude og longitude tilsvarende Oslo
    }

    fun loadForecast(lat: String, lon: String) {
        if (isAPiCalled.iscalled) {
            return;
        }
        else {
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    isAPiCalled.iscalled = true
                    val forecast = repository.getLocationForecastComplete(lat, lon)
                    _forecastInfo_UiState.update { forecast }
                    Log.e("VIEWMODEL", "API-kall")


                } catch (e: Exception) {
                    Log.e(
                        "ERROR ForeCast ViewModel",
                        "error in LocationForecastViewModel()loadForecast() ",
                        e
                    )
                }
            }
        }
    }

    /**
     * Denne returnerer dato og tid typ: '20 March 2024 16:00' i NORSK TID
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun convertDateAndTime(time: Int): String {
        val currentForecast = _forecastInfo_UiState.value
        val time_String: String = "${currentForecast?.properties?.timeseries?.get(time)?.time}"
        val parsedDate = ZonedDateTime.parse(time_String)
        val formats = DateTimeFormatter.ofPattern("d MMMM yyyy HH:mm")
        return parsedDate.withZoneSameInstant(ZoneId.of("Europe/Oslo")).format(formats)
    }

    /**
     * Denne returnerer norsk tid - typ: '16:00'
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun getNorwegianTime(time: Int): String {
        val currentForecast = _forecastInfo_UiState.value
        val time_String: String = "${currentForecast?.properties?.timeseries?.get(time)?.time}"
        var parsedDate = ZonedDateTime.parse(time_String)
        return parsedDate.withZoneSameInstant(ZoneId.of("Europe/Oslo")).hour.toString()
    }


    fun getCordinates(): List<Double>? {
        val currentForecast = _forecastInfo_UiState.value
        return currentForecast?.geometry?.coordinates
    }

    fun getTemperature(time: Int): String { // grader er i celsius
        val currentForecast = _forecastInfo_UiState.value
        val unit: String? = if (currentForecast?.properties?.meta?.units?.air_temperature == "celsius") "°C" else currentForecast?.properties?.meta?.units?.air_temperature
        return "${currentForecast?.properties?.timeseries?.get(time)?.data?.instant?.details?.air_temperature} $unit"
    }



    fun getWindSpeed(time: Int): String { // UV-indexen under klare himmelforhold
        val currentForecast = _forecastInfo_UiState.value
        return "${currentForecast?.properties?.timeseries?.get(time)?.data?.instant?.details?.wind_speed} ${currentForecast?.properties?.meta?.units?.wind_speed}"
    }


    fun getWindDirection(time: Int): String { // UV-indexen under klare himmelforhold
        val currentForecast = _forecastInfo_UiState.value
        val unit: String? = if (currentForecast?.properties?.meta?.units?.wind_from_direction == "degrees") "°" else currentForecast?.properties?.meta?.units?.wind_from_direction
        return "${currentForecast?.properties?.timeseries?.get(time)?.data?.instant?.details?.wind_from_direction}$unit"
    }


    fun getUVindex(time: Int): Double? { // UV-indexen under klare himmelforhold
        val currentForecast = _forecastInfo_UiState.value
        return currentForecast?.properties?.timeseries?.get(time)?.data?.instant?.details?.ultraviolet_index_clear_sky
    }
    // fog_area_fraction

    fun getFogAreaFraction(time: Int): String { // UV-indexen under klare himmelforhold
        val currentForecast = _forecastInfo_UiState.value
        return "${currentForecast?.properties?.timeseries?.get(time)?.data?.instant?.details?.fog_area_fraction} ${currentForecast?.properties?.meta?.units?.fog_area_fraction}"
    }

    fun getRelativeHumidity(time: Int): String { //Relativ fuktighet
        val currentForecast = _forecastInfo_UiState.value
        return "${currentForecast?.properties?.timeseries?.get(time)?.data?.instant?.details?.relative_humidity} ${currentForecast?.properties?.meta?.units?.relative_humidity}"
    }

    fun getPrecipitationAmount(time: Int): String{ //returnerer data for Nedbørmengde for neste time
        val currentForecast = _forecastInfo_UiState.value
        return "${currentForecast?.properties?.timeseries?.get(time)?.data?.next_1_hours?.details?.precipitation_amount} ${currentForecast?.properties?.meta?.units?.precipitation_amount}"
    }

    fun getCloudAreaFraction(time: Int): String{
        val currentForecast = _forecastInfo_UiState.value
        return "${currentForecast?.properties?.timeseries?.get(time)?.data?.instant?.details?.cloud_area_fraction} ${currentForecast?.properties?.meta?.units?.cloud_area_fraction}"
    }

    fun getProbabilityOfThunder(time: Int): String{
        val currentForecast = _forecastInfo_UiState.value
        return "${currentForecast?.properties?.timeseries?.get(time)?.data?.next_1_hours?.details?.probability_of_thunder} ${currentForecast?.properties?.meta?.units?.probability_of_thunder}"
    }

    fun getWeatherIcon(time: Int) : String? {
        val currentForecast = _forecastInfo_UiState.value
        return currentForecast?.properties?.timeseries?.get(time)?.data?.next_1_hours?.summary?.symbol_code
    }

    fun probabilityOfPrecipitation_12hours(): Double? { // Sannsynlighet for nedbør om 12 timer
        val currentForecast = _forecastInfo_UiState.value
        return currentForecast?.properties?.timeseries?.firstOrNull()?.data?.next_12_hours?.details?.probability_of_precipitation
    }
}