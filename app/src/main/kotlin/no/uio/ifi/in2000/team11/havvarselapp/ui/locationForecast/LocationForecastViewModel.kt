package no.uio.ifi.in2000.team11.havvarselapp.ui.locationForecast

import android.content.Context
import android.location.Geocoder
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
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
import java.io.FileNotFoundException
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

    private val _placeNameState = MutableStateFlow("Laster...")
    val placeNameState: StateFlow<String> = _placeNameState.asStateFlow()


    /**
    This method caches the last forecast and makes it possible to continue using the weather-page after loosing wifi
     */
    private fun saveForecastToFile(context: Context, forecast: LocationForecast?) {
        try {
            context.openFileOutput("forecast.json", Context.MODE_PRIVATE).use { output ->
                val jsonData = Gson().toJson(forecast)
                output.write(jsonData.toByteArray()) }
                Log.d("SAVE_FORECAST", "Forecast data saved successfully")
        } catch (e: Exception) {
            Log.e("SAVE_FORECAST", "Error saving forecast data", e)
        }
    }

    /**
    This method caches the last ocean-forecast and makes it possible to continue using the Ocean-page after loosing wifi
     */
    private fun saveOceanForecastToFile(context: Context, forecast: OceanForecast?) {
        try {
            context.openFileOutput("oceanforecast.json", Context.MODE_PRIVATE).use { output ->
                val jsonData = Gson().toJson(forecast)
                output.write(jsonData.toByteArray()) }
                Log.d("SAVE_OCEANFORECAST", "Ocean Forecast data saved successfully")

        } catch (e: Exception) {
            Log.e("SAVE_OCEANFORECAST", "Error saving ocean-forecast data", e)
        }
    }

    /**
    This method caches the last place-name so the place-name on top of the weather-page is still there and correct for the data shown after loosing wifi
     */
    private fun savePlaceNameToFile(context: Context, placeName: String) {
        try {
            context.openFileOutput("place_name.txt", Context.MODE_PRIVATE).use { output ->
                output.write(placeName.toByteArray())
                Log.d("SAVE_PLACE_NAME", "Place name saved successfully: $placeName")
            }
        } catch (e: Exception) {
            Log.e("SAVE_PLACE_NAME", "Error saving place name", e)
        }
    }

    /**
     * This methode return the cached place-name for when there is no wifi
     */
    private fun loadPlaceNameFromFile(context: Context): String? {
        return try {
            context.openFileInput("place_name.txt").use { input ->
                val placeName = input.bufferedReader().use { it.readText() }
                Log.d("LOAD_PLACE_NAME", "Place name loaded successfully: $placeName")
                placeName
            }
        } catch (e: FileNotFoundException) {
            Log.e("LOAD_PLACE_NAME", "File not found", e)
            null
        } catch (e: Exception) {
            Log.e("LOAD_PLACE_NAME", "Error loading place name", e)
            null
        }
    }


    /**
     * This methode return the cached LocationForecast for when there is no wifi
     */
    private fun loadForecastFromFile(context: Context): LocationForecast? {
        return try {
            context.openFileInput("forecast.json").use { input ->
                val json = input.bufferedReader().use { it.readText() }
                Log.d("LOAD_FORECAST", "Forecast data loaded successfully: $json")
                Gson().fromJson(json, LocationForecast::class.java)
            }
        } catch (e: FileNotFoundException) {
            Log.e("LOAD_FORECAST", "File not found", e)
            null
        } catch (e: Exception) {
            Log.e("LOAD_FORECAST", "Error loading forecast data", e)
            null
        }
    }

    /**
     * This methode return the cached OceanForecast for when there is no wifi
     */
    private fun loadOceanForecastFromFile(context: Context): OceanForecast? {
        return try {
            context.openFileInput("oceanforecast.json").use { input ->
                val json = input.bufferedReader().use { it.readText() }
                Log.d("LOAD_OCEANFORECAST", "Forecast data loaded successfully: $json")
                Gson().fromJson(json, OceanForecast::class.java)
            }
        } catch (e: FileNotFoundException) {
            Log.e("LOAD_OCEANFORECAST", "File not found", e)
            null
        } catch (e: Exception) {
            Log.e("LOAD_OCEANFORECAST", "Error loading oceanforecast data", e)
            null
        }
    }

    /**
     * The function that loads both the weather and ocean forecast.
     * First it tries to get data from the API online.
     * If it fails - likely because there is no wifi, it will try to return the last cached API instead.
     */
    fun loadForecast(lat: String, lon: String, context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                var forecast = repository.getLocationForecast(lat, lon)
                var oceanForecast = repositoryOcean.getOceanForecast(lat, lon)

                // true when the API-call succeeded
                if (forecast?.properties?.timeseries?.size != null) {
                    Log.d("FORECAST_VM", "API-CALL GOOD")
                    // caches and saves the forecast for when there is no wifi
                    saveForecastToFile(context, forecast)
                    saveOceanForecastToFile(context, oceanForecast)
                }
                // true when the API-call failed, likely because there is no internet connection. Trying to get the cached forecast instead
                else {
                    Log.d("FORECAST_VM", "\nAPI-CALL FAILED\nTRYING TO get CACHED DATA\n\n")
                    forecast = loadForecastFromFile(context)
                    oceanForecast = loadOceanForecastFromFile(context)
                }
                _forecastInfoUiState.update { forecast } // updating the UI-states with either the updated API or the cached one if there is no internet connection
                _oceanForecastUiState.update { oceanForecast }
            }

            catch (e: Exception) {
                Log.e("FORECAST_VM - cached", "error in LocationForecastViewModel()loadForecast()\n", e)
            }
        }
    }



    /**
     * After the user has searched for a location on the map screen, or
     * placed a marker on a new location, this function fetches the name of
     * the location. To be displayed on top of the weather screen.
     *
     * NOTE: "getFromLocation" is marked deprecated at API level 33,
     * however the new non-deprecated method has minimum API level 33.
     * We found no other to use this without setting minSdk to 33 and maxSdk to 34,
     * which is a very short API range, and we have chosen not to do this.
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
                    } else if (adminArea != null) {
                        if (sublocality != null) {
                            "$adminArea, $sublocality, $country"
                        } else if (subAdminArea != null) {
                            "$subAdminArea, $country"
                        } else {
                            "$adminArea, $country"
                        }
                    } else if (sublocality != null) {
                        "$sublocality, $country"
                    } else if (subAdminArea != null) {
                        "$subAdminArea, $country"
                    } else {
                        addresses[0].getAddressLine(0)
                    }
                    //Display "Ukjent" if location wasn´t found
                } else {
                    "Ukjent"
                }
            } catch (e: IOException) {
                e.printStackTrace()
                if (loadPlaceNameFromFile(context) != null) {
                    loadPlaceNameFromFile(context) // loading cached place-name when there is no internet connection
                } else {
                    "Laster...."
                }
            }
            if (placeName != null) {
                savePlaceNameToFile(context, placeName) // caching place-name for when there is no internet connection
                _placeNameState.value = placeName
            }
        }
    }
}

