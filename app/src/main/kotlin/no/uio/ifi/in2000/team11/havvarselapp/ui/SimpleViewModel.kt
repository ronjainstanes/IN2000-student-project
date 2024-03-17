package no.uio.ifi.in2000.team11.havvarselapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.team11.havvarselapp.data.alert.MetAlertsRepositoryImpl
import no.uio.ifi.in2000.team11.havvarselapp.model.alert.MetAlert

/**
 * UI State for alle skjermene i appen
 * TODO ingen parametre?
 */
data class AppUiState(
    /**
     * Den valgte posisjonen i appen, endres hver
     * gang brukeren velger en ny posisjon.
     */
    val currentLocation: LatLng = LatLng(59.9, 10.73),

    /**
     * Liste med alle farevarsler
     */
    val allMetAlerts: List<MetAlert> = listOf()
)

/**
 * View model for alle skjermer appen.
 */
class SimpleViewModel : ViewModel() {
    // Create instance of repository
    private val metAlertsRepository = MetAlertsRepositoryImpl()

    // UI State represented as a MutableStateFlow //TODO hvorfor gjør man det egt?
    private val _appUiState = MutableStateFlow(AppUiState())

    // Private mutable state flow to represent the UI state. //TODO hva skjer egt her?
    val appUiState: StateFlow<AppUiState> = _appUiState.asStateFlow()

    init {
        //TODO logg at dette kalles
        loadMetAlerts()
    }

    private fun loadMetAlerts() {
        // Launches a dispatch thread to fetch data
        viewModelScope.launch(Dispatchers.IO) {

            // Updates UiState with list of MetAlerts
            _appUiState.update { currentAppUiState ->
                //TODO logg at dette kalles
                val metAlerts = metAlertsRepository.getMetAlertsInNorway()

                // Return a new UiState with MetAlerts and replace the old UiState
                currentAppUiState.copy(allMetAlerts = metAlerts)
            }
        }
    }
}