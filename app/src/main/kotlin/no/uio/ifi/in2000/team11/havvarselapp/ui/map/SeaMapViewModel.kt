package no.uio.ifi.in2000.team11.havvarselapp.ui.map

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.widget.Toast
import androidx.compose.runtime.MutableState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.team11.havvarselapp.data.location.LocationRepositoryImpl
import java.io.IOException

data class MapUiState(
    /** Den valgte posisjonen i appen, oppdateres hver gang brukeren velger en ny posisjon. */
    var currentLocation: LatLng = LatLng(59.9, 10.73),

    /** Lagrer info om hvor i kartet brukeren sist navigerte til (koordinater og zoom nivå) */
    val currentCameraPosition: CameraPosition = CameraPosition.fromLatLngZoom(currentLocation, 12f),

    /** Er det en synlig pin på kartet? */
    val markerVisible: Boolean = false
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
    private fun updateUiStateLocation(newLocation: LatLng) {
        viewModelScope.launch(Dispatchers.IO) {

            // oppdaterer repository med den nye posisjonen i appen
            locationRepository.setCurrentLocation(newLocation)

            _mapUiState.update { currentState ->
                currentState.copy(currentLocation = newLocation)
            }
        }
    }

    fun updateCameraPosition(newCameraPosition: CameraPosition) {
        _mapUiState.update { currentState ->
            currentState.copy(currentCameraPosition = newCameraPosition)
        }
    }

    fun placeOrRemoveMarker(clickedPosition: LatLng) {
        // hvis marker allerede er synlig, fjern den
        if (mapUiState.value.markerVisible) {
            _mapUiState.update { currentState ->
                currentState.copy(markerVisible = false)
            }
        } else {
            _mapUiState.update { currentState ->
                currentState.copy(
                    markerVisible = true,
                    currentLocation = clickedPosition
                )
            }
        }
    }

    /**
     * Flytter kartet til området som brukeren har søkt opp.
     * Det kommer en pop-up melding hvis posisjonen ikke ble funnet.
     */
    fun getPosition(
        placeName: MutableState<String>,
        context: Context,
        cameraPositionState: CameraPositionState
    ){
        val locationName = placeName.value
        val geocoder = Geocoder(context)

        try {
            // Sjekk tilgjengeligheten av nettverkstilgang
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

            // Hent informasjon om nettverkstilkoblingen
            val networkCapabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)

            // Sjekk om enheten har en aktiv internettforbindelse via Wi-Fi eller mobilnett (CELLULAR for mobilnett)
            if (networkCapabilities != null && (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || networkCapabilities.hasTransport(
                    NetworkCapabilities.TRANSPORT_CELLULAR))) {

                // henter posisjonen til stedet som er søkt på
                val addressList: List<Address>? = geocoder.getFromLocationName(locationName, 1)
                if (!addressList.isNullOrEmpty()) {
                    val address: Address = addressList[0]
                    val lat = address.latitude
                    val long = address.longitude
                    val searchLocation = LatLng(lat, long)

                    // oppdater posisjon i UiState, som deretter oppdaterer locationRepository
                    updateUiStateLocation(searchLocation)

                    // flytter kartet til stedet som er søkt opp
                    cameraPositionState.position = CameraPosition.fromLatLngZoom(searchLocation, 12f)
                } else {
                    // viser en "toast", en liten pop-up melding om at stedet ikke ble funnet
                    Toast.makeText(context, "Location not found", Toast.LENGTH_SHORT).show()
                }
            } else {
                // Hvis det ikke er noen aktiv internettforbindelse, vis en passende melding
                Toast.makeText(context, "No internet connection available", Toast.LENGTH_SHORT).show()
            }
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}