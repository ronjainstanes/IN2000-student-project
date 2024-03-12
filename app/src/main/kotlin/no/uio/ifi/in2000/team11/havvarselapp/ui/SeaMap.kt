package no.uio.ifi.in2000.team11.havvarselapp.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.JointType
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.Polygon
import com.google.maps.android.compose.rememberCameraPositionState
import no.uio.ifi.in2000.team11.havvarselapp.R

@Composable
fun SeaMap() {

    // her trengs 'context' for å kunne hente utseende av kartet
    val context = LocalContext.current

    //TODO slett dette
    var polygonCoordinates: List<LatLng> = listOf(LatLng(6.08989,60.13),LatLng(5.77752,60.6673),LatLng(5.57731,60.9019),LatLng(5.67443,61.1222), LatLng(5.66474,61.4106), LatLng(4.69256,61.4607), LatLng(4.51089,61.0786), LatLng(4.73156,60.5526), LatLng(5.17784,59.5557), LatLng(5.55426,59.7626), LatLng(5.7202,59.8937), LatLng(6.08989,60.13), LatLng(6.08989,60.13))
    polygonCoordinates = polygonCoordinates.map { LatLng(it.longitude, it.latitude) }

    // bruker koordinatene til Oslo som startposisjon
    val oslo = LatLng(59.9, 10.73)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(oslo, 12f)
    }
    // kart
    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        properties = MapProperties(
            // dette er utseende av kartet, som man finner i filen "mapstyle" i raw-mappen
            mapStyleOptions = MapStyleOptions.loadRawResourceStyle(context, R.raw.mapstyle),

        )
    ) {
        // tegner opp et område i kartet //TODO slett dette
        Polygon(
            points = polygonCoordinates,
            visible = true,
            strokeColor = Color.Green,
            fillColor = Color.Transparent,
            strokeJointType = JointType.ROUND
        )
    }
}