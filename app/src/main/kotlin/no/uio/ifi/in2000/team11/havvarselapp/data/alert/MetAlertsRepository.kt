package no.uio.ifi.in2000.team11.havvarselapp.data.alert

import com.google.android.gms.maps.model.LatLng
import no.uio.ifi.in2000.team11.havvarselapp.model.alert.MetAlert

interface MetAlertsRepository {
    /** Returnerer farevarsler på den gitte posisjonen, som en liste MetAlert-objekter */
    suspend fun getMetAlertsAtLocation(): List<MetAlert>

    /** Returnerer alle farevarsler i Norge, som en liste MetAlert-objekter */
    suspend fun getMetAlertsInNorway(): List<MetAlert>
}

class MetAlertsRepositoryImpl(
    // data source for farevarsler
    private val dataSource: MetAlertsDataSource = MetAlertsDataSource(),

    // TODO: bytt ut denne med brukerens posisjon/valgt posisjon
    private val pos: LatLng = LatLng(59.9, 10.73)

) : MetAlertsRepository {

    //TODO kan cache farevarsler, må da lage logikk for når det skal oppdateres

    override suspend fun getMetAlertsAtLocation(): List<MetAlert> {
        return dataSource.fetchMetAlertsAtLocation(
            pos.latitude.toString(),
            pos.longitude.toString()
        )
    }

    override suspend fun getMetAlertsInNorway(): List<MetAlert> {
        return dataSource.fetchMetAlertsInNorway()
    }
}