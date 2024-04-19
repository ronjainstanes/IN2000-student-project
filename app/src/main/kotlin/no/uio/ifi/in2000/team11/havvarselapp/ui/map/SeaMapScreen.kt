package no.uio.ifi.in2000.team11.havvarselapp.ui.map

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
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
import no.uio.ifi.in2000.team11.havvarselapp.SharedUiState
import no.uio.ifi.in2000.team11.havvarselapp.model.seaSymbols.SeaSymbolsList
import no.uio.ifi.in2000.team11.havvarselapp.model.seaSymbols.SeaSymbolsPair
import no.uio.ifi.in2000.team11.havvarselapp.ui.harbors.GoogleMarkersGuest
import no.uio.ifi.in2000.team11.havvarselapp.ui.navigation.NavigationBarWithButtons
import java.net.URL

@Composable
fun SeaMapScreen(
    sharedUiState: SharedUiState,
    navController: NavController,
    placesClient: PlacesClient,
    updateLocation: (loc: LatLng) -> Unit,
    seaMapViewModel: SeaMapViewModel = viewModel()
) {
    val autocompleteTextFieldActivity = AutocompleteTextFieldActivity()
    val mapUiState: MapUiState by seaMapViewModel.mapUiState.collectAsState()
    val hideOverlayButton = rememberSaveable { mutableStateOf(true) }
    var showExplanation by rememberSaveable { mutableStateOf(false) }
    val listOfSymbols : List<SeaSymbolsPair> = SeaSymbolsList().symbolDescription
    // her trengs 'context' for å kunne hente utseende av kartet
    val context = LocalContext.current
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(sharedUiState.currentLocation, 12f)
    }
    // Variabel for å endre gjestehavnenes marører synlighet
    var visibleHarborMarker: Boolean

    // Henter data om gjestehavner fra JSON fil
    seaMapViewModel.fetchHarborData(context)
 Column (modifier = Modifier.fillMaxSize()){

     Box(modifier = Modifier.fillMaxSize().weight(1f)) {

        // selve kartet
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            onMapClick = { clickedPosition ->
                updateLocation(clickedPosition)
                seaMapViewModel.placeOrRemoveMarker()
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

            // Endrer markørenes synlighent
            visibleHarborMarker = if(cameraPositionState.position.zoom > 20f){
                false
            } else{
                true
            }
            // Viser gjestehavner markører på karten
            seaMapViewModel.harborData.value?.map { harbor -> GoogleMarkersGuest(harbor, visibleHarborMarker) }


            // kartlag fra OpenSeaMap
            if (hideOverlayButton.value) {
                TileOverlay(
                    tileProvider = tileProvider
                )
            }
            
            // pin som plasseres på kartet der brukeren trykker
            if (mapUiState.markerVisible) {
                Marker(
                    state = rememberMarkerState(position = sharedUiState.currentLocation),
                    icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)
                )
            }

         }
         autocompleteTextFieldActivity.AutocompleteTextField(
             context,
             updateLocation,
             cameraPositionState,
             placesClient
         )


         // Knapp som forklarer maritime symboler på kartet
         Button(
             onClick = {showExplanation=true},
             modifier = Modifier
                 .align(Alignment.TopStart)
                 .padding(start =16.dp, top= 100.dp)
         ) {
             Text(text = "?")
         }

         //Symbolforklaringen kalles på
         if (showExplanation) {
             ExplanationBox(
                 symbolDescription = listOfSymbols,
                 onDismiss = { showExplanation=false }
             )
         }

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
             Text(text = if (hideOverlayButton.value) "Deaktiver filter" else "Aktiver filter")
         }
     }
     NavigationBarWithButtons(navController = navController)
     }
}

//Funksjonen som viser symbolforklaringene
@Composable
fun ExplanationBox(
    symbolDescription: List<SeaSymbolsPair>,
    onDismiss:() -> Unit
){
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 700.dp)
            .padding(16.dp)
            .background(Color.White, shape = RoundedCornerShape(8.dp))
            .shadow(4.dp, shape = RoundedCornerShape(8.dp)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = "Symbolforklaringer",
                style = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(bottom = 16.dp)
            )
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(symbolDescription) { seaSymbolsPair ->
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
                        Image(
                            painter = painterResource(id = seaSymbolsPair.symbol),
                            contentDescription = "Bilde av ${seaSymbolsPair.description}",
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(seaSymbolsPair.description)
                    }
                }
            }
            Button(
                onClick = onDismiss,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("Skjønner")
            }
        }
    }
}

val tileProvider = object : UrlTileProvider(256, 256) {
    override fun getTileUrl(x: Int, y: Int, z: Int): URL {
        return URL("https://t1.openseamap.org/seamark/$z/$x/$y.png")
    }
}