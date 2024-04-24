package no.uio.ifi.in2000.team11.havvarselapp.ui.map

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.maps.android.compose.CameraPositionState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException


class AutocompleteTextFieldActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun AutocompleteTextField(
        context: Context,
        updateLocation: (loc: LatLng) -> Unit,
        cameraPositionState: CameraPositionState, 
        placesClient: PlacesClient,
        active: MutableState<Boolean>
    ) {
        val historyItems = remember {
            mutableStateListOf(
                "Oslo",
                "Bergen",
                "Drammen",
                "Trondheim",
                "Tromsø"
            )
        }
        var predictions by rememberSaveable { mutableStateOf(emptyList<AutocompletePrediction>()) }
        var text by rememberSaveable { mutableStateOf("") }
        // var active by rememberSaveable { mutableStateOf(false) }
        //val keyboardController = LocalSoftwareKeyboardController.current

        Column(
            modifier = Modifier.fillMaxSize().zIndex(2f),
            horizontalAlignment = Alignment.CenterHorizontally
        )
        {
            DockedSearchBar(
                modifier = Modifier.padding(15.dp),
                shape = RoundedCornerShape(30.dp),
                query = text,
                onQueryChange = {
                    text = it
                    // Fetch predictions when text changes
                    fetchPredictions(it, placesClient) { fetchedPredictions ->
                        predictions = fetchedPredictions
                    }
                },
                onSearch = {
                    if (text.length > 0){
                        val inputTextUpperCase = text.replaceFirstChar { it.uppercase() }
                        getPosition(
                            inputTextUpperCase,
                            context,
                            updateLocation,
                            cameraPositionState
                        )
                        if (historyItems.contains(inputTextUpperCase)) {
                            val indexToRemove = historyItems.indexOf(inputTextUpperCase)
                            historyItems.removeAt(indexToRemove)
                        } else {
                            historyItems.removeAt(4)
                        }
                        historyItems.add(0, inputTextUpperCase)
                    }
                    active.value = false },
                active = active.value,
                onActiveChange = { active.value = it },
                placeholder = { Text("Søk her") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search Icon"
                    )
                },
                trailingIcon = {
                    if (active.value) {
                        Icon(
                            modifier = Modifier.clickable {
                                if (text.isNotEmpty()) {
                                    text = ""
                                    predictions = emptyList() //clears up predictions list
                                } else {
                                    active.value = false
                                }
                            },
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close Icon"
                        )
                    }
                },
                shadowElevation = 10.dp,
                colors = SearchBarDefaults.colors(
                    containerColor = Color(0xFF_D9_D9_D9).copy(alpha = 0.96f),

                    )
            ) {
                if (predictions.isEmpty()) {
                    historyItems.forEach { historyItem ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(all = 12.dp)
                                .clickable(onClick = {
                                    active.value = false
                                    text = historyItem
                                    getPosition(
                                        historyItem,
                                        context,
                                        updateLocation,
                                        cameraPositionState
                                    )
                                    if (historyItems.contains(historyItem)) {
                                        val indexToRemove = historyItems.indexOf(historyItem)
                                        historyItems.removeAt(indexToRemove)
                                    } else {
                                        historyItems.removeAt(4)
                                    }
                                    historyItems.add(0, historyItem)
                                })
                        ) {
                            Icon(
                                modifier = Modifier.padding(end = 10.dp),
                                imageVector = Icons.Default.History,
                                contentDescription = "History Icon"
                            )
                            Text(historyItem)
                        }
                    }
                } else {
                    predictions.forEach { prediction ->
                        val predictionText = prediction.getPrimaryText(null).toString()
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(all = 12.dp)
                                .clickable(onClick = {
                                    active.value = false
                                    text = predictionText
                                    getPosition(
                                        predictionText,
                                        context,
                                        updateLocation,
                                        cameraPositionState
                                    )
                                    // save chosen to the history list
                                    if (historyItems.contains(predictionText)) {
                                        // just move existing history item on the top of the list
                                        val indexToRemove = historyItems.indexOf(predictionText)
                                        historyItems.removeAt(indexToRemove)
                                    } else {
                                        // item does not exist, remove last item in the list and add new one to the top (FIFO)
                                        historyItems.removeAt(4) // remove last element
                                    }
                                    historyItems.add(0, predictionText)
                                })
                        ) {
                            Text(
                                modifier = Modifier.padding(start = 5.dp),
                                text = prediction.getPrimaryText(null).toString()
                            )
                        }
                    }
                }
            }
        }
    }


    fun fetchPredictions(
        query: String,
        placesClient: PlacesClient,
        onPredictionsFetched: (List<AutocompletePrediction>) -> Unit
    ) {
        // Use Places API to fetch predictions
        val token = AutocompleteSessionToken.newInstance()

        CoroutineScope(Dispatchers.IO).launch {
            val request = FindAutocompletePredictionsRequest.builder()
                .setQuery(query)
                .setSessionToken(token)
                .build()

            placesClient.findAutocompletePredictions(request).addOnSuccessListener { response ->
                val predictions = response.autocompletePredictions
                // Update predictions state
                onPredictionsFetched(predictions)
            }.addOnFailureListener { exception ->
                println(exception)
            }
        }
    }


    /**
     * Flytter kartet til området som brukeren har søkt opp.
     * Det kommer en pop-up melding hvis posisjonen ikke ble funnet.
     */
    fun getPosition(
        placeName: String,
        context: Context,
        updateLocation: (loc: LatLng) -> Unit,
        cameraPositionState: CameraPositionState
    ) {
        val geocoder = Geocoder(context)

        try {
            // Sjekk tilgjengeligheten av nettverkstilgang
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

            // Hent informasjon om nettverkstilkoblingen
            val networkCapabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)

            // Sjekk om enheten har en aktiv internettforbindelse via Wi-Fi eller mobilnett (CELLULAR for mobilnett)
            if (networkCapabilities != null && (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || networkCapabilities.hasTransport(
                    NetworkCapabilities.TRANSPORT_CELLULAR
                ))
            ) {

                // henter posisjonen til stedet som er søkt på
                val addressList: List<Address>? = geocoder.getFromLocationName(placeName, 1)
                if (!addressList.isNullOrEmpty()) {
                    val address: Address = addressList[0]
                    val lat = address.latitude
                    val long = address.longitude
                    val searchLocation = LatLng(lat, long)

                    // oppdater posisjon i UiState, som deretter oppdaterer locationRepository
                    updateLocation(searchLocation)

                    // flytter kartet til stedet som er søkt opp
                    cameraPositionState.position =
                        CameraPosition.fromLatLngZoom(searchLocation, 12f)
                } else {
                    // viser en "toast", en liten pop-up melding om at stedet ikke ble funnet
                    Toast.makeText(context, "Posisjonen ble ikke funnet", Toast.LENGTH_SHORT).show()
                }
            } else {
                // Hvis det ikke er noen aktiv internettforbindelse, vis en passende melding
                Toast.makeText(context, "Ingen internettforbindelse", Toast.LENGTH_SHORT).show()
            }
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}
