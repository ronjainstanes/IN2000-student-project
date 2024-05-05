package no.uio.ifi.in2000.team11.havvarselapp.data.locationForecast

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.get
import io.ktor.serialization.gson.gson
import io.ktor.util.appendIfNameAbsent
import no.uio.ifi.in2000.team11.havvarselapp.model.locationForecast.LocationForecast

/**
 * A data source used to fetch data from the LocationForecast API.
 */
class LocationForecastDataSource {

    // HTTP client
    private val client = HttpClient {
        defaultRequest {
            url("https://gw-uio.intark.uh-it.no/in2000/")
            headers.appendIfNameAbsent("X-Gravitee-API-Key", "7c5b6de3-2539-4c5e-bfb3-ec6377399ece")
        }

        install(ContentNegotiation) {
            gson()
        }
    }

    /**
     * Fetches data from the LocationForecast API.
     * Returns the data as an object of the LocationForecast data class.
     * Must provide Latitude and Longitude for the given location.
     */
    suspend fun fetchLocationForecast(lat: String, lon: String): LocationForecast {
        return client.get(
            "https://gw-uio.intark.uh-it.no/in2000/weatherapi/" +
                    "locationforecast/2.0/complete?lat=${lat}&lon=${lon}"
        ).body<LocationForecast>()
    }
}
