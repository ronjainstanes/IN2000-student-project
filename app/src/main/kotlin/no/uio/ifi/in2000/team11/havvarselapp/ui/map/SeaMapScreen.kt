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
import no.uio.ifi.in2000.team11.havvarselapp.ui.harbors.CheckHarborColor
import no.uio.ifi.in2000.team11.havvarselapp.ui.navigation.NavigationBarWithButtons
import java.net.URL

@Composable
fun SeaMapScreen(
    sharedUiState: SharedUiState,
    navController: NavController,
    placesClient: PlacesClient,
    updateLocation: (loc: LatLng) -> Unit,
    seaMapViewModel: SeaMapViewModel = viewModel(),

) {
    val autocompleteTextFieldActivity = AutocompleteTextFieldActivity()
    val mapUiState: MapUiState by seaMapViewModel.mapUiState.collectAsState()
    val showSymbols = rememberSaveable { mutableStateOf(true) }
    val showHarborWithGas = rememberSaveable { mutableStateOf(true) }
    val showHarborWithoutGas = rememberSaveable { mutableStateOf(true) }
    var showExplanation by rememberSaveable { mutableStateOf(false) }
    val listOfSymbols : List<SeaSymbolsPair> = SeaSymbolsList().symbolDescription
    // her trengs 'context' for å kunne hente utseende av kartet
    val context = LocalContext.current


    val showDialog = rememberSaveable { mutableStateOf(false) } // when true filter will show up
    val activateSearch = rememberSaveable { mutableStateOf(false) } // when true show search "bar"


    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(sharedUiState.currentLocation, 12f)
    }


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
                    showDialog.value = false
                    activateSearch.value = false
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

                // Viser gjestehavner markører på karten basert på brukerens valg
                seaMapViewModel.harborData.value?.forEach { harbor ->
                    if (harbor.description.contains("Drivstoff") && showHarborWithGas.value) {
                        CheckHarborColor(harbor = harbor, visible = true)
                    } else if (!harbor.description.contains("Drivstoff") && showHarborWithoutGas.value) {
                        CheckHarborColor(harbor = harbor, visible = true)
                    }
                }


                // kartlag fra OpenSeaMap
                if (showSymbols.value) {
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

            LaunchedEffect(cameraPositionState.position) {
                // Hide the dialog when the camera position changes
                showDialog.value = false
                activateSearch.value = false
            }

            autocompleteTextFieldActivity.AutocompleteTextField(
                context,
                updateLocation,
                cameraPositionState,
                placesClient,
                activateSearch
            )


            // Knapp som forklarer maritime symboler på kartet
            Button(
                onClick = {showExplanation=true},
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(start =16.dp, top= 100.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF_13_23_2C)
                )
            ) {
                Text(text = "?", fontSize = (15.sp))
            }

            //Symbolforklaringen kalles på
            if (showExplanation) {
                ExplanationBox(
                    symbolDescription = listOfSymbols,
                    onDismiss = { showExplanation=false }
                )
            }
            // Knapp for å aktivere/deaktivere TileOverlay
            FilterButtonAndDialog(showSymbols, showHarborWithGas, showHarborWithoutGas, showDialog)
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
            .zIndex(2f)
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

val tileProvider = object : UrlTileProvider(256, 256) {
    override fun getTileUrl(x: Int, y: Int, z: Int): URL {
        return URL("https://t1.openseamap.org/seamark/$z/$x/$y.png")
    }
}


@Composable
fun FilterButtonAndDialog(showSymbols: MutableState<Boolean>,
                          showHarborWithGas: MutableState<Boolean>,
                          showHarborWithoutGas: MutableState<Boolean>,
                          showDialog: MutableState<Boolean>) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.BottomStart)
    ) {
        if (showDialog.value) {
            Box(
                modifier = Modifier
                    .width(320.dp)
                    .height(190.dp)
                    .padding(8.dp)
                    .shadow(10.dp) // Legg til skyggeeffekt
                    .clip(RoundedCornerShape(8.dp)) // Rund av hjørnene
                    .background(
                        color = Color(0xFF_D9_D9_D9).copy(alpha = 0.97f) // Bruk ønsket farge og gjennomsiktighet
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column() {
                    Text(text = "Filter menu:",
                        style = TextStyle(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold),
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(top = 5.dp)
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Checkbox(
                            checked = showSymbols.value,
                            onCheckedChange = { showSymbols.value = it  }
                        )
                        ClickableText(
                            text = AnnotatedString("Kartsymboler"),
                            style = TextStyle(fontSize = 15.sp),
                            onClick = { showSymbols.value = !showSymbols.value }
                        )
                    }

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




