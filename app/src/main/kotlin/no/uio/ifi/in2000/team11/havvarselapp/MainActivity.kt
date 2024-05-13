package no.uio.ifi.in2000.team11.havvarselapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import no.uio.ifi.in2000.team11.havvarselapp.BuildConfig.MAPS_API_KEY
import no.uio.ifi.in2000.team11.havvarselapp.ui.navigation.SetUpNavigation
import no.uio.ifi.in2000.team11.havvarselapp.ui.networkConnection.ConnectivityObserver
import no.uio.ifi.in2000.team11.havvarselapp.ui.networkConnection.NetworkConnectivityObserver
import no.uio.ifi.in2000.team11.havvarselapp.ui.theme.HavvarselAppTheme

class MainActivity : ComponentActivity() {
    // Is needed to know if the app has internet access
    private lateinit var connectivityObserver: ConnectivityObserver

    // Create places client, needed to get the location that the user searches/navigates to
    private lateinit var placesClient: PlacesClient


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Used to get location name or coordinates when the user seaches or navigates
        Places.initialize(applicationContext, MAPS_API_KEY)
        placesClient = Places.createClient(this)

        // Observes when the app loses or gains internet access
        connectivityObserver = NetworkConnectivityObserver(applicationContext)

        setContent {
            HavvarselAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Sets up navigation graph, and opens the SeaMapScreen as start screen
                    SetUpNavigation(placesClient = placesClient, connectivityObserver)
                }
            }
        }
    }
}
