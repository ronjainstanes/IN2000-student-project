package no.uio.ifi.in2000.team11.havvarselapp.ui.map

import android.content.Context
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
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.maps.android.compose.CameraPositionState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class AutocompleteTextFieldActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun AutocompleteTextField(seaMapViewModel: SeaMapViewModel, context: Context, cameraPositionState: CameraPositionState, placesClient: PlacesClient) {
        var historyItems = remember {
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
        var active by rememberSaveable { mutableStateOf(false) }

        Column(
            modifier = Modifier.fillMaxSize(),
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
                onSearch = {active = false},
                active = active,
                onActiveChange = { active = it },
                placeholder = {Text("Søk her")},
                leadingIcon = {
                         Icon(
                             imageVector = Icons.Default.Search,
                             contentDescription = "Search Icon"
                         )},
                trailingIcon = {
                    if (active){
                        Icon(
                            modifier = Modifier.clickable {
                                if(text.isNotEmpty()){
                                    text = ""
                                    predictions = emptyList() //clears up predictions list
                                } else {
                                    active = false
                                } },
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close Icon"
                        )
                    }
                },
                shadowElevation = 10.dp,
                colors = SearchBarDefaults.colors(
                    containerColor = Color(0xFF_D9_D9_D9).copy(alpha = 0.95f),

                )
            ) {
                if(predictions.isEmpty()){
                    historyItems.forEach{historyItem ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(all = 12.dp)
                                .clickable(onClick = {
                                    active = false
                                    text = historyItem
                                    seaMapViewModel.getPosition(
                                        historyItem,
                                        context,
                                        cameraPositionState
                                    )
                                    if (historyItems.contains(historyItem)) {
                                        val indexToRemove = historyItems.indexOf(historyItem)
                                        historyItems.removeAt(indexToRemove)
                                    } else {
                                        historyItems.removeAt(4)
                                    }
                                    historyItems.add(0, historyItem)
                                })){
                            Icon(modifier = Modifier.padding(end = 10.dp),
                                imageVector = Icons.Default.History,
                                contentDescription = "History Icon")
                            Text(historyItem)
                        }
                    }
                }else{
                    predictions.forEach { prediction ->
                        val predictionText = prediction.getPrimaryText(null).toString()
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(all = 12.dp)
                                .clickable(onClick = {
                                    active = false
                                    text = predictionText
                                    seaMapViewModel.getPosition(
                                        predictionText,
                                        context,
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
                        ){
                            Text(modifier = Modifier.padding(start = 5.dp), text = prediction.getPrimaryText(null).toString())
                        }
                    }
                }
            }
        }
    }



    fun fetchPredictions(query: String, placesClient: PlacesClient, onPredictionsFetched: (List<AutocompletePrediction>) -> Unit) {
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
}
