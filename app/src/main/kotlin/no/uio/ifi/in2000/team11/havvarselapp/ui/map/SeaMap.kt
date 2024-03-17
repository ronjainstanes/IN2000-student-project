package no.uio.ifi.in2000.team11.havvarselapp.ui.map

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.UrlTileProvider
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.TileOverlay
import com.google.maps.android.compose.rememberCameraPositionState
import no.uio.ifi.in2000.team11.havvarselapp.R
import java.net.URL

@Composable
fun SeaMap() {

    // her trengs 'context' for å kunne hente utseende av kartet
    val context = LocalContext.current

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
        TileOverlay(
            tileProvider = object : UrlTileProvider(256, 256) {
                override fun getTileUrl(x: Int, y: Int, z: Int): URL {
                    return URL("https://t1.openseamap.org/seamark/$z/$x/$y.png")
                }
            }
        )
    }
}