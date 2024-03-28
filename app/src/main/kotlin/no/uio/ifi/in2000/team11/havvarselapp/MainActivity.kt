package no.uio.ifi.in2000.team11.havvarselapp

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import no.uio.ifi.in2000.team11.havvarselapp.data.location.LocationRepository
import no.uio.ifi.in2000.team11.havvarselapp.data.location.LocationRepositoryImpl
import no.uio.ifi.in2000.team11.havvarselapp.ui.navigation.NavScreen
import no.uio.ifi.in2000.team11.havvarselapp.ui.theme.HavvarselAppTheme

class MainActivity : ComponentActivity() {

    // klient for å kunne hente posisjon
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    // repository for posisjons-data som alle skjermer trenger tilgang til
    private val locationRepository: LocationRepository = LocationRepositoryImpl()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // opprett en instans av LocationClient for å kunne hente brukerens posisjon
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // sjekker at brukeren har gitt tillatelse til å hente posisjon
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    // Got last known location. In some rare situations this can be null.
                }
        }


        setContent {
            HavvarselAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavScreen(locationRepository, "Oslo")
                }
            }
        }
    }
}