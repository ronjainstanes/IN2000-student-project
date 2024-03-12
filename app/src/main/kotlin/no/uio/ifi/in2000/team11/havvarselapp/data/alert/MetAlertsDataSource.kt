package no.uio.ifi.in2000.team11.havvarselapp.data.alert

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.util.appendIfNameAbsent
import no.uio.ifi.in2000.team11.havvarselapp.model.alert.MetAlert
import no.uio.ifi.in2000.team11.havvarselapp.model.alert.StartStopDate
import org.json.JSONObject
import java.net.UnknownHostException
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
    }

    /**
     * Returnerer en liste med alle farevarsler (Feature-objekter).
     * Sjekk ut filen "MetAlerts" for å se hvordan
     * dataen er strukturert.
     */
    suspend fun fetchData(): MutableList<MetAlert> {
        try {
            val response =
                client.get("https://gw-uio.intark.uh-it.no/in2000/" +
                        "weatherapi/metalerts/2.0/current.json")

            Log.d(
                "MET_ALERTS_DATA_SOURCE",
                "Statuskode for API-kall: ${response.status} \n"
            )

            // JSONObjekt som inneholder en array med features (farevarsler)
            val allMetAlerts = JSONObject(response.bodyAsText())
            val features = allMetAlerts.getJSONArray("features")

            // liste der vi lagrer farevarslene som MetAlerts
            val allAlerts: MutableList<MetAlert> = mutableListOf()

            // gå gjennom alle farevarsler, parse til MetAlerts-objekter
            for (i in 0 until features.length()) {
                val alert = features.getJSONObject(i)
                val id = alert.getJSONObject("properties").optString("id")

                // id bør aldri være null, hvis det er det lagrer vi ikke dette farevarselet
                if (id.isBlank()) {
                    continue
                }
                val area = alert.getJSONObject("properties").optString("area")
                val title = alert.getJSONObject("properties").optString("title")
                val description = alert.getJSONObject("properties").optString("description")
                val consequences = alert.getJSONObject("properties").optString("consequences")
                val instruction = alert.getJSONObject("properties").optString("instruction")
                val triggerLevel = alert.getJSONObject("properties").optString("triggerLevel")

                // parser fra "2; yellow; Moderate" til en List<String>
                val awarenessLevelStr =
                    alert.getJSONObject("properties").getString("awareness_level")
                val awarenessLevel =
                    awarenessLevelStr.split(";")

                // parser fra "1; wind" til en List<String>
                val awarenessTypeStr = alert.getJSONObject("properties").getString("type")
                val awarenessType = awarenessTypeStr.split(";")

                // parser datoene til et StartStopDate-objekt
                val twoDates = alert.getJSONObject("when").getJSONArray("interval")
                val duration = StartStopDate(
                    ZonedDateTime.parse(twoDates.getString(0)),
                    ZonedDateTime.parse(twoDates.getString(1))
                )

                // lagre det i et MetAlert, og legg til i listen
                val metAlert = MetAlert(
                    id, area, title, description, consequences,
                    instruction, awarenessLevel, awarenessType, duration, triggerLevel
                )

                allAlerts.add(metAlert)
            }

            return allAlerts

        // ikke koblet til internett
        } catch (e: UnknownHostException) {
            Log.e("MET_ALERTS_DATA_SOURCE",
                "API-kall mislykket. Ingen internett-tilkobling.\n")
            return mutableListOf()

        // noe annet gikk galt
        } catch (e: Exception) {
            Log.e("MET_ALERTS_DATA_SOURCE",
                "API-kall mislykket. Feilmelding: ${e.message}\n")
            return mutableListOf()
        }
    }
}
