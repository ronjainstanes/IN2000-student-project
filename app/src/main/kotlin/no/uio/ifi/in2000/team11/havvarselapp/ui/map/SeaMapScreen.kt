package no.uio.ifi.in2000.team11.havvarselapp.ui.map

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.UrlTileProvider
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.TileOverlay
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import no.uio.ifi.in2000.team11.havvarselapp.R
import no.uio.ifi.in2000.team11.havvarselapp.data.location.LocationRepository
import java.io.IOException
import java.net.URL


@Composable
fun SeaMapScreen(
    locationRepository: LocationRepository,
    region: String,
    seaMapViewModel: SeaMapViewModel = viewModel()
) {

    // observerer UiState fra ViewModel
    val mapUiState: MapUiState by seaMapViewModel.mapUiState.collectAsState()

    // lagrer tekst som skrives i søkefelt
    val textState = remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val hideSymbolsButton = remember { mutableStateOf(true) }

    // her trengs 'context' for å kunne hente utseende av kartet
    val context = LocalContext.current

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(mapUiState.currentLocation, 12f)
    }

    // brukes for å plassere pin på kartet
    val markerVisible = remember { mutableStateOf(false) }
    val markerPosition = remember { mutableStateOf(LatLng(59.9, 10.73)) }

    Box(modifier = Modifier.fillMaxSize()) {

        // selve kartet
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            onMapClick = { clickedPosition ->
                markerPosition.value = clickedPosition
                markerVisible.value = true
            },
            properties = MapProperties(
                // dette er utseende av kartet, som man finner i filen "mapstyle" i raw-mappen
                mapStyleOptions = MapStyleOptions.loadRawResourceStyle(context, R.raw.mapstyle),
            )
        ) {
            if (hideSymbolsButton.value) {
                TileOverlay(
                    tileProvider = object : UrlTileProvider(256, 256) {
                        override fun getTileUrl(x: Int, y: Int, z: Int): URL {
                            return URL("https://t1.openseamap.org/seamark/$z/$x/$y.png")
                        }
                    }
                )
            }
            // pin som plasseres på kartet der brukeren trykker
            if (markerVisible.value) {
                Marker(
                    state = rememberMarkerState(position = markerPosition.value),
                    visible = markerVisible.value,
                    icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)
                )
            }

        }
            // TextField with the value from textState and an event handler to update textState
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OutlinedTextField(
                    modifier = Modifier.padding(15.dp),
                    shape = RoundedCornerShape(50.dp),
                    value = textState.value,
                    onValueChange = { newText ->
                        textState.value = newText
                    },

                    // tekst og ikon som vises i søkefeltet
                    label = { Text("Søk her") },
                    maxLines = 1,

                    leadingIcon = {
                        Icon(
                            Icons.Default.Search, contentDescription = "Search Icon",
                            modifier = Modifier.clickable {
                                if (textState.value.isNotBlank()) {
                                    getPosition(textState, context,
                                        cameraPositionState, seaMapViewModel, mapUiState)
                                }
                                keyboardController?.hide()
                                focusManager.clearFocus(true)
                            }
                        ) },
                    //colors for search field
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFF_D9_D9_D9),
                        unfocusedContainerColor = Color(0xFF_D9_D9_D9).copy(alpha = 0.9f),
                        focusedBorderColor = Color(0xFF_D9_D9_D9),
                        unfocusedBorderColor = Color(0xFF_D9_D9_D9)
                    ),
                    // closing Text field after pressing enter
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done
                    ),
                    // søker opp området når brukeren trykker på søk-knappen
                    keyboardActions = KeyboardActions(
                    onDone = {
                        if (textState.value.isNotBlank()) {
                            getPosition(textState, context, cameraPositionState,
                                seaMapViewModel, mapUiState)
                        }
                        keyboardController?.hide()
                        focusManager.clearFocus(true)
                    }
                )
            )
        }
        // Knapp for å aktivere/deaktivere TileOverlay
        Button(
            onClick = { hideSymbolsButton.value = !hideSymbolsButton.value },
            modifier = Modifier
                .padding(start = 2.dp)
                .align(Alignment.BottomStart),
            colors =  ButtonDefaults.buttonColors(
                containerColor = Color(0xFF_13_23_2C)
            )
        ) {
            Text(text = if (hideSymbolsButton.value) "Deaktiver Overlay" else "Aktiver Overlay")
        }
    }
}

fun getPosition(
    placeName: MutableState<String>,
    context: Context,
    cameraPositionState: CameraPositionState,
    seaMapViewModel: SeaMapViewModel,
    mapUiState: MapUiState
){
    val locationName = placeName.value
    val geocoder = Geocoder(context)

    try {
        // Sjekk tilgjengeligheten av nettverkstilgang
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        // Sjekk om enheten kjører på Android Marshmallow (API 23) eller nyere
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Hent informasjon om nettverkstilkoblingen
            val networkCapabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)

            // Sjekk om enheten har en aktiv internettforbindelse via Wi-Fi eller mobilnett (CELLULAR for mobilnett)
            if (networkCapabilities != null && (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR))) {

                // henter posisjonen til stedet som er søkt på
                val addressList: List<Address>? = geocoder.getFromLocationName(locationName, 1)
                if (!addressList.isNullOrEmpty()) {
                    val address: Address = addressList[0]
                    val lat = address.latitude
                    val long = address.longitude
                    val searchLocation = LatLng(lat, long)

                    // oppdater posisjon i UiState, som deretter oppdaterer locationRepository
                    seaMapViewModel.updateUiStateLocation(searchLocation)

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
        }
    } catch (e: IOException) {
        e.printStackTrace()
        Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
    }
}
