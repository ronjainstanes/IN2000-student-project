package no.uio.ifi.in2000.team11.havvarselapp.ui.map

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class MapUiState(
    /**
     * Den valgte posisjonen i appen, endres hver
     * gang brukeren velger en ny posisjon.
     */
    val currentLocation: LatLng = LatLng(59.9, 10.73),

    /**
     * Lagrer info om hvor i kartet brukeren sist
     * navigerte til (koordinater og zoom nivå)
     */
    val cameraPositionState: CameraPosition = CameraPosition.fromLatLngZoom(currentLocation, 12f)
)

class SeaMapViewModel: ViewModel() {
    private val _mapUiState = MutableStateFlow(MapUiState())
    val mapUiState: StateFlow<MapUiState> = _mapUiState.asStateFlow()

    init {
        Log.w("SEA_MAP_VIEW_MODEL", "Init må ha en kropp :)") //TODO endre senere
    }
}