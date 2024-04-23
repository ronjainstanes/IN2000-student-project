package no.uio.ifi.in2000.team11.havvarselapp.data.alert

import com.google.android.gms.maps.model.LatLng
import no.uio.ifi.in2000.team11.havvarselapp.model.alert.MetAlert

interface MetAlertsRepository {
    /** Returnerer farevarsler på den gitte posisjonen, som en liste MetAlert-objekter */
    suspend fun getMetAlertsAtLocation(pos: LatLng): List<MetAlert>

    /** Returnerer alle farevarsler i Norge, som en liste MetAlert-objekter */
    suspend fun getMetAlertsInNorway(): List<MetAlert>
}

class MetAlertsRepositoryImpl(
    // data source for farevarsler
    private val dataSource: MetAlertsDataSource = MetAlertsDataSource(),

) : MetAlertsRepository {

    override suspend fun getMetAlertsAtLocation(pos: LatLng): List<MetAlert> {
        return dataSource.fetchMetAlertsAtLocation(
            pos.latitude.toString(),
            pos.longitude.toString()
        )
    }

    override suspend fun getMetAlertsInNorway(): List<MetAlert> {
        return dataSource.fetchMetAlertsInNorway()
    }
}