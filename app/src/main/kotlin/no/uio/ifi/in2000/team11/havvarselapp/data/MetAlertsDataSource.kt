package no.uio.ifi.in2000.team11.havvarselapp.data

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.util.appendIfNameAbsent
import no.uio.ifi.in2000.team11.havvarselapp.model.Features

/**
 * Fetches data from Met Alerts
 */
class MetAlertsDataSource {

    private val client = HttpClient {
        defaultRequest {
        url("https://gw-uio.intark.uh-it.no/in2000/")
        headers.appendIfNameAbsent("X-Gravitee-API-Key", "7c5b6de3-2539-4c5e-bfb3-ec6377399ece") }
    }

    suspend fun fetchData() {
        val response: HttpResponse =
            client.get("https://gw-uio.intark.uh-it.no/in2000/weatherapi/gribfiles/1.1/available.json?content=waves")

        Log.w("MET_ALERTS_DATA_SOURCE",
            "Fetching data from Met Alerts. HTTP status: ${response.status}")

        //TODO håndtere mislykket api-kall på en fin måte

        //TODO RYDD OPP
        Log.w("LOLOLLOLOL",
            "RESULTAT AV API-KALL: ${response.bodyAsText()}")
    }
}
