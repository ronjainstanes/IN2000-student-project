package no.uio.ifi.in2000.team11.havvarselapp

import com.google.android.gms.maps.model.LatLng
import no.uio.ifi.in2000.team11.havvarselapp.ui.map.SeaMapViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class SeaMapViewModelTest {
    private val viewModel = SeaMapViewModel()

    @Test
    fun seaMapViewModel_placeOrRemoveMarker_uiUpdated() {
        // startverdier
        val initialLoc = viewModel.mapUiState.value.currentLocation
        val initialVisible = viewModel.mapUiState.value.markerVisible

        val testCoordinates = LatLng(59.3, 8.2)
        viewModel.placeOrRemoveMarker(testCoordinates)

        // nye verdier
        val newLoc = viewModel.mapUiState.value.currentLocation
        val newVisible = viewModel.mapUiState.value.markerVisible

        // sjekk at de er endret
        assertNotEquals(initialLoc, newLoc)
        assertNotEquals(initialVisible, newVisible)

        // sjekk at de har forventet verdi
        assertEquals(newLoc, testCoordinates)
        assertEquals(newVisible, (!initialVisible))

        viewModel.placeOrRemoveMarker(testCoordinates)
        val newerLoc = viewModel.mapUiState.value.currentLocation
        val newerVisible = viewModel.mapUiState.value.markerVisible

        // sjekk at de endres til riktig verdi også denne gangen
        assertEquals(newLoc, newerLoc)
        assertNotEquals(newVisible, newerVisible)
    }

    @Test
    fun seaMapViewModel_updateLocationUiState_uiUpdated() {
        val initialLoc = viewModel.mapUiState.value.currentLocation
        val testCoordinates = LatLng(69.6, 18.9)
        viewModel.updateLocationUiState(testCoordinates)
        val newLoc = viewModel.mapUiState.value.currentLocation

        // sjekk at posisjon er endret, og har riktig verdi
        assertNotEquals(initialLoc, newLoc)
        assertEquals(newLoc, testCoordinates)
    }
}
