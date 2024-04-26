package no.uio.ifi.in2000.team11.havvarselapp

import android.app.Application
import no.uio.ifi.in2000.team11.havvarselapp.ui.map.SeaMapViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class SeaMapViewModelTest {
    private val viewModel = SeaMapViewModel(application = Application())

    @Test
    fun seaMapViewModel_placeOrRemoveMarker_uiUpdated() {
        // initial value
        val initialVisible = viewModel.mapUiState.value?.markerVisible
        viewModel.toggleMarkerVisibility()

        // new value
        val newVisible = viewModel.mapUiState.value?.markerVisible

        // check that value has changed
        assertNotEquals(initialVisible, newVisible)

        // check that value is as expected
        assertEquals(newVisible, (!initialVisible!!))

        // run again
        viewModel.toggleMarkerVisibility()
        val newerVisible = viewModel.mapUiState.value?.markerVisible

        // check that value has changed yet another time
        assertNotEquals(newVisible, newerVisible)
    }
}
