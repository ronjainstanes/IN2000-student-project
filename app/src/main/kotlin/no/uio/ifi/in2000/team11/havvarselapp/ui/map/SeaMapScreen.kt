package no.uio.ifi.in2000.team11.havvarselapp.ui.map

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
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
import no.uio.ifi.in2000.team11.havvarselapp.ui.harbors.GoogleMarkersGuestBlue
import no.uio.ifi.in2000.team11.havvarselapp.ui.harbors.GoogleMarkersGuestRed
import no.uio.ifi.in2000.team11.havvarselapp.ui.metalert.MetAlertsDialog
import no.uio.ifi.in2000.team11.havvarselapp.ui.navigation.NavigationBarWithButtons
import no.uio.ifi.in2000.team11.havvarselapp.ui.networkConnection.ConnectivityObserver
import no.uio.ifi.in2000.team11.havvarselapp.ui.networkConnection.NetworkConnectionStatus
import java.net.URL

@Composable
fun SeaMapScreen(
    sharedUiState: SharedUiState,
    navController: NavController,
    placesClient: PlacesClient,
    updateLocation: (loc: LatLng) -> Unit,
    connectivityObserver: ConnectivityObserver,
    seaMapViewModel: SeaMapViewModel = viewModel()
) {
    val autocompleteTextFieldActivity = AutocompleteTextFieldActivity()
    val mapUiState: MapUiState by seaMapViewModel.mapUiState.collectAsState()
    val showSymbols = rememberSaveable { mutableStateOf(true) }
    var showMetAlerts by rememberSaveable { mutableStateOf(false) }
    val showHarborWithGas = rememberSaveable { mutableStateOf(true) }
    val showHarborWithoutGas = rememberSaveable { mutableStateOf(true) }
    var showExplanation by rememberSaveable { mutableStateOf(false) }
    /*var showNetworkWarning by rememberSaveable { mutableStateOf(false) }*/
    val listOfSymbols: List<SeaSymbolsPair> = SeaSymbolsList().symbolDescription
    val context = LocalContext.current

    val showDialog = rememberSaveable { mutableStateOf(false) } // when true, filter will show up
    val activateSearch = rememberSaveable { mutableStateOf(false) } // when true, show searchbar

    // camera position, the area of map that is currently shown on screen
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(sharedUiState.currentLocation, 12f)
    }

    // get data about guest harbors from a JSON file
    seaMapViewModel.fetchHarborData(context)

    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {

        Column(modifier = Modifier.fillMaxSize()) {

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {

                // composable of the map itself
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState,
                    // when map is clicked
                    onMapClick = { clickedPosition ->
                        updateLocation(clickedPosition)
                        seaMapViewModel.placeOrRemoveMarker()
                        showDialog.value = false
                        activateSearch.value = false
                    },
                    // stops a google map button to pop up in the corner at random times
                    uiSettings = MapUiSettings(
                        mapToolbarEnabled = false
                    ),
                    properties = MapProperties(
                        // The appearance of the map, from the file 'mapstyle'
                        mapStyleOptions = MapStyleOptions.loadRawResourceStyle(
                            context,
                            R.raw.mapstyle
                        ),
                    )
                ) {
                    // Showing the guest harbors if zoom is small enough
                    if(cameraPositionState.position.zoom > 10.5f) {
                        // the two types of harbor markers
                        seaMapViewModel.harborData.value?.map { harbor ->
                            if (harbor.description.contains("Drivstoff") && showHarborWithGas.value) {
                                GoogleMarkersGuestRed(harbor = harbor, visible = true)
                            } else if (!harbor.description.contains("Drivstoff") && showHarborWithoutGas.value) {
                                GoogleMarkersGuestBlue(harbor = harbor, visible = true)
                            }
                        }
                    }


                    // map overlay from OpenSeaMap
                    if (showSymbols.value) {
                        TileOverlay(
                            tileProvider = tileProvider
                        )
                    }

                    // places a location-marker on the map where the user clicks, or hides the marker
                    if (mapUiState.markerVisible) {
                        Marker(
                            state = rememberMarkerState(position = sharedUiState.currentLocation),
                            icon = BitmapDescriptorFactory
                                .defaultMarker(BitmapDescriptorFactory.HUE_AZURE)
                        )
                    }
                }

                // Hide the dialog when the camera position changes
                LaunchedEffect(cameraPositionState.position) {
                    showDialog.value = false
                    activateSearch.value = false
                }

                // the search bar, with an autocomplete drop-down menu
                autocompleteTextFieldActivity.AutocompleteTextField(
                    context,
                    updateLocation,
                    cameraPositionState,
                    placesClient,
                    activateSearch
                )

                // button that opens a dialog that explains maritime symbols on the map
                Button(
                    onClick = { showExplanation = true },
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(start = 16.dp, top = 100.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF_13_23_2C)
                    )
                ) {
                    Text(text = "?", fontSize = (15.sp))
                }

                // the dialog with maritime symbols
                if (showExplanation) {
                    ExplanationBox(
                        symbolDescription = listOfSymbols,
                        onDismiss = { showExplanation = false }
                    )
                }

                // button for active met alerts at this location
                Button(
                    onClick = { showMetAlerts = true },
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(start = 316.dp, top = 100.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF_13_23_2C)
                    )
                ) {
                    Text(text = "Varsler")
                }

                // if clicked on, show met-alert dialog
                if (showMetAlerts) {
                    MetAlertsDialog(
                        sharedUiState = sharedUiState,
                        onDismiss = { showMetAlerts = false }
                    )
                }

                // button to activate/deactivate tile overlay
                FilterButtonAndDialog(
                    showSymbols,
                    showHarborWithGas,
                    showHarborWithoutGas,
                    showDialog
                )
            }
            // the bottom navigation between screens
            NavigationBarWithButtons(navController = navController)
        }
        NetworkConnectionStatus(connectivityObserver)
    }
}


