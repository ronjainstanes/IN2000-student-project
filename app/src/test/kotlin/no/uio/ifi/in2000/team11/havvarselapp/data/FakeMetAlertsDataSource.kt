package no.uio.ifi.in2000.team11.havvarselapp.data

import no.uio.ifi.in2000.team11.havvarselapp.data.alert.MetAlertsDataSource
import no.uio.ifi.in2000.team11.havvarselapp.model.alert.MetAlert

/**
 * This is a fake data source used for testing
 */
class FakeMetAlertsDataSource : MetAlertsDataSource {
    override suspend fun fetchMetAlertsInNorway(): List<MetAlert> {
        return mutableListOf(
            MetAlert("22", "", "", "", "",
                "", "", "", listOf(""), "", listOf(""), "")
        )
    }

    override suspend fun fetchMetAlertsAtLocation(lat: String, lon: String): List<MetAlert> {
        return mutableListOf(
            MetAlert("33", "", "", "", "",
                "", "", "", listOf(""), "", listOf(""), "")
        )
    }
}