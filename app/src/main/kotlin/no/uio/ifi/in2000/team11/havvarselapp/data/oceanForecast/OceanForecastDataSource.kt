package no.uio.ifi.in2000.team11.havvarselapp.data.oceanForecast

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.get
import io.ktor.serialization.gson.gson
import io.ktor.util.appendIfNameAbsent
import no.uio.ifi.in2000.team11.havvarselapp.model.oceanForecast.OceanForecast

/**
 * Data source used to fetch data from OceanForecast API
 */
class OceanForecastDataSource {
    // HTTP client
    private val client = HttpClient {
        defaultRequest {
            url("https://in2000.api.met.no/")
            headers.appendIfNameAbsent("X-Gravitee-API-Key", "7c5b6de3-2539-4c5e-bfb3-ec6377399ece")
        }
        install(ContentNegotiation) {
            gson()
        }
    }

    /**
     * Fetches data from the OceanForecast API, returns it as an object of
     * the OceanForecast data class
     */
    suspend fun fetchOceanForecast(lat: String, lon: String): OceanForecast? {
        return try {
            client.get("https://in2000.api.met.no/weatherapi/oceanforecast/2.0/complete?lat=${lat}&lon=${lon}")
                .body<OceanForecast>()
        } catch (e: Exception) {
            null
        }
    }
}

