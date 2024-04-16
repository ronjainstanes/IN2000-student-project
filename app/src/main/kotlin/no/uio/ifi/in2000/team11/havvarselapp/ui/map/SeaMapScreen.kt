package no.uio.ifi.in2000.team11.havvarselapp.ui.map

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.UrlTileProvider
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.TileOverlay
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import no.uio.ifi.in2000.team11.havvarselapp.R
import java.net.URL

@Composable
fun SeaMapScreen(
    placesClient: PlacesClient,
    seaMapViewModel: SeaMapViewModel = viewModel()
) {
    val autocompleteTextFieldActivity = AutocompleteTextFieldActivity()
    // observerer UiState fra ViewModel
    val mapUiState: MapUiState by seaMapViewModel.mapUiState.collectAsState()

    val hideOverlayButton = rememberSaveable { mutableStateOf(true) }

    // her trengs 'context' for å kunne hente utseende av kartet
    val context = LocalContext.current

    // dette lagres lokalt, ikke i UiState for å unngå at skjermen blir
    // recomposed hvert sekund hvis man scroller rundt i kartet */
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
            uiSettings = MapUiSettings(
                mapToolbarEnabled = false,
                myLocationButtonEnabled = true
            ),
            properties = MapProperties(
                // dette er utseende av kartet, som man finner i filen "mapstyle" i raw-mappen
                mapStyleOptions = MapStyleOptions.loadRawResourceStyle(context, R.raw.mapstyle),
            )
        ) {
            // kartlag fra OpenSeaMap
            if (hideOverlayButton.value) {
                TileOverlay(
                    tileProvider = tileProvider
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
        autocompleteTextFieldActivity.AutocompleteTextField(
            seaMapViewModel,
            context,
            cameraPositionState,
            placesClient
        )

        // Knapp for å aktivere/deaktivere TileOverlay
        Button(
            onClick = { hideOverlayButton.value = !hideOverlayButton.value },
            modifier = Modifier
                .padding(start = 2.dp)
                .align(Alignment.BottomStart),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF_13_23_2C)
            )
        ) {
            Text(text = if (hideOverlayButton.value) "Deaktiver Overlay" else "Aktiver Overlay")
        }
    }
}

val tileProvider = object : UrlTileProvider(256, 256) {
    override fun getTileUrl(x: Int, y: Int, z: Int): URL {
        return URL("https://t1.openseamap.org/seamark/$z/$x/$y.png")
    }
}