package no.uio.ifi.in2000.team11.havvarselapp.data.weatherCurrentWaves

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.get
import io.ktor.serialization.gson.gson
import io.ktor.util.appendIfNameAbsent
import no.uio.ifi.in2000.team11.havvarselapp.model.weatherCurrentWaves.WeatherCurrentWaves
import no.uio.ifi.in2000.team11.havvarselapp.model.weatherCurrentWaves.WeatherCurrentWaves_Objekt

class GripfilesDataSource {

    // API endepunkt fra MET for weather, currents og waves for 'ish' hele Norge
    private val url: String =
        "https://gw-uio.intark.uh-it.no/in2000/weatherapi/gribfiles/1.1/available.json"

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
    suspend fun fetchWeatherCurrentWaves(): WeatherCurrentWaves_Objekt {

        val liste_weatherCurrentWaves: List<WeatherCurrentWaves> = client.get(url).body()
        val groupedWeatherCurrentWaves = liste_weatherCurrentWaves.groupBy { it.labels.area }
        return WeatherCurrentWaves_Objekt(groupedWeatherCurrentWaves)
    }





}
