package no.uio.ifi.in2000.team11.havvarselapp.ui.locationForecast

import android.content.Context
import android.location.Geocoder
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.team11.havvarselapp.data.locationForecast.LocationForecastRepositoryImpl
import no.uio.ifi.in2000.team11.havvarselapp.data.oceanForecast.OceanForecastRepositoryImpl
import no.uio.ifi.in2000.team11.havvarselapp.model.locationForecast.LocationForecast
import no.uio.ifi.in2000.team11.havvarselapp.model.oceanForecast.OceanForecast
import java.io.IOException
import java.util.Locale

//NOTE: this file contains an warning because we were enable to use non-deprecated method
//warning explanation: getFromLocation() is still used

class LocationForecastViewModel(
    private val repository: LocationForecastRepositoryImpl = LocationForecastRepositoryImpl(),
    private val repositoryOcean: OceanForecastRepositoryImpl = OceanForecastRepositoryImpl()
) : ViewModel() {
    private val _forecastInfoUiState = MutableStateFlow<LocationForecast?>(null)
    val forecastInfoUiState: StateFlow<LocationForecast?> = _forecastInfoUiState.asStateFlow()
    private val _oceanForecastUiState = MutableStateFlow<OceanForecast?>(null)
    val oceanForecastUiState: StateFlow<OceanForecast?> = _oceanForecastUiState.asStateFlow()

    private val _placeNameState = MutableStateFlow("Upload...")
    val placeNameState: StateFlow<String> = _placeNameState.asStateFlow()

    init {
        loadForecast("59.91", "10.75") // Start with Latitude and longitude for Oslo
    }

    fun loadForecast(lat: String, lon: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                Log.d("LOCATION_FORECAST_VIEWMODEL", "GOOD")
                val forecast = repository.getLocationForecast(lat, lon)
                _forecastInfoUiState.update { forecast }
                val oceanForecast = repositoryOcean.getOceanForecast(lat, lon)
                _oceanForecastUiState.update { oceanForecast }
                Log.d("LOCATION_FORECAST_VIEWMODEL", "API-call")


            } catch (e: Exception) {
                Log.e(
                    "LOCATION_FORECAST_VIEWMODEL",
                    "error in LocationForecastViewModel()loadForecast() ",
                    e
                )
            }
        }

    }

    /**
     * Reverse geocoder for placename on top of weatherScreen
     */
    fun setCurrentPlaceName(context: Context, lat: Double, lon: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            val geocoder = Geocoder(context, Locale.getDefault())
            val placeName = try {
                val addresses = geocoder.getFromLocation(lat, lon, 1)
                println(addresses)
                if (addresses?.isNotEmpty() == true) {
                    // Return formated placename
                    val city = addresses[0].locality
                    val adminArea = addresses[0].adminArea
                    val subAdminArea = addresses[0].subAdminArea

                    val sublocality = addresses[0].subLocality
                    val country = addresses[0].countryName

                    if (city != null) {
                        "$city, $country"
                    } else if (sublocality != null) {
                        "$sublocality, $country"
                    } else if (subAdminArea != null) {
                        "$subAdminArea, $country"
                    } else if (adminArea != null) {
                        "$adminArea, $country"
                    } else {
                        addresses[0].getAddressLine(0)
                    }
                //Display "Ukjent" if location wasn´t found
                } else {
                    "Ukjent"
                }
            } catch (e: IOException) {
                e.printStackTrace()
                "Laster..."
            }
            _placeNameState.value = placeName
        }
    }
}
