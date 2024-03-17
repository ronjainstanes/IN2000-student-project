package no.uio.ifi.in2000.team11.havvarselapp.ui.LocationForecast

import android.util.Log
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
                    val forecast = repository.getLocationForecast(lat, lon)
                    _forecastInfo_UiState.update { forecast }
                    Log.e("LOCATIONFORECAST_VIEWMODEL", "Gjør et API-kall")

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

    fun getCordinates(): List<Double>? {
        val currentForecast = _forecastInfo_UiState.value
        return currentForecast?.geometry?.coordinates
    }

    fun getTemperatureNow(): Double? { // grader er i celsius
        val currentForecast = _forecastInfo_UiState.value
        return currentForecast?.properties?.timeseries?.firstOrNull()?.data?.instant?.details?.air_temperature
    }
    fun getTemperatureUnit(): String? { // grader er i celsius
        val currentForecast = _forecastInfo_UiState.value
        return currentForecast?.properties?.meta?.units?.air_temperature
    }


    fun getUVIndexNow(): Double? { // UV-indexen under klare himmelforhold
        val currentForecast = _forecastInfo_UiState.value
        return currentForecast?.properties?.timeseries?.firstOrNull()?.data?.instant?.details?.ultraviolet_index_clear_sky
    }
    fun getUVIndexUnit(): String? { // grader er i celsius
        val currentForecast = _forecastInfo_UiState.value
        return currentForecast?.properties?.meta?.units?.ultraviolet_index_clear_sky
    }


    fun getWindSpeedNow(): Double? { // UV-indexen under klare himmelforhold
        val currentForecast = _forecastInfo_UiState.value
        return currentForecast?.properties?.timeseries?.firstOrNull()?.data?.instant?.details?.wind_speed
    }
    fun getWindSpeedUnit(): String? { // grader er i celsius
        val currentForecast = _forecastInfo_UiState.value
        return currentForecast?.properties?.meta?.units?.wind_speed
    }


    fun getWindDirection(): Double? { // UV-indexen under klare himmelforhold
        val currentForecast = _forecastInfo_UiState.value
        return currentForecast?.properties?.timeseries?.firstOrNull()?.data?.instant?.details?.wind_from_direction
    }

    fun getWindDirectionUnit(): String? { // grader er i celsius
        val currentForecast = _forecastInfo_UiState.value
        return currentForecast?.properties?.meta?.units?.wind_from_direction
    }

    fun getCurrentWeatherData(): String? {
        return " \n\nCURRENT WHEATER DATA\n " +
                "\nCordinates: ${getCordinates()} " +
                "\nTemperature: ${getTemperatureNow()} ${getTemperatureUnit()}" +
                "\nUV-Index: ${getUVIndexNow()} ${getUVIndexUnit()}" +
                "\nWind speed: ${getWindSpeedNow()} ${getWindSpeedUnit()}" +
                "\nWind direction: ${getWindDirection()} ${getWindDirectionUnit()}\n\n"
    }


}