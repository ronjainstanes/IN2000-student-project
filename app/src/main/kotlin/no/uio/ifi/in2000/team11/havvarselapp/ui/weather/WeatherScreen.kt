package no.uio.ifi.in2000.team11.havvarselapp.ui.weather

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import no.uio.ifi.in2000.team11.havvarselapp.R
import no.uio.ifi.in2000.team11.havvarselapp.ui.LocationForecast.LocationForecastViewModel
import no.uio.ifi.in2000.team11.havvarselapp.ui.metalert.CurrentLocationAlert

enum class DisplayInfo {
    Weather, Sea
}

@SuppressLint("DiscouragedApi")
@RequiresApi(Build.VERSION_CODES.O)
@Preview
@Composable
fun WeatherScreen(forecastViewModel: LocationForecastViewModel = viewModel()){
    var displayInfo by remember { mutableStateOf(DisplayInfo.Sea) }
    LaunchedEffect(key1 = Unit) {
        forecastViewModel.loadForecast("59.9", "10.7")
    }


    // IKON VÆR_SKJERM
    val ikonTemp = ImageVector.vectorResource(id = R.drawable.p1honsftvsnih1nss1kofsciqo4_page_165)
    val klokke = ImageVector.vectorResource(id = R.drawable.clock)
    val veerikon = ImageVector.vectorResource(id = R.drawable.weather1)
    val vind1 = ImageVector.vectorResource(id = R.drawable.vind1)
    val uv1 = ImageVector.vectorResource(id = R.drawable.uv1)
    // + disse brukes ikke atm
    val vind0 = ImageVector.vectorResource(id = R.drawable.p1honsftvsnih1nss1kofsciqo4_page_156)
    val uv2 = ImageVector.vectorResource(id = R.drawable.p1honsftvsnih1nss1kofsciqo4_page_194)
    val vind2 = ImageVector.vectorResource(id = R.drawable.vind2)
    val hovedbildet = ImageVector.vectorResource(id = R.drawable.p1honsftvsnih1nss1kofsciqo4_page_01)


    // IKON til knappene: vær-skjerm og hav-skjerm
    val buttonOcean = ImageVector.vectorResource(id = R.drawable.buttonocean)
    val buttonWeather = ImageVector.vectorResource(id = R.drawable.buttonweather)


    // IKON  HAV-SKJERM
    val vannTemp = ImageVector.vectorResource(id = R.drawable.watertemp2)
    val wave = ImageVector.vectorResource(id = R.drawable.wave)
    val current = ImageVector.vectorResource(id = R.drawable.current2)
    val pilMot = ImageVector.vectorResource(id = R.drawable.pil)
    val imageVectorOcean = ImageVector.vectorResource(id = R.drawable.oceanikon)


    // FARGENE TIL TABELLEN
    val radColor1: Color = Color(235, 238, 255, 255) // annenhver farge i tabell radene
    val radColor2: Color = Color(218, 222, 255, 255) // annenhver farge i tabell radene
    val headerColor: Color = Color(161, 170, 219, 255) // Farge til header i tabellen

    // ULIKE FONTER LASTET NED FRA NETT
    val PoppinsLight = FontFamily( Font(R.font.poppins_light, FontWeight.W400))
    val NatoSansJP = FontFamily( Font(R.font.notosansjp_variablefont_wght, FontWeight.W400))
    val PoppinsExtralight = FontFamily( Font(R.font.poppins_extralight, FontWeight.W400))
    val PoppinsRegular = FontFamily( Font(R.font.poppins_regular, FontWeight.W400))
    val ojujuRegular = FontFamily( Font(R.font.ojuju_regular, FontWeight.W400))
    val ojujumedium = FontFamily( Font(R.font.ojuju_medium, FontWeight.W400))
    val ojuju = FontFamily( Font(R.font.ojuju_variablefont_wght, FontWeight.W400))
    val truculentaRegular = FontFamily( Font(R.font.truculenta_regular, FontWeight.W400))
    val truculentaLight = FontFamily( Font(R.font.truculenta_semiexpanded_light, FontWeight.W400))
    val truculentaMedium = FontFamily( Font(R.font.truculenta_semiexpanded_medium, FontWeight.W400))
    val truculenta3 = FontFamily( Font(R.font.truculenta_semiexpanded_regular, FontWeight.W400))
    val truculenta4 = FontFamily( Font(R.font.truculenta_60pt_semicondensed_regular, FontWeight.W400))

    val fonts = arrayOf(PoppinsLight, NatoSansJP, PoppinsExtralight, PoppinsRegular, ojujuRegular, ojujumedium, ojuju, truculentaRegular, truculentaLight, truculentaMedium, truculenta3, truculenta4 )
    // FOR Å ENDRE FONT PÅ ALT ENDRE BARE DENNE VARIABELEN !!
    val fontBrukt = fonts[8] // fra 0 - 11


    Column(modifier = Modifier.fillMaxWidth().padding(2.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween )
    {

        // Området for data på skjermen
        Text(
            text = "Oslo", textAlign = TextAlign.Center,
            fontSize = 35.sp,
            fontFamily = fonts[1],
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier.padding(top = 12.dp) )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically )
        {
            Spacer(modifier = Modifier.weight(1f))

            // FORSIDE BILDET
            Column (
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally )
            {
                when (displayInfo) {

                    // Forside bildet er NÅVÆRENDE VÆR på VÆR-SKJERM
                    DisplayInfo.Weather -> {
                        val ikonName = forecastViewModel.getWeatherIcon(0) ?: "fair_day"
                        val context = LocalContext.current
                        val resId = context.resources.getIdentifier(ikonName, "drawable", context.packageName)
                        val weatherIkon: ImageVector = if (resId != 0) {
                            ImageVector.vectorResource(id = resId)
                        } else {
                            ImageVector.vectorResource(id = R.drawable.fair_day) }

                        Image(imageVector = weatherIkon, contentDescription = "image",
                            Modifier
                                .size(145.dp)
                                .padding(top = 13.dp, bottom = 20.dp))
                    }

                    // Forside bildet er hardkodet på HAV-SKJERM
                    DisplayInfo.Sea -> {
                        Image(imageVector = imageVectorOcean, contentDescription = "image",
                            Modifier
                                .size(145.dp)
                                .padding(top = 13.dp, bottom = 20.dp))

                    }
                }

            }
                // Knapper for å bytte mellom skjerm
            Row (
                modifier = Modifier.weight(1f).padding(top = 80.dp),
                horizontalArrangement = Arrangement.End )
            {
                IconButton(
                    onClick = { displayInfo = DisplayInfo.Weather },
                    modifier = Modifier.size(55.dp).padding(2.dp) ) {
                    Image( imageVector = buttonWeather, contentDescription = "Weather" ) }
                IconButton(
                    onClick = { displayInfo = DisplayInfo.Sea },
                    modifier = Modifier.size(55.dp).padding(2.dp)) {
                    Image( imageVector = buttonOcean, contentDescription = "Sea" ) }
            }

    }

        // KORT med vær tabellen
        Card(modifier = Modifier
            .padding(start = 8.dp, end = 8.dp)
            .border(width = 1.dp, color = radColor2, shape = RoundedCornerShape(8.dp))
            .fillMaxWidth() )
        {
                                                                                 // WEATHER SCREEN
            when (displayInfo) {
                DisplayInfo.Weather -> {
                    // RAD MED IKON ØVERST
                    Row(
                        modifier = Modifier.fillMaxWidth().background(headerColor).padding(top = 7.dp, bottom = 5.dp),
                        horizontalArrangement = Arrangement.Center, )
                    {

                        // Klokke ikon
                        Column( modifier = Modifier.weight(1f).wrapContentSize() ) {
                            Image(
                                imageVector = klokke, contentDescription = "image",
                                Modifier.size(45.dp).padding(top = 5.dp) ) }

                        // Temnperatur ikon
                        Column( modifier = Modifier.weight(1f).wrapContentSize() ) {
                            Image(
                                imageVector = ikonTemp, contentDescription = "image",
                                Modifier.size(50.dp).padding(2.dp) ) }

                        // Vær ikon
                        Column( modifier = Modifier.weight(1f).wrapContentSize() ) {
                            Image(
                                imageVector = veerikon, contentDescription = "image",
                                Modifier.size(55.dp)) }

                        // Vind ikon
                        Column( modifier = Modifier.weight(1f).wrapContentSize() ) {
                            Image(
                                imageVector = vind1, contentDescription = "image",
                                Modifier.size(50.dp).padding(2.dp) ) }

                        // UV ikon
                        Column( modifier = Modifier.weight(1f).wrapContentSize() ) {
                            Image(
                                imageVector = uv1, contentDescription = "image",
                                Modifier.size(45.dp).padding(top = 5.dp) ) }
                    }
                    // RAD MED IKON FOR VÆR-SKJERM SLUTT

                    // LASTER INN RADER MED VÆR-INFO
                    if (forecastViewModel.forecastInfo_UiState.collectAsState().value != null) {
                        var farge: Boolean = true
                        for (i in 0..4) {
                            farge = if (farge) {
                                WeatherRow(forecastViewModel, i, radColor1, fontBrukt)
                                false
                            } else {
                                WeatherRow(forecastViewModel, i, radColor2, fontBrukt)
                                true }
                        }
                    }
                }
                // VÆR-SKJERM SLUTT



                                                                                    // OCEAN-SCREEN
                DisplayInfo.Sea -> {
                    // RAD MED IKON ØVERST Oceanforecast
                    Row(modifier = Modifier.fillMaxWidth().background(headerColor).padding(top = 7.dp, bottom = 5.dp),
                        horizontalArrangement = Arrangement.Center, )
                    {

                        // Klokke ikon
                        Column( modifier = Modifier.weight(1f).wrapContentSize() ) {
                            Image(
                                imageVector = klokke, contentDescription = "image",
                                Modifier.size(45.dp).padding(top = 5.dp) ) }

                        // Vann Temnperatur ikon
                        Column( modifier = Modifier.weight(1f).wrapContentSize() ) {
                            Image(
                                imageVector = vannTemp, contentDescription = "image",
                                Modifier.size(52.dp).padding(2.dp) ) }

                        // Bølge høyde ikon
                        Column( modifier = Modifier.weight(1f).wrapContentSize() ) {
                            Image(
                                imageVector = wave, contentDescription = "image",
                                Modifier.size(52.dp)) }

                        // Current ikon
                        Column( modifier = Modifier.weight(1f).wrapContentSize() ) {
                            Image(
                                imageVector = current, contentDescription = "image",
                                Modifier.size(50.dp).padding(2.dp) ) }

                        // Pil mot
                        Column( modifier = Modifier.weight(1f).wrapContentSize() ) {
                            Image(
                                imageVector = pilMot, contentDescription = "image",
                                Modifier.size(40.dp).padding(top = 15.dp) ) }
                    }
                    // RAD MED IKON FOR HAV_SKJERM SLUTT

                    // LASTER INN RADENE MED HAV-INFO
                    if (forecastViewModel.OceanForecast_UiState.collectAsState().value != null) {
                        var farge: Boolean = true
                        // ALLE RADENE med Hav-info
                        for (i in 0..4) {
                            farge = if (farge) {
                                OceanRow(forecastViewModel, i, radColor1, fontBrukt)
                                false
                            } else {
                                OceanRow(forecastViewModel, i, radColor2, fontBrukt)
                                true }
                        }
                    }
                }
                // HAV-SKJERM SLUTT
            }

        }
        CurrentLocationAlert(
            region = "oslo",
            TextStyle(
                fontSize = 23.sp,
                fontFamily = fonts[0],
                fontWeight = FontWeight.ExtraBold
            )
        )
    }
}




