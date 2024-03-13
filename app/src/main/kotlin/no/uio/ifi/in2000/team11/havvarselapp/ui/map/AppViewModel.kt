package no.uio.ifi.in2000.team11.havvarselapp.ui.map

import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * UI state for the home screen.
 * @param ? TODO ingen parametre?
 */
data class AppUiState(
    val currentLocation: LatLng = LatLng(59.9, 10.73),
)

/**
 * View model for appen.
 */
class AppViewModel : ViewModel() {

    // Create instance of repository

    // Private mutable state flow to represent the UI state.
    private val _appUiState = MutableStateFlow(AppUiState())

    // Private mutable state flow to represent the UI state.
    val appUiState: StateFlow<AppUiState> = _appUiState.asStateFlow()
}