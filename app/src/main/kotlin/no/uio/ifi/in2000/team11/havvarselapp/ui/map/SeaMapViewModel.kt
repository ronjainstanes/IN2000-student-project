package no.uio.ifi.in2000.team11.havvarselapp.ui.map

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class MapUiState(

    /** Er det en synlig pin på kartet? */
    val markerVisible: Boolean = false
)

class SeaMapViewModel : ViewModel() {
    // oppretter en privat state flow, denne er mutable og kan derfor endres på
    private val _mapUiState = MutableStateFlow(MapUiState())

    // oppretter en immutable/uforanderlig UiState av typen StateFlow
    val mapUiState: StateFlow<MapUiState> = _mapUiState.asStateFlow()

    fun placeOrRemoveMarker() {
        // hvis marker allerede er synlig, fjern den
        if (mapUiState.value.markerVisible) {
            _mapUiState.update { currentState ->
                currentState.copy(markerVisible = false)
            }
        } else {
            _mapUiState.update { currentState ->
                currentState.copy(
                    markerVisible = true
                )
            }
        }
    }
}
