package no.uio.ifi.in2000.team11.havvarselapp.data.alert

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.util.appendIfNameAbsent
import no.uio.ifi.in2000.team11.havvarselapp.model.alert.MetAlert
import org.json.JSONObject
import java.net.ConnectException
import java.net.UnknownHostException

/**
 * Data source used to fetch data from Met Alerts API
 */
interface MetAlertsDataSource {
    suspend fun fetchMetAlertsInNorway(): List<MetAlert>
    suspend fun fetchMetAlertsAtLocation(lat: String, lon: String): List<MetAlert>
}

/**
 * An implementation of a met-alert data source,
 * used to fetch data from Met Alerts API
 */
class MetAlertsDataSourceImpl : MetAlertsDataSource {

    // set up client that will do the api-call
    private val client = HttpClient {
        defaultRequest {
            url("https://gw-uio.intark.uh-it.no/in2000/")
            headers.appendIfNameAbsent("X-Gravitee-API-Key", "7c5b6de3-2539-4c5e-bfb3-ec6377399ece")
        }
    }

    /**
     * Returns a list of all active met-alerts in Norway.
     * See the file "MetAlert" to see what this object contains and how the data is structured.
     */
    override suspend fun fetchMetAlertsInNorway(): List<MetAlert> {
        return fetchMetAlerts(
            "https://gw-uio.intark.uh-it.no/in2000/" +
                    "weatherapi/metalerts/2.0/current.json"
        )
    }

    /**
     * Returns a list of active met-alerts for the gicen location.
     * See the file "MetAlert" to see what this object contains and how the data is structured.
     */
    override suspend fun fetchMetAlertsAtLocation(lat: String, lon: String): List<MetAlert> {
        return fetchMetAlerts(
            "https://gw-uio.intark.uh-it.no/in2000/" +
                    "weatherapi/metalerts/2.0/current.json?lat=${lat}&lon=${lon}"
        )
    }

    /**
     * Takes a URL as an argument, fetches and parses the data,
     * and returns a list of MetAlert-objects.
     */
    private suspend fun fetchMetAlerts(url: String): List<MetAlert> {
        try {
            val response =
                client.get(url)

            Log.d(
                "MET_ALERTS_DATA_SOURCE",
                "Status code for MetAlerts API: ${response.status} \n"
            )

            // JSONObject containing an array of "features" (met-alerts)
            val allMetAlerts = JSONObject(response.bodyAsText())
            val features = allMetAlerts.getJSONArray("features")

            // list to save all MetAlerts
            val allAlerts: MutableList<MetAlert> = mutableListOf()

            // go through all met-alerts, parse into MetAlert-objects
            for (i in 0 until features.length()) {
                val alert = features.getJSONObject(i)

                // ID should never be null, in that case we will not save this met-alert
                val id = alert.getJSONObject("properties").optString("id")
                if (id.isBlank()) {
                    continue
                }

                val area = alert.getJSONObject("properties").optString("area")
                val title = alert.getJSONObject("properties").optString("title")
                val description = alert.getJSONObject("properties").optString("description")
                val event = alert.getJSONObject("properties").optString("event")
                val consequences = alert.getJSONObject("properties").optString("consequences")
                val instruction = alert.getJSONObject("properties").optString("instruction")
                val triggerLevel = alert.getJSONObject("properties").optString("triggerLevel")
                val riskMatrixColor = alert.getJSONObject("properties").optString("riskMatrixColor")

                // parses from "2; yellow; Moderate" to a List<String>
                val awarenessLevelStr =
                    alert.getJSONObject("properties").getString("awareness_level")
                val awarenessLevel =
                    awarenessLevelStr.split(";")

                // parser fra "1; wind" til en List<String>
                val awarenessTypeStr = alert.getJSONObject("properties").getString("awareness_type")
                val awarenessType = awarenessTypeStr.split(";")

                // save info in a MetAlert-object and add to list
                val metAlert = MetAlert(
                    id,
                    area,
                    title,
                    description,
                    event,
                    consequences,
                    instruction,
                    awarenessLevel,
                    riskMatrixColor,
                    awarenessType,
                    triggerLevel
                )
                allAlerts.add(metAlert)
            }
            return allAlerts

            // no internet
        } catch (e: UnknownHostException) {
            Log.e(
                "MET_ALERTS_DATA_SOURCE",
                "Met Alerts API call failed. No internet access.\n"
            )
            return mutableListOf()

            // no internet
        } catch (e: ConnectException) {
            Log.e(
                "MET_ALERTS_DATA_SOURCE",
                "Met Alerts API call failed. No internet access.\n"
            )
            return mutableListOf()

            // something went wrong
        } catch (e: Exception) {
            Log.e(
                "MET_ALERTS_DATA_SOURCE",
                "Met Alerts API call failed.\n"
            )
            return mutableListOf()
        }
    }
}
