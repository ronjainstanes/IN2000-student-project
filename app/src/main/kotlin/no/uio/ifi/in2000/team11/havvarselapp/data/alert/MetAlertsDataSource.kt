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
import java.net.ConnectException
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
            headers.appendIfNameAbsent("X-Gravitee-API-Key", "7c5b6de3-2539-4c5e-bfb3-ec6377399ece")
        }
    }

    /**
     * Returnerer alle farevarsler for hele Norge, som en liste med MetAlerts.
     * Sjekk ut filen "MetAlerts" for å se hvordan dataen er strukturert.
     */
    suspend fun fetchMetAlertsInNorway(): List<MetAlert> {
        return fetchMetAlerts(
            "https://gw-uio.intark.uh-it.no/in2000/" +
                    "weatherapi/metalerts/2.0/current.json"
        )
    }

    /**
     * Returnerer en liste med alle farevarsler som gjelder for posisjonen.
     * Sjekk ut filen "MetAlerts" for å se hvordan dataen er strukturert.
     */
    suspend fun fetchMetAlertsAtLocation(lat: String, lon: String): List<MetAlert> {
        return fetchMetAlerts(
            "https://gw-uio.intark.uh-it.no/in2000/" +
                    "weatherapi/metalerts/2.0/current.json?lat=${lat}&lon=${lon}"
        )
    }

    /**
     * Tar inn en URL, og henter alle farevarsler fra denne URL-en.
     * Parser dataen slik at det returneres som en liste med MetAlert-objekter.
     */
    private suspend fun fetchMetAlerts(url: String): List<MetAlert> {
        try {
            val response =
                client.get(url)

            Log.d(
                "MET_ALERTS_DATA_SOURCE",
                "Statuskode for MetAlerts API-kall: ${response.status} \n"
            )

            // JSONObjekt som inneholder en array med features (farevarsler)
            val allMetAlerts = JSONObject(response.bodyAsText())
            val features = allMetAlerts.getJSONArray("features")

            // liste der vi lagrer farevarslene som MetAlerts
            val allAlerts: MutableList<MetAlert> = mutableListOf()

            // gå gjennom alle farevarsler, parse til MetAlerts-objekter
            for (i in 0 until features.length()) {
                val alert = features.getJSONObject(i)

                // id bør aldri være null, hvis det er det lagrer vi ikke dette farevarselet
                val id = alert.getJSONObject("properties").optString("id")
                if (id.isBlank()) {
                    continue
                }

                val area = alert.getJSONObject("properties").optString("area")
                val title = alert.getJSONObject("properties").optString("title")
                val description = alert.getJSONObject("properties").optString("description")
                val consequences = alert.getJSONObject("properties").optString("consequences")
                val instruction = alert.getJSONObject("properties").optString("instruction")
                val triggerLevel = alert.getJSONObject("properties").optString("triggerLevel")
                val riskMatrixColor = alert.getJSONObject("properties").optString("riskMatrixColor")

                // parser fra "2; yellow; Moderate" til en List<String>
                val awarenessLevelStr =
                    alert.getJSONObject("properties").getString("awareness_level")
                val awarenessLevel =
                    awarenessLevelStr.split(";")

                // parser fra "1; wind" til en List<String>
                val awarenessTypeStr = alert.getJSONObject("properties").getString("awareness_type")
                val awarenessType = awarenessTypeStr.split(";")

                // parser datoene til et StartStopDate-objekt
                val twoDates = alert.getJSONObject("when").getJSONArray("interval")
                val duration = StartStopDate(
                    ZonedDateTime.parse(twoDates.getString(0)),
                    ZonedDateTime.parse(twoDates.getString(1))
                )

                // lagre det i et MetAlert, og legg til i listen
                val metAlert = MetAlert(
                    id,
                    area,
                    title,
                    description,
                    consequences,
                    instruction,
                    awarenessLevel,
                    riskMatrixColor,
                    awarenessType,
                    duration,
                    triggerLevel
                )
                allAlerts.add(metAlert)
            }
            return allAlerts

            // ikke koblet til internett, mulighet 1
        } catch (e: UnknownHostException) {
            Log.e(
                "MET_ALERTS_DATA_SOURCE",
                "API-kall mislykket. UnknownHostException. Ingen internett-tilkobling.\n"
            )
            return mutableListOf()

            // ikke koblet til internett, mulighet 2
        } catch (e: ConnectException) {
            Log.e(
                "MET_ALERTS_DATA_SOURCE",
                "API-kall mislykket. ConnectException. Ingen internett-tilkobling.\n"
            )
            return mutableListOf()

            // noe annet gikk galt
        } catch (e: Exception) {
            Log.e(
                "MET_ALERTS_DATA_SOURCE",
                "API-kall mislykket. Noe gikk galt.\n"
            )
            return mutableListOf()
        }
    }
}
