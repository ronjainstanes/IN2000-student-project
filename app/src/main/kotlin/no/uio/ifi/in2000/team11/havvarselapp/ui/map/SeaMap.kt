package no.uio.ifi.in2000.team11.havvarselapp.ui.map

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.tooling.preview.Preview
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import java.io.IOException


@Composable
@Preview(showBackground = true)
fun SeaMap() {
    /*
    val textState = remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val hideSymbolsButton = remember { mutableStateOf(true) }
    var showExplanation by remember { mutableStateOf(false) }
    val listOfSymbols : List<SeaSymbolsPair> = SeaSymbolsList().symbolDescription

    // her trengs 'context' for å kunne hente utseende av kartet
    val context = LocalContext.current

    // bruker koordinatene til Oslo som startposisjon
    val oslo = LatLng(59.9, 10.73)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(oslo, 12f)
    }
    Box(modifier = Modifier.fillMaxSize()) {
        // kart
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
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
        }
            // TextField with the value from textState and an event handler to update textState
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                OutlinedTextField(
                    modifier = Modifier.padding(15.dp),
                    shape = RoundedCornerShape(50.dp),
                    value = textState.value,
                    onValueChange = { newText ->
                        textState.value = newText
                    },
                    // Optional parameters to customize the TextField
                    label = { Text("Søk her") },
                    maxLines = 1,

                    leadingIcon = {
                        Icon(
                            Icons.Default.Search, contentDescription = "Search Icon",
                            modifier = Modifier.clickable {
                                if (textState.value.isNotBlank()) {
                                    getPosition(textState, context, cameraPositionState)
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
                    keyboardActions = KeyboardActions(
                    onDone = {
                        if (textState.value.isNotBlank()) {
                            getPosition(textState, context, cameraPositionState)
                        }
                        keyboardController?.hide()
                        focusManager.clearFocus(true)
                    }
                )
            )
        }
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
*/
}

fun getPosition(placeName: MutableState<String>, context: Context, cameraPositionState: CameraPositionState){
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
                val addressList: List<Address>? = geocoder.getFromLocationName(locationName, 1)
                if (!addressList.isNullOrEmpty()) {
                    val address: Address = addressList[0]
                    val latitude = address.latitude
                    val longitude = address.longitude
                    // TODO: remove or change text later
                    Toast.makeText(
                        context,
                        "Latitude: $latitude, Longitude: $longitude",
                        Toast.LENGTH_SHORT
                    ).show()
                    val searchLocation = LatLng(latitude, longitude)
                    cameraPositionState.position = CameraPosition.fromLatLngZoom(searchLocation, 12f)
                } else {
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

//Funksjonen som viser symbolforklaringene
@Composable
fun ExplanationBox(){
    /*
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
    */
}