// Metoden for å laste inn alle radene med værinfo
@SuppressLint("DiscouragedApi")
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun WeatherRow(forecastViewModel: LocationForecastViewModel, time: Int, rowColor: Color, font: FontFamily) {
    val ikonName = forecastViewModel.getWeatherIcon(time) ?: "fair_day"
    val context = LocalContext.current
    val resId = context.resources.getIdentifier(ikonName, "drawable", context.packageName)

    val weatherIkon: ImageVector = if (resId != 0) {
        ImageVector.vectorResource(id = resId)
    } else {
        ImageVector.vectorResource(id = R.drawable.fair_day) }

    val borderColor: Color = Color(134, 145, 205, 255)

    // Rad x
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(55.dp)
            .background(rowColor),
        horizontalArrangement = Arrangement.Center )
    {// .border(width = 0.5.dp, color = borderColor, shape = RoundedCornerShape(2.dp))
        // tidspunkt
        Column(modifier = Modifier
            .weight(1f)
            .wrapContentSize()
            .padding(top = 15.dp) ) {
            Text(
                text = forecastViewModel.getNorwegianTimeAndMin(time),
                modifier = Modifier.weight(1f),
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp,
                fontFamily = font ) }

        // Temnperatur
        Column(modifier = Modifier
            .weight(1f)
            .wrapContentSize()
            .padding(top = 15.dp) ) {
            Text(
                text = forecastViewModel.getTemperature(time),
                modifier = Modifier.weight(1f),
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                fontFamily = font ) }

        // Ikon for været
        Column(modifier = Modifier
            .weight(1f)
            .wrapContentSize()
            .padding(top = 15.dp) ) {
            Image(
                imageVector = weatherIkon, contentDescription = "image",
                Modifier.size(40.dp)) }

        // Vind-speed
        Column(modifier = Modifier
            .weight(1f)
            .wrapContentSize()
            .padding(top = 15.dp) ) {
            Text(
                text = forecastViewModel.getWindSpeed(time),
                modifier = Modifier.weight(1f),
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                fontFamily = font ) }

        // UV index
        Column(modifier = Modifier
            .weight(1f)
            .wrapContentSize()
            .padding(top = 15.dp) ) {
            Text(
                text = "${forecastViewModel.getUVindex(time)}",
                modifier = Modifier.weight(1f),
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                fontFamily = font ) }
    }
}



