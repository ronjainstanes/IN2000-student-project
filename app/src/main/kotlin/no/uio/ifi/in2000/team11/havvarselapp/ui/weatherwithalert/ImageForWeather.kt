package no.uio.ifi.in2000.team11.havvarselapp.ui.weatherwithalert

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import no.uio.ifi.in2000.team11.havvarselapp.R

@Composable
fun ValidImageWeather(){
    val imageVector = ImageVector.vectorResource(id = R.drawable.p1honsftvsnih1nss1kofsciqo4_page_01)
    Image(imageVector = imageVector, contentDescription = "image",
        Modifier
            .size(100.dp)
            .padding(20.dp))
}