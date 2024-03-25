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
 * Henter LocationForecast-API'et, oppretter et objekt av data klassen LocationForecast, må sende inn Latitude og Longtitude på området data skal hentes fra
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

    suspend fun fetchLocationForecast_complete(lat: String, lon: String): LocationForecast {
        val compleatelocationforecast: LocationForecast = client.get("https://gw-uio.intark.uh-it.no/in2000/weatherapi/locationforecast/2.0/complete?lat=${lat}&lon=${lon}").body()
        return compleatelocationforecast
    }                                                                                         // https://api.met.no/weatherapi/locationforecast/2.0/complete?lat=60.10&lon=9.58

}
