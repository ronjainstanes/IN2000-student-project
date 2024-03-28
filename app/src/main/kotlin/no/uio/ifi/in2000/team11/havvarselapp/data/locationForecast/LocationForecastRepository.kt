package no.uio.ifi.in2000.team11.havvarselapp.data.locationForecast

import no.uio.ifi.in2000.team11.havvarselapp.model.locationForecast.LocationForecast

/**
 * mellomman mellom datasource og Viewmodel for Location Forecast-API'et
 * må sende inn latitude og longtitude for oprådet data skal hentes fra
 */
interface LocationForecastRepository{
    suspend fun getLocationForecast(lat: String, lon: String): LocationForecast?
}

class LocationForecastRepositoryImpl(
    private val dataSource: LocationForecastDataSource = LocationForecastDataSource()
): LocationForecastRepository {

    override suspend fun getLocationForecast(lat: String, lon: String): LocationForecast {
        return dataSource.fetchLocationForecast(lat, lon)
    }
}