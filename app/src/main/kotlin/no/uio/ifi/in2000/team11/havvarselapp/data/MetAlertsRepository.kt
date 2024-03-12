package no.uio.ifi.in2000.team11.havvarselapp.data

import no.uio.ifi.in2000.team11.havvarselapp.data.alert.MetAlertsDataSource
import no.uio.ifi.in2000.team11.havvarselapp.model.alert.MetAlert

interface MetAlertsRepository {
    /** Returnerer farevarsler som en liste MetAlert-objekter */
    suspend fun getAllMetAlerts(): List<MetAlert>
}

class MetAlertsRepositoryImpl(
    // opprett en instans av data source
    private val dataSource: MetAlertsDataSource = MetAlertsDataSource()
) : MetAlertsRepository {

    //TODO kan cache farevarsler, må da lage logikk for når det skal oppdateres

    override suspend fun getAllMetAlerts(): List<MetAlert> {
        //TODO husk å logge at metoden kalles

        return dataSource.fetchData()
    }
}