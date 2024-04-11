package no.uio.ifi.in2000.team11.havvarselapp.data.oceanForecast

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.get
import io.ktor.serialization.gson.gson
import io.ktor.util.appendIfNameAbsent
import no.uio.ifi.in2000.team11.havvarselapp.model.oceanForecast.OceanForecast

class OceanForecastDataSource {
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

    // oslo - lat: 59.9, lon: 10.7
    suspend fun fetchOceanForecast(lat: String, lon: String): OceanForecast? {
        return try {
            client.get("https://gw-uio.intark.uh-it.no/in2000/weatherapi/oceanforecast/2.0/complete?lat=${lat}&lon=${lon}")
                .body<OceanForecast>()
        } catch (e: Exception) {
            Log.e("OCEAN_FORECAST_DATA_SOURCE",
                "Ocean forecast API call failed.\n")
            null
        }
    }
}

