package no.uio.ifi.in2000.team11.havvarselapp.data.locationForecast

import android.util.Log
import no.uio.ifi.in2000.team11.havvarselapp.model.locationForecast.LocationForecast

/**
 * An interface for LocationForecastRepository, which
 * provides data for weather forecast at a given location.
 */
interface LocationForecastRepository {

    /**
     * Fetches weather data at the given location, from LocationForecast API.
     * Parameters: latitude and longitude for the given location.
     */
    suspend fun getLocationForecast(lat: String, lon: String): LocationForecast?
}

/**
 * Implementation of a LocationForecastRepository
 */
class LocationForecastRepositoryImpl(
    private val dataSource: LocationForecastDataSource = LocationForecastDataSource()
) : LocationForecastRepository {

    override suspend fun getLocationForecast(lat: String, lon: String): LocationForecast? {
        return try {
            dataSource.fetchLocationForecast(lat, lon)
        } catch (e: Exception) {
            Log.e(
                "LOCATION_FORECAST_DATA_SOURCE",
                "Location forecast API call failed.\n"
            )
            null
        }
    }
}