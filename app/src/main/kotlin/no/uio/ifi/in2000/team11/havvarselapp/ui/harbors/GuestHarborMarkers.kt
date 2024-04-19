package no.uio.ifi.in2000.team11.havvarselapp.ui.harbors

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberMarkerState
import no.uio.ifi.in2000.team11.havvarselapp.R
import no.uio.ifi.in2000.team11.havvarselapp.model.harbor.Harbor

@SuppressLint("SuspiciousIndentation", "CheckResult")
@Composable
fun GoogleMarkersGuest(harbor: Harbor, visible: Boolean) {

    val icon = if(harbor.description.contains("Drivstoff")){
        BitmapDescriptorFactory.fromResource(R.drawable.with_gas_harbor)
    }
    else {
        BitmapDescriptorFactory.fromResource(R.drawable.without_gas_harbor)
    }



    Marker(
            state = rememberMarkerState(position = LatLng(harbor.location[0],harbor.location[1])),
            title = harbor.name,
            snippet = harbor.description,
            icon = icon,
            visible = visible,
/*
            onInfoWindowLongClick = {
                showMoreInformation(context, harbor = harbor)
            }   
*/
        )
}

/**
 * Det skal være funksjon for å vise ekstra innhold når bruker trykker på markøren
 */
/*fun showMoreInformation(context: Context, harbor: Harbor){
    Toast.makeText(context, "her er det en kjeeeeeeeeeeeeeeeeeeempe laaaaaaaaaaaaaaaaangt tekst!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!", Toast.LENGTH_LONG).show()
}*/
