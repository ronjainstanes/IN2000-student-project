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

    /**
    * List of all searched items in the search bar
     */
    val historyItems: List<String> = listOf("Oslo", "Bergen", "Drammen", "Trondheim", "Tromsø"),
)

/**
 * The shared ViewModel handles the SharedUiState, which all screens in the app
 * have access to. The ViewModel itself is not sent into the screens because each screen
 * has only one ViewModel.
 */
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

    /**
     * Updates the history items list
     * It takes name "String" of the location user have searched for
     * and puts it on the first index of the list and removes the last one (first in, last out - FILO)
     * If the item that already in list is being searched for it will move up to the first index
     * and no elements will be deleted
     */
    fun updateHistoryItems(userInput: String) {
        _sharedUiState.update { currentState ->
            val updatedHistoryItems = mutableListOf<String>().apply {
                add(userInput)
                addAll(currentState.historyItems.filter { it != userInput }.take(4))
            }
            currentState.copy(historyItems = updatedHistoryItems)
        }
    }

}