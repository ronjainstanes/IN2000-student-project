package no.uio.ifi.in2000.team11.havvarselapp.ui.map

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.UrlTileProvider
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.TileOverlay
import com.google.maps.android.compose.rememberCameraPositionState
import no.uio.ifi.in2000.team11.havvarselapp.R
import no.uio.ifi.in2000.team11.havvarselapp.model.seaSymbols.SeaSymbolsPair
import java.io.IOException
import java.net.URL


@Composable
@Preview(showBackground = true)
fun SeaMap() {
    val textState = remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val hideSymbolsButton = remember { mutableStateOf(true) }
    var showExplanation by remember { mutableStateOf(false) }
    //Listen over symbolene
    val listOfSymbols = listOf(
        SeaSymbolsPair(R.drawable.east_cardinal,"Øst kardinal" ),
        SeaSymbolsPair(R.drawable.west_cardinal,"Vest kardinal" ),
        SeaSymbolsPair(R.drawable.north_cardinal,"Nord kardinal" ),
        SeaSymbolsPair(R.drawable.south_cardinal,"Sør kardinal" ),
        SeaSymbolsPair(R.drawable.isolatedcardinal,"Isolert kardinal" ),
        SeaSymbolsPair(R.drawable.lateral_safewater,"Lateral senterledsmerke" ),
        SeaSymbolsPair(R.drawable.port_mark,"Portmerke" ),
        SeaSymbolsPair(R.drawable.preferred_channel_port,"Foretrukket kanal babord" ),
        SeaSymbolsPair(R.drawable.preferred_channel_starboard,"Foretrukket kanal styrbord" ),
        SeaSymbolsPair(R.drawable.special_mark,"Spesielt merke" ),
        SeaSymbolsPair(R.drawable.starboardmark,"Styrbord merke" ),
        SeaSymbolsPair(R.drawable.red_beacon,"Rødt signallys" ),
        SeaSymbolsPair(R.drawable.yellow_beacon,"Gult signallys" ),
        SeaSymbolsPair(R.drawable.green_beacon,"Grønt signallys" ),
        SeaSymbolsPair(R.drawable.harbour,"Havn" ),
        SeaSymbolsPair(R.drawable.anchorage,"Ankerplass" ),
        SeaSymbolsPair(R.drawable.fishing_harbour,"Fiskehavn" ),
        SeaSymbolsPair(R.drawable.marina,"Marina" ),
        SeaSymbolsPair(R.drawable.breakwater,"Molo" ),
        SeaSymbolsPair(R.drawable.pier,"Brygge" ),
        SeaSymbolsPair(R.drawable.crane,"Kran" ),
        SeaSymbolsPair(R.drawable.slipway,"Slipp" ),
        SeaSymbolsPair(R.drawable.harbour_master,"Havnemester" ),
        SeaSymbolsPair(R.drawable.waste_disposal,"Avfallshåndtering" ))

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
fun ExplanationBox(symbolDescription: List<SeaSymbolsPair>, onDismiss:() -> Unit){
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

