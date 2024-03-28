package no.uio.ifi.in2000.team11.havvarselapp.ui.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.team11.havvarselapp.data.location.LocationRepositoryImpl

data class MapUiState(
    /**
     * Den valgte posisjonen i appen, oppdateres hver
     * gang brukeren velger en ny posisjon.
     */
    var currentLocation: LatLng = LatLng(59.9, 10.73),

    /**
     * Lagrer info om hvor i kartet brukeren sist
     * navigerte til (koordinater og zoom nivå)
     *TODO er det riktig å definere det her? Blir det mye å bytte
     * ut UiState hver gang brukeren navigerer rundt i kartet?
     * For det er jo fint at man kommer tilbake til samme plass i kartet når man
     * navigerer mellom skjermene i appen, bør det ikke lagres i UiState da?
     */
    val cameraPositionState: CameraPosition = CameraPosition.fromLatLngZoom(currentLocation, 12f)
)

class SeaMapViewModel: ViewModel() {
    // et repository som lagrer posisjonsdata som alle skjermer skal ha tilgang til
    private val locationRepository: LocationRepositoryImpl = LocationRepositoryImpl()

    // oppretter en privat state flow, denne er mutable og kan derfor endres på
    private val _mapUiState = MutableStateFlow(MapUiState())

    // oppretter en immutable/uforanderlig UiState av typen StateFlow
    val mapUiState: StateFlow<MapUiState> = _mapUiState.asStateFlow()

    /**
     * Metode som oppdaterer UiState med den nye posisjonen i appen
     */
    fun updateUiStateLocation(newLocation: LatLng) {
        viewModelScope.launch(Dispatchers.IO) {

            // oppdaterer repository med den nye posisjonen i appen
            locationRepository.setCurrentLocation(newLocation)

            _mapUiState.update { currentLocationUiState ->
                currentLocationUiState.copy(currentLocation = newLocation)
            }
        }
    }
}