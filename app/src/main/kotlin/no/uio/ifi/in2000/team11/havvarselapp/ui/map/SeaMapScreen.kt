package no.uio.ifi.in2000.team11.havvarselapp.ui.map

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
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.UrlTileProvider
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.TileOverlay
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import no.uio.ifi.in2000.team11.havvarselapp.R
import no.uio.ifi.in2000.team11.havvarselapp.data.location.LocationRepository
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
    val hideOverlayButton = remember { mutableStateOf(true) }

    // her trengs 'context' for å kunne hente utseende av kartet
    val context = LocalContext.current

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(mapUiState.currentLocation, 12f)
    }

    Box(modifier = Modifier.fillMaxSize()) {

        // selve kartet
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            onMapClick = { clickedPosition ->
                seaMapViewModel.placeOrRemoveMarker(clickedPosition)
            },
            properties = MapProperties(
                // dette er utseende av kartet, som man finner i filen "mapstyle" i raw-mappen
                mapStyleOptions = MapStyleOptions.loadRawResourceStyle(context, R.raw.mapstyle),
            )
        ) {
            // kartlag fra OpenSeaMap
            if (hideOverlayButton.value) {
                TileOverlay(
                    tileProvider = object : UrlTileProvider(256, 256) {
                        override fun getTileUrl(x: Int, y: Int, z: Int): URL {
                            return URL("https://t1.openseamap.org/seamark/$z/$x/$y.png")
                        }
                    }
                )
            }
            // pin som plasseres på kartet der brukeren trykker
            if (mapUiState.markerVisible) {
                Marker(
                    state = rememberMarkerState(position = mapUiState.currentLocation),
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
                                seaMapViewModel.getPosition(textState, context,
                                    cameraPositionState)
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
                            seaMapViewModel.getPosition(textState, context, cameraPositionState)
                        }
                        keyboardController?.hide()
                        focusManager.clearFocus(true)
                    }
                )
            )
        }
        // Knapp for å aktivere/deaktivere TileOverlay
        Button(
            onClick = { hideOverlayButton.value = !hideOverlayButton.value },
            modifier = Modifier
                .padding(start = 2.dp)
                .align(Alignment.BottomStart),
            colors =  ButtonDefaults.buttonColors(
                containerColor = Color(0xFF_13_23_2C)
            )
        ) {
            Text(text = if (hideOverlayButton.value) "Deaktiver Overlay" else "Aktiver Overlay")
        }
    }
}
