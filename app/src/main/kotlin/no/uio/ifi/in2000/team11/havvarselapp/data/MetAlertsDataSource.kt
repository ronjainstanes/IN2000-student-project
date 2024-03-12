package no.uio.ifi.in2000.team11.havvarselapp.data

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.google.android.gms.maps.model.LatLng
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.serialization.gson.gson
import io.ktor.util.appendIfNameAbsent
import no.uio.ifi.in2000.team11.havvarselapp.model.MetAlert
import no.uio.ifi.in2000.team11.havvarselapp.model.StartStopDate
import org.json.JSONObject
import java.time.ZonedDateTime

/**
 * Fetches data from Met Alerts
 */
class MetAlertsDataSource {

    // sett opp klienten
    private val client = HttpClient {
        defaultRequest {
        url("https://gw-uio.intark.uh-it.no/in2000/")
        headers.appendIfNameAbsent("X-Gravitee-API-Key", "7c5b6de3-2539-4c5e-bfb3-ec6377399ece") }

        install(ContentNegotiation) {
            gson()
        }
    }

    /**
     * Returnerer en liste med alle farevarsler (Feature-objekter).
     * Sjekk ut filen "MetAlerts" for å se hvordan
     * dataen er strukturert.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun fetchData(): List<MetAlert> {
        val response = client.get("https://gw-uio.intark.uh-it.no/in2000/weatherapi/metalerts/2.0/current.json")

        // JSONObjekt som inneholder en array med features (farevarsler)
        val allMetAlerts = JSONObject(response.bodyAsText())
        val features = allMetAlerts.getJSONArray("features")

        // liste der vi lagrer farevarslene som MetAlerts
        val allAlerts: MutableList<MetAlert> = mutableListOf()

        // gå gjennom alle farevarsler, parse til MetAlerts-objekter
        for(i in 0 until features.length()) {
            val alert = features.getJSONObject(i)
            val id = alert.getJSONObject("properties").optString("id")
            // id bør aldri være null, hvis det er det lagrer vi ikke dette farevaselet
            if (id.isBlank()) { break }
            val area = alert.getJSONObject("properties").optString("area")
            val title = alert.getJSONObject("properties").optString("title")
            val description = alert.getJSONObject("properties").optString("description")
            val consequences = alert.getJSONObject("properties").optString("consequences")
            val instruction = alert.getJSONObject("properties").optString("instruction")
            val triggerLevel = alert.getJSONObject("properties").optString("triggerLevel")

            // parser fra "2; yellow; Moderate" til en List<String>
            val awarenessLevelStr = alert.getJSONObject("properties").getString("awareness_level")
            val awarenessLevel = awarenessLevelStr.split(";") //TODO sjekk at det blir riktig uten mellomrom, eller bytt til "; "

            // parser fra "1; wind" til en List<String>
            val awarenessTypeStr = alert.getJSONObject("properties").getString("type")
            val awarenessType = awarenessTypeStr.split(";")

            // parser datoene til et StartStopDate-objekt
            val twoDates = alert.getJSONObject("when").getJSONArray("interval")
            val duration = StartStopDate(
                ZonedDateTime.parse(twoDates.getString(0)),
                ZonedDateTime.parse(twoDates.getString(1))
            )

            // henter koordinatene, bytter plass på "long, lat", lagrer som en liste med LatLng-objekter
            val allCoordinates = alert.getJSONObject("geometry").getJSONArray("coordinates").getJSONArray(0)
            val coordinates: MutableList<LatLng> = mutableListOf()
            for(j in 0 until allCoordinates.length()) {
                val c = allCoordinates.getJSONArray(j)
                coordinates.add(LatLng(c.getDouble(1), c.getDouble(0)))
            }

            // lagre det i et MetAlert, og legg til i listen
            val metAlert = MetAlert(id, area, title, coordinates, description, consequences,
                instruction, awarenessLevel, awarenessType, duration, triggerLevel)

            allAlerts.add(metAlert)
        }

        //TODO håndtere mislykket api-kall på en fin måte, sjekk statuskode


        //TODO rydd opp
        Log.w("LOLOLLOLOL",
            "RESULTAT AV API-KALL: ${allAlerts} \n")

        return allAlerts
    }
}