/**
 * The dialog to explain maritime symbols on the map,
 * which are visible with OpenSeaMap tile overlay
 */
@Composable
fun ExplanationBox(
    symbolDescription: List<SeaSymbolsPair>,
    onDismiss: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .zIndex(2f)
            .heightIn(max = 700.dp)
            .padding(16.dp)
            .background(Color.White, shape = RoundedCornerShape(8.dp))
            .shadow(1.dp, shape = RoundedCornerShape(8.dp))

    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth()

        ) {

            // the title in the dialog
            Text(
                text = "Symbolforklaringer",
                style = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            // a scrollable window with symbols and explanations
            LazyColumn(modifier = Modifier.weight(1f).padding(16.dp)) {
                items(symbolDescription) { seaSymbolsPair ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {

                        // maritime symbol image
                        Image(
                            painter = painterResource(id = seaSymbolsPair.symbol),
                            contentDescription = "Bilde av ${seaSymbolsPair.description}",
                            modifier = Modifier.size(36.dp)
                        )

                        // desciption for each image
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(seaSymbolsPair.description)
                    }
                }
            }

            // button to close the dialog
            Button(
                onClick = onDismiss,
                modifier = Modifier.align(Alignment.CenterHorizontally),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF_13_23_2C)
                )
            ) {
                Text("Skjønner")
            }
        }
    }
}



/**
 * Provides the tile overlay at the
 * area that is currently visible on screen
 */
val tileProvider = object : UrlTileProvider(256, 256) {
    override fun getTileUrl(x: Int, y: Int, z: Int): URL {
        return URL("https://t1.openseamap.org/seamark/$z/$x/$y.png")
    }
}

/**
 * A dialog bith buttons to hide/show guest harbor markers,
 * and map overlay with maritime symbols
 */
@Composable
fun FilterButtonAndDialog(
    showSymbols: MutableState<Boolean>,
    showHarborWithGas: MutableState<Boolean>,
    showHarborWithoutGas: MutableState<Boolean>,
    showDialog: MutableState<Boolean>
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.BottomStart)
    ) {

        // when filter-button is clicked, show dialog
        if (showDialog.value) {
            Box(
                modifier = Modifier
                    .width(320.dp)
                    .height(190.dp)
                    .padding(8.dp)
                    .shadow(10.dp) // shadow effect when clicked
                    .clip(RoundedCornerShape(8.dp)) // specify how round corners should be
                    .background(
                        // color of dialog, and transparancy (alpha)
                        color = Color(0xFF_D9_D9_D9).copy(alpha = 0.97f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column {

                    // title of the dialog
                    Text(
                        text = "Filtermeny:",
                        style = TextStyle(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(top = 5.dp)
                    )

                    // button to show/hide maritime symbols
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Checkbox(
                            checked = showSymbols.value,
                            onCheckedChange = { showSymbols.value = it }
                        )
                        ClickableText(
                            text = AnnotatedString("Maritime symboler"),
                            style = TextStyle(fontSize = 15.sp),
                            onClick = { showSymbols.value = !showSymbols.value }
                        )
                    }

                    // button to show/hide harbor markers
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = showHarborWithGas.value,
                            onCheckedChange = { showHarborWithGas.value = it }
                        )
                        ClickableText(
                            text = AnnotatedString("Gjestehavner med bensinstasjoner"),
                            style = TextStyle(fontSize = 15.sp),
                            onClick = { showHarborWithGas.value = !showHarborWithGas.value }
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = showHarborWithoutGas.value,
                            onCheckedChange = { showHarborWithoutGas.value = it }
                        )
                        ClickableText(
                            text = AnnotatedString("Gjestehavner uten bensinstasjoner"),
                            style = TextStyle(fontSize = 15.sp),
                            onClick = { showHarborWithoutGas.value = !showHarborWithoutGas.value }
                        )
                    }
                }
            }
        }

        // the button that opens or closes the filter-menu
        Button(
            onClick = { showDialog.value = !showDialog.value },
            modifier = Modifier
                .padding(start = 2.dp, bottom = 4.dp)
                .size(90.dp, 40.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF_13_23_2C)
            )
        ) {
            Text(text = "Filter", fontSize = (15.sp))
        }
    }
}