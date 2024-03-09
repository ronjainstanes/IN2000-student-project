package no.uio.ifi.in2000.team11.havvarselapp.data.weather_current_waves
import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.gson.gson
import no.uio.ifi.in2000.team11.havvarselapp.model.weather_current_waves.WeatherCurrentWaves
import no.uio.ifi.in2000.team11.havvarselapp.model.weather_current_waves.WeatherCurrentWaves_Objekt

import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.gson.*
import io.ktor.client.*
import com.google.gson.Gson
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.statement.HttpResponse
import io.ktor.util.appendIfNameAbsent


class GripfilesDataSource {

    // API endepunkt fra MET for weather, currents og waves for 'ish' hele Norge
    private val url: String =
        "https://gw-uio.intark.uh-it.no/in2000/weatherapi/gribfiles/1.1/available.json?content=waves"

    // HTTP client
    private val client = HttpClient {
        defaultRequest {
            url("https://gw-uio.intark.uh-it.no/in2000/")
            headers.appendIfNameAbsent("X-Gravitee-API-Key", "7c5b6de3-2539-4c5e-bfb3-ec6377399ece")

        }

        /**
        install(ContentNegotiation) {
            gson()
        } */
    }


    suspend fun fetchWeatherCurrentWaves2(): WeatherCurrentWaves_Objekt {

        Log.e("TEST RESPONS",  "${url}" )

        //val liste_weatherCurrentWaves: List<WeatherCurrentWaves> = client.get(url).body()
        try {
            val liste_weatherCurrentWaves: HttpResponse = client.get(url)
        }
        catch (e:Exception) {
            Log.e("feilmelding??", "HEI", e)

        }
       // Log.e("TEST RESPONS 2 ",  "$liste_weatherCurrentWaves" )
        /**
        val groupedWeatherCurrentWaves = liste_weatherCurrentWaves.groupBy { it.area }

        groupedWeatherCurrentWaves.forEach { data ->
            Log.e("TEST DATA", "$data")
        }
        return WeatherCurrentWaves_Objekt(groupedWeatherCurrentWaves) */
        return WeatherCurrentWaves_Objekt(emptyMap())
    }



    /**
    suspend fun fetchWeatherCurrentWaves(): WeatherCurrentWaves_Objekt {
    try {
    val jsonRespons = client.get(url).body<String>()
    val weatherData = gson().fromJson(jsonRespons, Array<WeatherCurrentWaves>:: class.java).toList()
    return WeatherCurrentWaves_Objekt(weatherData)

    } catch (e: Exception) {
    Log.e("ERROR WEATHER_DATA", "Feilmelding: ${e.message}")
    throw e }
    }
     */


}

/**
 *         val liste_weatherCurrentWaves: List<WeatherCurrentWaves> = client.get(url).body()
 *         val groupedWeatherCurrentWaves = liste_weatherCurrentWaves.groupBy { it.area }
 *         return WeatherCurrentWaves_Objekt(groupedWeatherCurrentWaves)
 */
