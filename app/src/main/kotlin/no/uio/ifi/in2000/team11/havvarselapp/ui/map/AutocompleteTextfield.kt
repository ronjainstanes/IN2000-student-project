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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
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
import no.uio.ifi.in2000.team11.havvarselapp.SharedUiState
import java.io.IOException

/**
 * NOTE: this file contains a warning - deprecated. Scroll down for explanation.
 */

/**
 * A search field to search for a location, which moves the map
 * and updates the weather-screen with the new location
 */
class AutocompleteTextFieldActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun AutocompleteTextField(
        context: Context,
        updateLocation: (loc: LatLng) -> Unit,
        cameraPositionState: CameraPositionState,
        placesClient: PlacesClient,
        active: MutableState<Boolean>,
        enableSearch: MutableState<Boolean>,
        sharedUiState: SharedUiState,
        updateSearchHistory: (userInput: String) -> Unit,
    ) {
        var predictions by rememberSaveable { mutableStateOf(emptyList<AutocompletePrediction>()) }
        val text = remember { mutableStateOf("") }

        // Vis søkehistorikken ved oppstart
        val keyboardController = LocalSoftwareKeyboardController.current
        val focusManager = LocalFocusManager.current

        Column(
            modifier = Modifier.fillMaxSize().zIndex(2f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            DockedSearchBar(
                modifier = Modifier.padding(15.dp),
                shape = RoundedCornerShape(30.dp),
                query = text.value,
                onQueryChange = {
                    text.value = it
                    // Fetch predictions when text changes
                    fetchPredictions(it, placesClient) { fetchedPredictions ->
                        predictions = fetchedPredictions
                    }
                },
                //if enableSearch.value is true, user will be able to use search
                enabled = enableSearch.value,
                onSearch = {
                    // Close keyboard if enableSearch.value is true
                    if (enableSearch.value) {
                        focusManager.clearFocus()
                        keyboardController?.hide()
                    }

                    if (text.value.isNotEmpty()) {
                        val inputTextUpperCase = text.value.replaceFirstChar { it.uppercase() }
                        getPosition(
                            inputTextUpperCase,
                            context,
                            updateLocation,
                            cameraPositionState
                        )
                        updateSearchHistory(inputTextUpperCase)
                    }
                    active.value = false

                },
                active = active.value && enableSearch.value,
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
                                if (text.value.isNotEmpty()) {
                                    text.value = ""
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
                    containerColor = Color(0xFF_D9_D9_D9).copy(alpha = 0.96f)
                )
            ) {
                if (predictions.isEmpty()) {
                    sharedUiState.historyItems.forEach { historyItem ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(all = 12.dp)
                                .clickable(onClick = {
                                    active.value = false
                                    text.value = historyItem
                                    getPosition(
                                        historyItem,
                                        context,
                                        updateLocation,
                                        cameraPositionState
                                    )
                                    updateSearchHistory(historyItem)
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
                                    text.value = predictionText
                                    getPosition(
                                        predictionText,
                                        context,
                                        updateLocation,
                                        cameraPositionState
                                    )
                                    updateSearchHistory(predictionText)
                                })
                        ) {
                            Text(
                                modifier = Modifier.padding(start = 5.dp),
                                text = predictionText
                            )
                        }
                    }
                }
            }
        }
    }



    /**
     * When the user starts typing a location in the search field,
     * this method fetches predictions to be displayed in an autocomplete
     * drop-down menu
     */
    private fun fetchPredictions(
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
     * After the user has searched a location in the search field,
     * the camera will move there.
     * A pop-up message will appear if the location is not found.
     *
     * NOTE: "getFromLocationName" is marked deprecated at API level 33,
     * however the new non-deprecated method has minimum API level 33.
     * We found no other to use this without setting minSdk to 33 and maxSdk to 34,
     * which is a very short API range, and we have chosen not to do this.
     */
    private fun getPosition(
        placeName: String,
        context: Context,
        updateLocation: (loc: LatLng) -> Unit,
        cameraPositionState: CameraPositionState
    ) {
        val geocoder = Geocoder(context)

        try {
            // Used to check internet connection
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

            // Get information about internet connection
            val networkCapabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)

            // Check if the device has an active internet connection via Wi-Fi or cellular network
            if (networkCapabilities != null && (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || networkCapabilities.hasTransport(
                    NetworkCapabilities.TRANSPORT_CELLULAR
                ))
            ) {

                // Get the location that the user searched for
                val addressList: List<Address>? = geocoder.getFromLocationName(placeName, 1)
                if (!addressList.isNullOrEmpty()) {
                    val address: Address = addressList[0]
                    val lat = address.latitude
                    val long = address.longitude
                    val searchLocation = LatLng(lat, long)

                    // Updates location in UiState
                    updateLocation(searchLocation)

                    // Moves the camera
                    cameraPositionState.position =
                        CameraPosition.fromLatLngZoom(searchLocation, 12f)
                } else {
                    // Shows a "toast", a pop-up message if the location was not found
                    Toast.makeText(context, "Posisjonen ble ikke funnet", Toast.LENGTH_SHORT).show()
                }
            } else {
                // If there is no internet connection, a message pops up
                Toast.makeText(context, "Ingen internettforbindelse", Toast.LENGTH_SHORT).show()
            }
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}
