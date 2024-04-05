package no.uio.ifi.in2000.team11.havvarselapp.ui.weather

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import no.uio.ifi.in2000.team11.havvarselapp.R
import no.uio.ifi.in2000.team11.havvarselapp.ui.locationForecast.LocationForecastViewModel
import no.uio.ifi.in2000.team11.havvarselapp.ui.metalert.CurrentLocationAlert

enum class DisplayInfo {
    Weather, Sea
}

enum class Expanded {
    Long, Short
}

/**
 * Skjerm med værinfo og farevarsler.
 * NB: Funksjonen har "SuppressLint" annotering fordi ikonnavn
 * og ID hentes dynamisk i stedet for statisk, noe som gir en warning.
 */
@SuppressLint("DiscouragedApi")
@Composable
fun WeatherScreen(forecastViewModel: LocationForecastViewModel = viewModel()){
    var displayInfo by remember { mutableStateOf(DisplayInfo.Weather) }
    var expanded by remember { mutableStateOf(Expanded.Short) }
    LaunchedEffect(key1 = Unit) {
        forecastViewModel.loadForecast("59.9", "10.7")
    }

    // IKON til knappene: vær-skjerm og hav-skjerm
    val buttonOcean = ImageVector.vectorResource(id = R.drawable.buttonocean)
    val buttonWeather = ImageVector.vectorResource(id = R.drawable.buttonweather)
    val imageVectorOcean = ImageVector.vectorResource(id = R.drawable.oceanicon)


    // FARGENE TIL TABELLEN
    val radColor1: Color = Color(235, 238, 255, 255) // annenhver farge i tabell radene
    val radColor2: Color = Color(218, 222, 255, 255) // annenhver farge i tabell radene
    val headerColor: Color = Color(120, 133, 191, 255) // Farge til header i tabellen
    val expandRowColor: Color = Color(73, 81, 129, 255) // Farge til header i tabellen
    val oldHeader2: Color = Color(117, 144, 203, 255) // Farge til header i tabellen

    val oldHeader: Color = Color( 161, 170, 219, 255) // Farge til header i tabellen


    // ULIKE FONTER LASTET NED FRA NETT
    val poppinsLight = FontFamily( Font(R.font.poppins_light, FontWeight.W400))
    val natoSansJP = FontFamily( Font(R.font.notosansjp_variablefont_wght, FontWeight.W400))
    val poppinsExtralight = FontFamily( Font(R.font.poppins_extralight, FontWeight.W400))
    val poppinsRegular = FontFamily( Font(R.font.poppins_regular, FontWeight.W400))
    val ojujuRegular = FontFamily( Font(R.font.ojuju_regular, FontWeight.W400))
    val ojujumedium = FontFamily( Font(R.font.ojuju_medium, FontWeight.W400))
    val ojuju = FontFamily( Font(R.font.ojuju_variablefont_wght, FontWeight.W400))
    val truculentaRegular = FontFamily( Font(R.font.truculenta_regular, FontWeight.W400))
    val truculentaLight = FontFamily( Font(R.font.truculenta_semiexpanded_light, FontWeight.W400))
    val truculentaMedium = FontFamily( Font(R.font.truculenta_semiexpanded_medium, FontWeight.W400))
    val truculenta3 = FontFamily( Font(R.font.truculenta_semiexpanded_regular, FontWeight.W400))
    val truculenta4 = FontFamily( Font(R.font.truculenta_60pt_semicondensed_regular, FontWeight.W400))

    val fonts = arrayOf(poppinsLight, natoSansJP, poppinsExtralight, poppinsRegular, ojujuRegular, ojujumedium, ojuju, truculentaRegular, truculentaLight, truculentaMedium, truculenta3, truculenta4 )
    // FOR Å ENDRE FONT PÅ ALT ENDRE BARE DENNE VARIABELEN !!
    val fontBrukt = fonts[8] // fra 0 - 11 8

    val pilopp = ImageVector.vectorResource(id = R.drawable.p1honsftvsnih1nss1kofsciqo4_page_145)
    val pilned = ImageVector.vectorResource(id = R.drawable.p1honsftvsnih1nss1kofsciqo4_page_146)



    @Composable
    fun shortToLongButton() {
        Row( modifier = Modifier
            .fillMaxWidth()
            .height(35.dp)
            .background(expandRowColor),
             horizontalArrangement = Arrangement.Center
        ) {
            when (expanded) {
                Expanded.Short -> {
                    Button(
                        onClick = { expanded = Expanded.Long },
                        modifier = Modifier.fillMaxSize(),
                        colors = ButtonDefaults.buttonColors(expandRowColor)
                    )
                    {
                        Text(
                            text = "Time for time ",
                            fontSize = 14.sp,
                            fontFamily = fonts[1],
                            fontWeight = FontWeight.ExtraBold )
                        Image(
                            imageVector = pilned,
                            contentDescription = "pil",
                            Modifier
                                .size(25.dp)
                                .padding(top = 0.dp) )
                    }
                }
                Expanded.Long -> {
                    Button(
                        onClick = { expanded = Expanded.Short },
                        modifier = Modifier.fillMaxSize(),
                        colors = ButtonDefaults.buttonColors(expandRowColor))
                    {
                        Text(
                            text = "Time for time ",
                            textAlign = TextAlign.Center,
                            fontSize = 14.sp,
                            fontFamily = fonts[1],
                            fontWeight = FontWeight.ExtraBold)
                        Image(
                            imageVector = pilopp,
                            contentDescription = "pil",
                            Modifier.size(25.dp) )
                    }

                }
            }
        }
    }


    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(2.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween )
    {

        // Området for data på skjermen
        Text(
            text = "Oslo", textAlign = TextAlign.Center,
            fontSize = 30.sp,
            fontFamily = fonts[1],
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier.padding(top = 8.dp) )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically )
        {
            if (forecastViewModel.forecastInfoUiState.collectAsState().value != null) {

                Column(modifier = Modifier.weight(1f)) {
                    Row( modifier = Modifier
                        .align(Alignment.Start)
                        .padding(start = 26.dp, bottom = 3.dp) ) {
                        Text(
                            text = "I dag "+ forecastViewModel.getNorwegianTime(0) + "-" +forecastViewModel.getNorwegianTime(1),
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 15.sp,
                            fontFamily = fontBrukt )

                    }

                    Row(
                        Modifier
                            .align(Alignment.Start)
                            .padding(start = 40.dp)  )
                    {

                        Text(
                            text = forecastViewModel.getTemperature(0),
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 25.sp,
                            fontFamily = fontBrukt )

                    }
                    Column( modifier = Modifier
                        .align(Alignment.Start)
                        .padding(start = 15.dp, top = 10.dp) ) {
                        when (displayInfo) {
                            DisplayInfo.Weather -> {
                                Text(
                                    text = "Last updated:\n" + forecastViewModel.locationForecastLastUpdated(),
                                    fontWeight = FontWeight.ExtraBold,
                                    lineHeight = 13.sp,
                                    fontSize = 11.sp,
                                    color = Color(115, 115, 115, 255),
                                    fontFamily = fontBrukt )
                            }
                            DisplayInfo.Sea -> {
                                Text(
                                    text = "Last updated:\n" + forecastViewModel.oceanForecastLastUpdated(),
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 11.sp,
                                    color = Color(115, 115, 115, 255),
                                    lineHeight = 13.sp,
                                    fontFamily = fontBrukt )
                            }
                        }

                    }

                }

                // FORSIDE BILDET
                Column (
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally )
                {
                    GetWeatherIcon(forecastViewModel = forecastViewModel)
                }
            }
                // Knapper for å bytte mellom skjerm
            Row (
                modifier = Modifier
                    .weight(1f)
                    .padding(top = 20.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.Bottom)
            {
                Column (
                    modifier = Modifier.align(Alignment.Bottom)


                ) {
                    Text(
                        text = "Vær",
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 13.sp,
                        fontFamily = fontBrukt )
                    IconButton(
                        onClick = { displayInfo = DisplayInfo.Weather },
                        modifier = Modifier
                            .size(55.dp)
                            .padding(1.dp) ) {
                        Image( imageVector = buttonWeather, contentDescription = "Weather" ) }
                }
                Column {
                    Text(
                        text = "Hav",
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 13.sp,
                        fontFamily = fontBrukt )
                    IconButton(
                        onClick = { displayInfo = DisplayInfo.Sea },
                        modifier = Modifier
                            .size(55.dp)
                            .padding(1.dp)) {
                        Image( imageVector = buttonOcean, contentDescription = "Sea" ) }

                }

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
                    WeatherHeader(headerColor = headerColor, font = fontBrukt )
                    // LASTER INN RADER MED VÆR-INFO
                    if (forecastViewModel.forecastInfoUiState.collectAsState().value != null) {
                        when (expanded) {

                            Expanded.Short -> {
                                var farge: Boolean = true
                                for (i in 0..1) {
                                    farge = if (farge) {
                                        WeatherRow(forecastViewModel, i, radColor1, fontBrukt)
                                        false
                                    } else {
                                        WeatherRow(forecastViewModel, i, radColor2, fontBrukt)
                                        true } }
                                shortToLongButton()
                            }

                            Expanded.Long -> {
                                var farge: Boolean = true
                                for (i in 0..8) {
                                    farge = if (farge) {
                                        WeatherRow(forecastViewModel, i, radColor1, fontBrukt)
                                        false
                                    } else {
                                        WeatherRow(forecastViewModel, i, radColor2, fontBrukt)
                                        true } }
                                shortToLongButton()
                            }
                        }
                    }
                }
                // VÆR-SKJERM SLUTT


                                                                                    // OCEAN-SCREEN
                DisplayInfo.Sea -> {
                    // RAD MED IKON ØVERST Oceanforecast
                    OceanHeader(headerColor = headerColor, font = fontBrukt )

                    // LASTER INN RADENE MED HAV-INFO
                    if (forecastViewModel.oceanForecastUiState.collectAsState().value != null) {
                        when (expanded) {

                            Expanded.Short -> {
                                var farge: Boolean = true
                                // ALLE RADENE med Hav-info
                                for (i in 0..1) {
                                    farge = if (farge) {
                                        OceanRow(forecastViewModel, i, radColor1, fontBrukt)
                                        false
                                    } else {
                                        OceanRow(forecastViewModel, i, radColor2, fontBrukt)
                                        true }
                                }
                                shortToLongButton()
                            }

                            Expanded.Long -> {
                                var farge: Boolean = true
                                // ALLE RADENE med Hav-info
                                for (i in 0..8) {
                                    farge = if (farge) {
                                        OceanRow(forecastViewModel, i, radColor1, fontBrukt)
                                        false
                                    } else {
                                        OceanRow(forecastViewModel, i, radColor2, fontBrukt)
                                        true } }
                                shortToLongButton()
                            }
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

/**
 * Metode for å laste inn alle radene med værinfo
 * NB: Funksjonen har "SuppressLint" annotering fordi ikonnavn
 * og ID hentes dynamisk i stedet for statisk, noe som gir en warning.
 */
@SuppressLint("DiscouragedApi")
@Composable
fun WeatherRow(forecastViewModel: LocationForecastViewModel, time: Int, rowColor: Color, font: FontFamily) {
    val iconName = forecastViewModel.getWeatherIcon(time) ?: "fair_day"
    val context = LocalContext.current
    val resId = context.resources.getIdentifier(iconName, "drawable", context.packageName)

    val weatherIcon: ImageVector = if (resId != 0) {
        ImageVector.vectorResource(id = resId)
    } else {
        ImageVector.vectorResource(id = R.drawable.fair_day) }

    val borderColor: Color = Color(134, 145, 205, 255)
    val north  = ImageVector.vectorResource(id = R.drawable.north)
    val south  = ImageVector.vectorResource(id = R.drawable.south)
    val west  = ImageVector.vectorResource(id = R.drawable.west)
    val east  = ImageVector.vectorResource(id = R.drawable.oest)
    val northWest  = ImageVector.vectorResource(id = R.drawable.northwest)
    val northEast  = ImageVector.vectorResource(id = R.drawable.northeast)
    val southWest  = ImageVector.vectorResource(id = R.drawable.southwest)
    val southEast  = ImageVector.vectorResource(id = R.drawable.southeast)

    val windDirecting = forecastViewModel.getWindDirection(time)
    val windIcon: ImageVector = when (windDirecting) {
        "Nord" -> north
        "Nord-øst" -> northEast
        "Øst" -> east
        "Sør-øst" -> southEast
        "Sør" -> south
        "Sør-vest" -> southWest
        "Vest" -> west
        "Nord-vest"-> northWest
        else -> north
    }

    // Rad x
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(45.dp)
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
                fontSize = 13.sp,
                fontFamily = font ) }

        // Temnperatur
        Column(modifier = Modifier
            .weight(0.9f)
            .wrapContentSize()
            .align(alignment = Alignment.CenterVertically)
            .padding(top = 14.dp) ) {
            Text(
                text = forecastViewModel.getTemperature(time),
                modifier = Modifier
                    .weight(1f)
                    .padding(top = 1.dp),
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                fontFamily = font ) }

        // Ikon for været
        Column(modifier = Modifier
            .weight(0.9f)
            .wrapContentSize()
            .padding(top = 5.dp) ) {
            Image(
                imageVector = weatherIcon, contentDescription = "image",
                Modifier.size(40.dp)) }

        // Rain / perciption amount
        Column(modifier = Modifier
            .weight(1.2f)
            .wrapContentSize()
            .padding(top = 15.dp) ) {
            Text(
                text = forecastViewModel.getPrecipitationAmountMaxMin(time),
                modifier = Modifier.weight(1f),
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                fontFamily = font ) }

        // Vind-speed
        Column(modifier = Modifier
            .weight(1.1f)
            .wrapContentSize()
            .padding(top = 15.dp) ) {

            Row {
                Text(
                    text = forecastViewModel.getWindSpeed(time),
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    fontFamily = font )
                Image(
                    imageVector = windIcon, contentDescription = "image",
                    Modifier.size(15.dp))
            }


        }

        // UV index
        Column(modifier = Modifier
            .weight(0.8f)
            .wrapContentSize()
            .padding(top = 15.dp) ) {
            Text(
                text = "${forecastViewModel.getUVindex(time)}",
                modifier = Modifier.weight(1f),
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                fontFamily = font ) }
    }
}


/**
 * Laster inn rader med hav-info, en rad for hvert klokkeslett
 */
@Composable
fun OceanRow(forecastViewModel: LocationForecastViewModel, time: Int, rowColor: Color, font: FontFamily) {

    val north  = ImageVector.vectorResource(id = R.drawable.north)
    val south  = ImageVector.vectorResource(id = R.drawable.south)
    val west  = ImageVector.vectorResource(id = R.drawable.west)
    val east  = ImageVector.vectorResource(id = R.drawable.oest)
    val northWest  = ImageVector.vectorResource(id = R.drawable.northwest)
    val northEast  = ImageVector.vectorResource(id = R.drawable.northeast)
    val southWest  = ImageVector.vectorResource(id = R.drawable.southwest)
    val southEast  = ImageVector.vectorResource(id = R.drawable.southeast)

    val currentFrom = forecastViewModel.getCurrentDirectionFrom(time)
    val fromIcon: ImageVector = when (currentFrom) {
        "Nord" -> north
        "Nord-øst" -> northEast
        "Øst" -> east
        "Sør-øst" -> southEast
        "Sør" -> south
        "Sør-vest" -> southWest
        "Vest" -> west
        "Nord-vest"-> northWest
        else -> north
    }

    val currentTowards = forecastViewModel.getCurrentDirectionTowards(time)
    val towardsIcon: ImageVector = when (currentTowards) {
        "Nord" -> north
        "Nord-øst" -> northEast
        "Øst" -> east
        "Sør-øst" -> southEast
        "Sør" -> south
        "Sør-vest" -> southWest
        "Vest" -> west
        "Nord-vest"-> northWest
        else -> north
    }

    // Rad x
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(45.dp)
            .background(rowColor),
        horizontalArrangement = Arrangement.Center )
    {
        // tidspunkt
        Column(modifier = Modifier
            .weight(1f)
            .wrapContentSize()
            .padding(top = 15.dp) ) {
            Text(
                text = forecastViewModel.getNorwegianTimeAndMinOcean(time),
                modifier = Modifier.weight(1f),
                fontWeight = FontWeight.ExtraBold,
                fontSize = 13.sp,
                fontFamily = font ) }

        // Vann Temnperatur
        Column(modifier = Modifier
            .weight(1f)
            .wrapContentSize()
            .padding(top = 15.dp) ) {
            Text(
                text = forecastViewModel.getSeaWaterTemperature(time),
                modifier = Modifier.weight(1f),
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                fontFamily = font ) }

        // Bølge høyde
        Column(modifier = Modifier
            .weight(1f)
            .wrapContentSize()
            .padding(top = 15.dp) ) {
            Text(
                text = forecastViewModel.getSeaWaveHeight(time),
                modifier = Modifier.weight(1f),
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                fontFamily = font ) }


        // Current from
        Column(modifier = Modifier
            .weight(1f).wrapContentSize().align(Alignment.CenterVertically) ) {
            Row {
                Text(
                    text = "fra ",
                    //  modifier = Modifier.weight(0.6f).padding(top = 0.dp),
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 11.sp,
                    fontFamily = font )
                Image(
                    imageVector = fromIcon, contentDescription = "image",
                    Modifier.size(15.dp))
                Text(
                    text = " til ",
                  //  modifier = Modifier.weight(0.6f).padding(top = 0.dp),
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 11.sp,
                    fontFamily = font )
                Image(
                    imageVector = towardsIcon, contentDescription = "image",
                    Modifier.size(15.dp))

            }
        }

        // Current speed
        Column(modifier = Modifier
            .weight(1f)
            .wrapContentSize()
            .padding(top = 18.dp) ) {
            Text(
                text = forecastViewModel.getCurrentSpeed(time),
                modifier = Modifier.weight(1f),
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                fontFamily = font ) }
    }

}






@Composable
fun OceanHeader(headerColor: Color, font: FontFamily) {
    // IKON  HAV-SKJERM
    val klokke = ImageVector.vectorResource(id = R.drawable.clock)
    val vannTemp = ImageVector.vectorResource(id = R.drawable.watertemp2)
    val wave = ImageVector.vectorResource(id = R.drawable.wave)
    val currentDirection = ImageVector.vectorResource(id = R.drawable.direction)
    val currentSpeed = ImageVector.vectorResource(id = R.drawable.current)
    Row(modifier = Modifier
        .fillMaxWidth()
        .background(headerColor)
        .height(42.dp)
        .padding(top = 5.dp),
        horizontalArrangement = Arrangement.Center, )
    {

        // Klokke ikon
        Column( modifier = Modifier
            .weight(1f)
            .wrapContentSize() ) {
            Image(
                imageVector = klokke, contentDescription = "image",
                Modifier
                    .size(35.dp)
                    .padding(top = 3.dp) ) }

        // Vann Temnperatur ikon
        Column( modifier = Modifier
            .weight(1f)
            .wrapContentSize() ) {
            Image(
                imageVector = vannTemp, contentDescription = "image",
                Modifier
                    .size(45.dp)) }

        // Bølge høyde ikon
        Column( modifier = Modifier
            .weight(1f)
            .wrapContentSize() ) {
            Image(
                imageVector = wave, contentDescription = "image",
                Modifier
                    .size(35.dp)
                    .padding(top = 3.dp)) }

        // Current ikon
        Column( modifier = Modifier
            .weight(1f)
            .wrapContentSize() ) {
            Image(
                imageVector = currentDirection, contentDescription = "image",
                Modifier
                    .size(35.dp)
                    .padding(3.dp) ) }

        // Pil mot
        Column( modifier = Modifier
            .weight(1f)
            .wrapContentSize() ) {
            Image(
                imageVector = currentSpeed, contentDescription = "image",
                Modifier
                    .size(32.dp)
                    .padding(top = 3.dp) ) }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(18.dp)
            .background(headerColor),
        horizontalArrangement = Arrangement.Center,
    )
    {

        // Klokke ikon
        Column(
            modifier = Modifier
                .weight(1f)
                .wrapContentSize()
        ) {
            Text(
                text = "Tid ",
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 5.dp),
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                fontSize = 11.sp,
                fontFamily = font
            )
        }

        // Bade temperatur
        Column(
            modifier = Modifier
                .weight(1f)
                .wrapContentSize()
        ) {
            Text(
                text = "Bade temp. °C",
                modifier = Modifier.weight(1f),
                fontWeight = FontWeight.ExtraBold,
                fontSize = 11.sp,
                fontFamily = font,
                color = Color.White,
            )
        }

        // Bølgehøyde
        Column(
            modifier = Modifier
                .weight(1f)
                .wrapContentSize()
        ) {
            Text(
                text = "Bølgehøyde",
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 5.dp),
                fontWeight = FontWeight.ExtraBold,
                fontSize = 11.sp,
                fontFamily = font,
                color = Color.White,
            )
        }
        // Strøm reting
        Column(
            modifier = Modifier
                .weight(1.1f)
                .wrapContentSize()

        ) {
            Text(
                text = "Strøm retning",
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 5.dp),
                fontWeight = FontWeight.ExtraBold,
                fontSize = 11.sp,
                fontFamily = font,
                color = Color.White,
            )
        }

        // Strøm hastighet
        Column(
            modifier = Modifier
                .weight(1f)
                .wrapContentSize()
                .padding(start = 5.dp)

        ) {
            Text(
                text = "Strøm m/s",
                modifier = Modifier.weight(1f),
                fontWeight = FontWeight.ExtraBold,
                fontSize = 10.sp,
                fontFamily = font,
                color = Color.White,
            )
        }
    }

}


