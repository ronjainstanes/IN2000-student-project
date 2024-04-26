package no.uio.ifi.in2000.team11.havvarselapp.ui.harbors

import androidx.compose.runtime.Composable
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberMarkerState
import no.uio.ifi.in2000.team11.havvarselapp.R
import no.uio.ifi.in2000.team11.havvarselapp.model.harbor.Harbor

@Composable
fun GoogleMarkersGuestBlue(harbor: Harbor, visible: Boolean) {

    val icon = BitmapDescriptorFactory.fromResource(R.drawable.without_gas_harbor)

    Marker(
        state = rememberMarkerState(position = LatLng(harbor.location!![0],harbor.location[1])),
        title = harbor.name,
        snippet = harbor.description,
        icon = icon,
        visible = visible,
    )
}

@Composable
fun GoogleMarkersGuestRed(harbor: Harbor, visible: Boolean) {

    val icon = BitmapDescriptorFactory.fromResource(R.drawable.with_gas_harbor)

    Marker(
        state = rememberMarkerState(position = LatLng(harbor.location!![0],harbor.location[1])),
        title = harbor.name,
        snippet = harbor.description,
        icon = icon,
        visible = visible,
    )
}
