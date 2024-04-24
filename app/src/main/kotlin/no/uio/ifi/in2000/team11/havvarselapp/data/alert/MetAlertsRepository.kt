package no.uio.ifi.in2000.team11.havvarselapp.data.alert

import com.google.android.gms.maps.model.LatLng
import no.uio.ifi.in2000.team11.havvarselapp.model.alert.MetAlert

interface MetAlertsRepository {
    /** Returns a list of met-alerts at the given location */
    suspend fun getMetAlertsAtLocation(loc: LatLng): List<MetAlert>

    /** Returns a list of all met-alerts in Norway */
    suspend fun getMetAlertsInNorway(): List<MetAlert>
}

class MetAlertsRepositoryImpl(
    // Data source for met-alerts
    private val dataSource: MetAlertsDataSource = MetAlertsDataSource(),

) : MetAlertsRepository {

    override suspend fun getMetAlertsAtLocation(loc: LatLng): List<MetAlert> {
        return dataSource.fetchMetAlertsAtLocation(
            loc.latitude.toString(),
            loc.longitude.toString()
        )
    }

    override suspend fun getMetAlertsInNorway(): List<MetAlert> {
        return dataSource.fetchMetAlertsInNorway()
    }
}