package no.uio.ifi.in2000.team11.havvarselapp

import kotlinx.coroutines.runBlocking
import no.uio.ifi.in2000.team11.havvarselapp.data.oceanForecast.OceanForecastDataSource
import org.junit.Test
import org.junit.Assert

// Unit test suite for OceanForecastDataSource.
class OceanForecastDataSourceTest {

    // Test case to verify that fetchOceanForecast returns a non-null OceanForecast object
    // when valid latitude and longitude coordinates are passed as arguments.
    @Test
    fun fetchOceanForecast_should_return_a_non_null_OceanForecast_object() {
        runBlocking {
            // Calls the fetchOceanForecast with valid latitude ("59.9") and longitude ("10.7") strings.
            val oceanForecast = OceanForecastDataSource().fetchOceanForecast("59.9", "10.7")
            // Asserts that the returned object is not null. If it is null, the test will fail.
            Assert.assertNotNull("OceanForecast data should not be null", oceanForecast)
        }
    }

    // Test case to verify that fetchOceanForecast returns a null OceanForecast object
    // when invalid latitude and longitude coordinates are passed as arguments.
    @Test
    fun fetchOceanForecast_should_return_a_null_OceanForecast_object() {
        runBlocking {
            // Calls the fetchOceanForecast with invalid latitude ("591") and longitude ("11") strings.
            val oceanForecast = OceanForecastDataSource().fetchOceanForecast("591", "11")
            // Asserts that the returned object is null. If it is not null, the test will fail.
            Assert.assertNull("OceanForecast data should be null", oceanForecast)
        }
    }
}