// Metoden for å laste inn alle radene med hav-info
@SuppressLint("DiscouragedApi")
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun OceanRow(forecastViewModel: LocationForecastViewModel, time: Int, rowColor: Color, font: FontFamily) {
    val borderColor: Color = Color(134, 145, 205, 255)
    val pil = ImageVector.vectorResource(id = R.drawable.pilned)

    // Rad x
    Row(
        modifier = Modifier.fillMaxWidth().height(55.dp).background(rowColor),
        horizontalArrangement = Arrangement.Center )
    {
        // tidspunkt
        Column(modifier = Modifier.weight(1f).wrapContentSize().padding(top = 15.dp) ) {
            Text(
                text = forecastViewModel.getNorwegianTimeAndMinOcean(time),
                modifier = Modifier.weight(1f),
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp,
                fontFamily = font ) }

        // Vann Temnperatur
        Column(modifier = Modifier.weight(1f).wrapContentSize().padding(top = 15.dp) ) {
            Text(
                text = forecastViewModel.getSeaWaterTemperature(time),
                modifier = Modifier.weight(1f),
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                fontFamily = font ) }

        // Bølge høyde
        Column(modifier = Modifier.weight(1f).wrapContentSize().padding(top = 15.dp) ) {
            Text(
                text = forecastViewModel.getSeaWaveHeight(time),
                modifier = Modifier.weight(1f),
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                fontFamily = font ) }


        // Current from
        Column(modifier = Modifier.weight(1f).wrapContentSize() ) {
            Text(
                text = forecastViewModel.getCurrentDirectionFrom(time),
                modifier = Modifier.weight(0.75f),
                fontWeight = FontWeight.ExtraBold,
                fontSize = 11.sp,
                fontFamily = font )
            Image(
                imageVector = pil, contentDescription = "image",
                Modifier.size(12.dp).align(Alignment.CenterHorizontally).weight(0.5f).padding(top = 3.dp))
            Text(
                text = forecastViewModel.getCurrentDirectionTowards(time) + "",
                modifier = Modifier.weight(0.95f).padding(bottom = 0.5.dp),
                fontWeight = FontWeight.ExtraBold,
                fontSize = 11.sp,
                fontFamily = font )
        }


        // Current speed
        Column(modifier = Modifier.weight(1f).wrapContentSize().padding(top = 18.dp) ) {
            Text(
                text = forecastViewModel.getCurrentSpeed(time),
                modifier = Modifier.weight(1f),
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                fontFamily = font ) }
    }

}