@Composable
fun WeatherHeader(headerColor: Color, font: FontFamily) {
    // IKON FOR VÆRSKJERM
    val tempIcon = ImageVector.vectorResource(id = R.drawable.p1honsftvsnih1nss1kofsciqo4_page_165)
    val clockIcon = ImageVector.vectorResource(id = R.drawable.clock)
    val weatherIcon1 = ImageVector.vectorResource(id = R.drawable.weather1)
    val uvIcon = ImageVector.vectorResource(id = R.drawable.p1honsftvsnih1nss1kofsciqo4_page_194)
    val rainIcon = ImageVector.vectorResource(id = R.drawable.p1honsftvsnih1nss1kofsciqo4_page_152)
    val windIcon = ImageVector.vectorResource(id = R.drawable.wind2)
    val weatherIcon2 = ImageVector.vectorResource(id = R.drawable.ikonveer)


    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(42.dp)
            .background(headerColor)
            .padding(top = 5.dp),
        horizontalArrangement = Arrangement.Center,
    )
    {

        // Klokke ikon
        Column(
            modifier = Modifier
                .weight(1f)
                .wrapContentSize()
        ) {
            Image(
                imageVector = clockIcon, contentDescription = "image",
                Modifier
                    .size(35.dp)
                    .padding(top = 3.dp))
        }

        // Temperatur ikon
        Column(
            modifier = Modifier
                .weight(1f)
                .wrapContentSize()
        ) {
            Image(
                imageVector = tempIcon, contentDescription = "image",
                Modifier
                    .size(35.dp)
                    .padding(top = 4.dp)
            )
        }

        // Vær ikon
        Column(
            modifier = Modifier
                .weight(1f)
                .wrapContentSize()
        ) {
            Image(
                imageVector = weatherIcon2, contentDescription = "image",
                Modifier.size(35.dp)
            )
        }
        // Nedbørsmengde ikon
        Column(
            modifier = Modifier
                .weight(1f)
                .wrapContentSize()
        ) {
            Image(
                imageVector = rainIcon, contentDescription = "image",
                Modifier.size(35.dp)
            )
        }

        // Vind ikon
        Column(
            modifier = Modifier
                .weight(1f)
                .wrapContentSize()
        ) {
            Image(
                imageVector = windIcon, contentDescription = "image",
                Modifier
                    .size(34.dp)
                    .padding(top = 5.dp)
            )
        }

        // UV ikon
        Column(
            modifier = Modifier
                .weight(1f)
                .wrapContentSize()
        ) {
            Image(
                imageVector = uvIcon, contentDescription = "image",
                Modifier
                    .size(37.dp)
                    .padding(top = 5.dp)
            )
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(18.dp)
            .background(headerColor),
        horizontalArrangement = Arrangement.Center,
    )
    {

        // Klokke ikon
        Column(
            modifier = Modifier
                .weight(1f)
                .wrapContentSize()
        ) {
            Text(
                text = "Tid ",
                modifier = Modifier.weight(1f),
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                fontSize = 11.sp,
                fontFamily = font
            )
        }

        // Temperatur ikon
        Column(
            modifier = Modifier
                .weight(0.8f)
                .wrapContentSize()
        ) {
            Text(
                text = "Temp. °C",
                modifier = Modifier.weight(1f),
                fontWeight = FontWeight.ExtraBold,
                fontSize = 11.sp,
                fontFamily = font,
                color = Color.White,
                )
        }

        // Vær ikon
        Column(
            modifier = Modifier
                .weight(1f)
                .wrapContentSize()
        ) {
            Text(
                text = "Vær",
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 15.dp),
                fontWeight = FontWeight.ExtraBold,
                fontSize = 11.sp,
                fontFamily = font,
                color = Color.White,
                )
        }
            // Nedbørsmengde ikon
            Column(
                modifier = Modifier
                    .weight(1.2f)
                    .wrapContentSize()

            ) {
                Text(
                    text = "Nedbør mm",
                    modifier = Modifier.weight(1f),
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 11.sp,
                    fontFamily = font,
                    color = Color.White,
                    )
            }

            // Vind ikon
            Column(
                modifier = Modifier
                    .weight(1.2f)
                    .wrapContentSize()
                    .padding(start = 5.dp)

            ) {
                Text(
                    text = "Vind(kast) m/s",
                    modifier = Modifier.weight(1f),
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 10.sp,
                    fontFamily = font,
                    color = Color.White,
                    )
            }

            // UV ikon
            Column(
                modifier = Modifier
                    .weight(0.8f)
                    .wrapContentSize()
            ) {
                Text(
                    text = "UV",
                    modifier = Modifier.weight(1f),
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 11.sp,
                    fontFamily = font,
                    color = Color.White,

                    )
            }
        }
}

/**
 * Henter riktig ikon for værskjermen.
 * NB: Funksjonen har "SuppressLint" annotering fordi ikonnavn
 * og ID er dynamiske i stedet for statiske, noe som gir en warning.
 */
@SuppressLint("DiscouragedApi")
@Composable
fun GetWeatherIcon(forecastViewModel: LocationForecastViewModel) {
    val iconName = forecastViewModel.getWeatherIcon(0) ?: "fair_day"
    val context = LocalContext.current
    val resId = context.resources.getIdentifier(iconName, "drawable", context.packageName)

    val weatherIcon: ImageVector = if (resId != 0) {
        ImageVector.vectorResource(id = resId)
    } else {
        ImageVector.vectorResource(id = R.drawable.fair_day) }
    Image(imageVector = weatherIcon, contentDescription = "image",
        Modifier
            .size(110.dp)
            .padding(top = 3.dp, bottom = 10.dp))

}






