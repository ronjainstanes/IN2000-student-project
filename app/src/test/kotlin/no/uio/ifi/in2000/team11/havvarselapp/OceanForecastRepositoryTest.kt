package no.uio.ifi.in2000.team11.havvarselapp

import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import no.uio.ifi.in2000.team11.havvarselapp.data.oceanForecast.OceanForecastDataSource
import no.uio.ifi.in2000.team11.havvarselapp.data.oceanForecast.OceanForecastRepositoryImpl
import no.uio.ifi.in2000.team11.havvarselapp.model.oceanForecast.OceanForecast
import org.junit.Assert
import org.junit.Before
import org.junit.Test

// This test class is responsible for testing the behavior of OceanForecastRepositoryImpl
class OceanForecastRepositoryTest {

    // Late-initialized properties for the repository and data source
    private lateinit var oceanForecastRepository: OceanForecastRepositoryImpl
    private val dataSource: OceanForecastDataSource = mockk(relaxed = true)
    private val mockOceanForecast: OceanForecast = mockk()

    // Setup method that runs before each test, initializing objects
    @Before
    fun setUp() {
        // Instantiate the repository with the mock data source
        oceanForecastRepository = OceanForecastRepositoryImpl(dataSource)
    }

    // Test case to verify that valid latitude and longitude return forecast data
    @Test
    fun getOceanForecast_validCoordinates_returnsForecast() {
        runBlocking {

        val latitude = "59.9"
        val longitude = "10.7"
        // Define behavior for the mock, returning mockOceanForecast when fetchOceanForecast is called
        coEvery { dataSource.fetchOceanForecast(any(), any()) } returns mockOceanForecast

        // Make the actual call to the method under test
        val result = oceanForecastRepository.getOceanForecast(latitude, longitude)

        // Verify that the result is not null when valid coordinates are provided
        Assert.assertNotNull("OceanForecast should not be null with valid coordinates", result)
        // Check if the result matches the mock object
        Assert.assertEquals("The result should be the mocked OceanForecast", mockOceanForecast, result)
        }
    }

    // Test case to verify that the method returns null in case of a network error
    @Test
    fun getOceanForecast_networkError_returnsNull() {
        runBlocking {
            // Arrange parameters for fetchOceanForecast()
            val latitude = "59.9"
            val longitude = "10.1"
            // Simulate a network error by having the mock return null
            coEvery { dataSource.fetchOceanForecast(latitude, longitude) } returns null

            // Make the call to get the ocean forecast
            val result = oceanForecastRepository.getOceanForecast(latitude, longitude)

            // Check that the method correctly returns null in case of network errors
            Assert.assertNull("OceanForecast should be null when a network error occurs", result)
        }
    }
}