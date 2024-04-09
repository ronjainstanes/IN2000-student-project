package no.uio.ifi.in2000.team11.havvarselapp

import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.runBlocking
import no.uio.ifi.in2000.team11.havvarselapp.data.FakeMetAlertsDataSource
import no.uio.ifi.in2000.team11.havvarselapp.data.alert.MetAlertsDataSource
import no.uio.ifi.in2000.team11.havvarselapp.data.alert.MetAlertsRepositoryImpl
import no.uio.ifi.in2000.team11.havvarselapp.model.alert.MetAlert
import org.junit.Assert
import org.junit.Test

class MetAlertsRepositoryTest {
    // creating a data source, only used for testing
    private val fakeDataSource: MetAlertsDataSource = FakeMetAlertsDataSource()

    // create a repository, injecting the fake data source
    private val repository = MetAlertsRepositoryImpl(fakeDataSource)

    // location used for testing
    private val drammen: LatLng = LatLng(59.7, 10.2)

    @Test
    fun metAlersRepository_getMetAlertsAtLocation_getMetAlerts() {
        runBlocking {
            val metAlerts : List<MetAlert> = repository.getMetAlertsAtLocation(drammen)

            // assert that method returns the expected value from fake data source
            Assert.assertEquals(metAlerts[0].id, "33")
        }
    }

    @Test
    fun metAlersRepository_getMetAlertsInNorway_getMetAlerts() {
        runBlocking {
            val metAlerts : List<MetAlert> = repository.getMetAlertsInNorway()

            // check that value is as expected
            Assert.assertEquals(metAlerts[0].id, "22")
        }
    }
}