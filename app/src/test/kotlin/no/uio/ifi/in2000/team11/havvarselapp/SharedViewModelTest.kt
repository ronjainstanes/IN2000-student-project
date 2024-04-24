package no.uio.ifi.in2000.team11.havvarselapp

import com.google.android.gms.maps.model.LatLng
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Test

class SharedViewModelTest {
    private val viewModel = SharedViewModel()

    @Test
    fun seaMapViewModel_updateLocationUiState_uiUpdated() {
        val testCoordinates = LatLng(69.6, 18.9)

        // initial location
        val initialLoc = viewModel.sharedUiState.value.currentLocation
        viewModel.updateLocation(testCoordinates)

        // new location
        val newLoc = viewModel.sharedUiState.value.currentLocation

        // check that location has changed
        Assert.assertNotEquals(initialLoc, newLoc)

        // check that location has expected value
        assertEquals(newLoc, testCoordinates)
    }
}