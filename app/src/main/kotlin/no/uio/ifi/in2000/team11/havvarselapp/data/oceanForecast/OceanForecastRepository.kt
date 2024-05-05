package no.uio.ifi.in2000.team11.havvarselapp.data.oceanForecast

import no.uio.ifi.in2000.team11.havvarselapp.model.oceanForecast.OceanForecast

/**
 * An interface for OceanForecastRepository, which
 * provides data for weather forecast at a given location.
 */
interface OceanForecastRepository {

    /**
     * Fetches maritime data at the given location, from OceanForecast API.
     * Parameters: latitude and longitude for the given location.
     */
    suspend fun getOceanForecast(lat: String, lon: String): OceanForecast?
}

/**
 * Implementation of an OceanForecastRepository
 */
class OceanForecastRepositoryImpl(
    private val dataSource: OceanForecastDataSource = OceanForecastDataSource()
) : OceanForecastRepository {

    override suspend fun getOceanForecast(lat: String, lon: String): OceanForecast? {
        return dataSource.fetchOceanForecast(lat, lon)
    }
}