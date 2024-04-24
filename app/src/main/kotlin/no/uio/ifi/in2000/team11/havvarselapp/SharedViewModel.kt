package no.uio.ifi.in2000.team11.havvarselapp

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

data class SharedUiState(
    /**
     * The current location, shared between all screens
     */
    var currentLocation: LatLng = LatLng(59.9, 10.73),

    /**
     * List of all met-alerts at the current location
     */
    val allMetAlerts: List<MetAlert> = listOf(),
)

class SharedViewModel: ViewModel() {
    // Create instance of repository
    private val metAlertsRepository = MetAlertsRepositoryImpl()

    // private UiState, that ViewModel can modify
    private val _sharedUiState = MutableStateFlow(SharedUiState())

    // public UiState that all screens have access to, cannot be modified
    val sharedUiState: StateFlow<SharedUiState> = _sharedUiState.asStateFlow()

    init {
        loadMetAlerts()
    }

    /**
     * Updates the current location, for the whole app
     */
    fun updateLocation(newLocation: LatLng) {
        _sharedUiState.update { currentState ->
            currentState.copy(currentLocation = newLocation)
        }
        loadMetAlerts()
    }

    private fun loadMetAlerts() {
        // Launches a dispatch thread to fetch data
        viewModelScope.launch(Dispatchers.IO) {

            // Updates UiState with list of MetAlerts
            _sharedUiState.update { currentState ->
                val metAlerts = metAlertsRepository.getMetAlertsAtLocation(sharedUiState.value.currentLocation)

                // Return a new UiState with MetAlerts and replace the old UiState
                currentState.copy(allMetAlerts = metAlerts)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        println("ViewModel cleared")
    }
}