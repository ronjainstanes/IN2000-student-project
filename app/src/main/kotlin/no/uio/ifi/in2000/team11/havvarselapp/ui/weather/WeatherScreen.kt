package no.uio.ifi.in2000.team11.havvarselapp.ui.weather

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import no.uio.ifi.in2000.team11.havvarselapp.ui.map.SeaMapViewModel
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
@SuppressLint("DiscouragedApi", "FlowOperatorInvokedInComposition")
@Composable
fun WeatherScreen( forecastViewModel: LocationForecastViewModel = viewModel(), seaMapViewModel: SeaMapViewModel = viewModel()){
    val displayInfo = remember { mutableStateOf(DisplayInfo.Weather) } // endret fra by til = slik at funksjonene kan være utenfor hovedmetoden
    val expanded = remember { mutableStateOf(Expanded.Short) }
    val context = LocalContext.current

    // Henter MapUiState fra SeaMapViewModel
    val mapUiState by seaMapViewModel.mapUiState.collectAsState()

    // Bruker LaunchedEffect for å laste værdata når posisjonen endres via søk / pin
    LaunchedEffect(mapUiState.currentLocation.hashCode()) {
        forecastViewModel.loadForecast(
            mapUiState.currentLocation.latitude.toString(),
            mapUiState.currentLocation.longitude.toString()
        )
        // slik at Stedsnavn oppdateres når data endres
        forecastViewModel.setCurrentPlaceName(
            context,
            mapUiState.currentLocation.latitude,
            mapUiState.currentLocation.longitude
        )
    }

    // FARGENE TIL TABELLEN VÆR: rad 1 er oddetall rader, rad 2 er partall, header er header + time for time.
    val weatherRow1 = Color(234, 236, 255, 255)
    val weatherRow2 = Color(218, 222, 255, 255)
    val weatherHeader = Color(120, 133, 191, 255)

    // FARGENE TIL TABELLEN HAV: rad 1 er oddetall rader, rad 2 er partall, header er header + time for time.
    val oceanRow1 = Color(212, 225, 255, 255)
    val oceanRow2 = Color(190, 210, 254, 255)
    val oceanHeader = Color(105, 131, 187, 232)

   // Ulike fonter, getFonts1-getFonts6. Laget funksjoner for mindre rot, bare å slette når vi vet hva vi skal bruke.
    val fonts3 = getFonts3()


    // FOR Å ENDRE FONT PÅ ALT ENDRE BARE DENNE VARIABELEN !!
    val fontNormal = fonts3[2]
    val fontBold = fonts3[3]

    // knappene for vær og hav, sitter fast på bunn av siden
    Scaffold(
        bottomBar = { BottomNavBar(currentScreen = displayInfo, fontNormal) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(innerPadding)
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(2.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            )
            {
                // øverste del av skjermen frem til tabellen
                ScreenTop(
                    forecastViewModel = forecastViewModel,
                    displayInfo = displayInfo,
                    fontNormal = fontNormal,
                    fontBold = fontBold
                )


                // KORT med vær tabellen
                Card(
                    modifier = Modifier
                        .padding(start = 8.dp, end = 8.dp)
                        .border(width = 1.dp, color = weatherRow2, shape = RoundedCornerShape(8.dp))
                        .fillMaxWidth()
                )
                {
                    // WEATHER SCREEN
                    when (displayInfo.value) {
                        DisplayInfo.Weather -> {
                            // RAD MED IKON ØVERST
                            WeatherHeader(headerColor = weatherHeader, font = fontNormal)
                            // LASTER INN RADER MED VÆR-INFO
                            if (forecastViewModel.forecastInfoUiState.collectAsState().value != null) {
                                when (expanded.value) {

                                    Expanded.Short -> {
                                        var farge = true
                                        for (i in 0..1) {
                                            farge = if (farge) {
                                                WeatherRow(
                                                    forecastViewModel,
                                                    i,
                                                    weatherRow1,
                                                    fontNormal
                                                )
                                                false
                                            } else {
                                                WeatherRow(
                                                    forecastViewModel,
                                                    i,
                                                    weatherRow2,
                                                    fontNormal
                                                )
                                                true
                                            }
                                        }
                                        ShortToLongButton(expanded, weatherHeader, fontNormal)
                                    }

                                    Expanded.Long -> {
                                        var farge = true
                                        for (i in 0..8) {
                                            farge = if (farge) {
                                                WeatherRow(
                                                    forecastViewModel,
                                                    i,
                                                    weatherRow1,
                                                    fontNormal
                                                )
                                                false
                                            } else {
                                                WeatherRow(
                                                    forecastViewModel,
                                                    i,
                                                    weatherRow2,
                                                    fontNormal
                                                )
                                                true
                                            }
                                        }
                                        ShortToLongButton(expanded, weatherHeader, fontNormal)
                                    }
                                }
                            }
                        }
                        // VÆR-SKJERM SLUTT


                        // OCEAN-SCREEN
                        DisplayInfo.Sea -> {
                            // RAD MED IKON ØVERST Oceanforecast
                            OceanHeader(headerColor = oceanHeader, font = fontNormal)

                            // LASTER INN RADENE MED HAV-INFO
                            if (forecastViewModel.oceanForecastUiState.collectAsState().value != null) {
                                when (expanded.value) {

                                    Expanded.Short -> {
                                        var farge = true
                                        // ALLE RADENE med Hav-info
                                        for (i in 0..1) {
                                            farge = if (farge) {
                                                OceanRow(
                                                    forecastViewModel,
                                                    i,
                                                    oceanRow1,
                                                    fontNormal
                                                )
                                                false
                                            } else {
                                                OceanRow(
                                                    forecastViewModel,
                                                    i,
                                                    oceanRow2,
                                                    fontNormal
                                                )
                                                true
                                            }
                                        }
                                        ShortToLongButton(expanded, oceanHeader, fontNormal)
                                    }

                                    Expanded.Long -> {
                                        var farge = true
                                        // ALLE RADENE med Hav-info
                                        for (i in 0..8) {
                                            farge = if (farge) {
                                                OceanRow(
                                                    forecastViewModel,
                                                    i,
                                                    oceanRow1,
                                                    fontNormal
                                                )
                                                false
                                            } else {
                                                OceanRow(
                                                    forecastViewModel,
                                                    i,
                                                    oceanRow2,
                                                    fontNormal
                                                )
                                                true
                                            }
                                        }
                                        ShortToLongButton(expanded, oceanHeader, fontNormal)
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
                        fontSize = 22.sp,
                        fontFamily = fontNormal,
                        fontWeight = FontWeight.ExtraBold
                    )
                )
            }
        }
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
    val iconName = forecastViewModel.getWeatherIcon(time)
    val context = LocalContext.current
    val resId = context.resources.getIdentifier(iconName, "drawable", context.packageName)

    val weatherIcon: ImageVector = if (resId != 0) {
        ImageVector.vectorResource(id = resId)
    } else {
        ImageVector.vectorResource(id = R.drawable.fair_day) }

    val pos = Color(159, 8, 8, 255) // Farge til positiv temp som i YR
    val neg = Color(40, 75, 202, 255) // Farge til negativ temp

    val north  = ImageVector.vectorResource(id = R.drawable.north)
    val south  = ImageVector.vectorResource(id = R.drawable.south)
    val west  = ImageVector.vectorResource(id = R.drawable.west)
    val east  = ImageVector.vectorResource(id = R.drawable.oest)
    val northWest  = ImageVector.vectorResource(id = R.drawable.northwest)
    val northEast  = ImageVector.vectorResource(id = R.drawable.northeast)
    val southWest  = ImageVector.vectorResource(id = R.drawable.southwest)
    val southEast  = ImageVector.vectorResource(id = R.drawable.southeast)

    val windIcon: ImageVector = when (forecastViewModel.getWindDirection(time)) {
        "N" -> north
        "NØ" -> northEast
        "Ø" -> east
        "SØ" -> southEast
        "S" -> south
        "SV" -> southWest
        "V" -> west
        "NV"-> northWest
        else -> north
    }

    // Rad x
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(45.dp)
            .background(rowColor),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically) {

        // tidspunkt
        Column(modifier = Modifier
            .weight(0.8f)
            .wrapContentSize() ) {
            Text(
                text = forecastViewModel.getNorwegianTimeWeather(time),
                fontWeight = FontWeight.ExtraBold,
                fontSize = 13.sp,
                fontFamily = font )
        }


        // Ikon for været
        Column(modifier = Modifier
            .weight(0.8f)
            .wrapContentSize()) {
            Image(
                imageVector = weatherIcon, contentDescription = "image",
                Modifier.size(33.dp))
        }

        // Temnperatur
        Column(modifier = Modifier
            .weight(1f)
            .wrapContentSize()) {
            Text(
                text = forecastViewModel.getTemperature(time),
                fontWeight = FontWeight.ExtraBold,
                fontSize = 13.sp,
                fontFamily = font,
                color = if (forecastViewModel.temperaturePositive(time)) pos else neg )
        }

        // Rain / perciption amount
        Column(modifier = Modifier
            .weight(1.25f)
            .wrapContentSize()) {
            Text(
                text = forecastViewModel.getPrecipitationAmountMaxMin(time),
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                fontFamily = font,
                color = neg )
        }

        // Vind-speed
        Column(modifier = Modifier
            .weight(1.2f)
            .wrapContentSize()) {
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


    }
}


/**
 * Laster inn rader med hav-info, en rad for hvert klokkeslett
 */
@Composable
fun OceanRow(forecastViewModel: LocationForecastViewModel, time: Int, rowColor: Color, font: FontFamily) {
    val pos = Color(159, 8, 8, 255) // Farge til positiv temp som i YR
    val neg = Color(40, 75, 202, 255) // Farge til negativ temp
    val arrow  = ImageVector.vectorResource(id = R.drawable.oest)
    val currentFrom = forecastViewModel.getCurrentDirectionFrom(time)
    val currentTowards = forecastViewModel.getCurrentDirectionTowards(time)


    // Rad x
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(45.dp)
            .background(rowColor),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically)
    {
        // tidspunkt
        Column(modifier = Modifier
            .weight(0.7f)
            .wrapContentSize()) {
            Text(
                text = forecastViewModel.getNorwegianTimeOcean(time),
                fontWeight = FontWeight.ExtraBold,
                fontSize = 13.sp,
                fontFamily = font ) }

        // Vann Temnperatur
        Column(modifier = Modifier
            .weight(1f)
            .wrapContentSize() ) {
            Text(
                text = forecastViewModel.getSeaWaterTemperature(time),
                fontWeight = FontWeight.ExtraBold,
                fontSize = 13.sp,
                fontFamily = font,
                color = if (forecastViewModel.seaTemperaturePositive(time)) pos else neg
                ) }




        // Current from
        Column( modifier = Modifier
            .weight(1f)
            .wrapContentSize() ) {
            Row {
                Text(
                    text = "$currentFrom ",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 11.sp,
                    fontFamily = font )

                Image(
                    imageVector = arrow, contentDescription = "image",
                    Modifier
                        .size(15.dp)
                        .align(Alignment.CenterVertically))
                Text(
                    text = " $currentTowards",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 11.sp,
                    fontFamily = font )
                /**
                Image(
                    imageVector = towardsIcon, contentDescription = "image",
                    Modifier.size(15.dp)) */
            }
        }

        // Current speed
        Column( modifier = Modifier
            .weight(0.8f)
            .wrapContentSize() ) {
            Text(
                text = forecastViewModel.getCurrentSpeed(time),
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                fontFamily = font ) }
    }

}






@Composable
fun OceanHeader(headerColor: Color, font: FontFamily) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(30.dp)
            .background(headerColor),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    )
    {

        // Klokke ikon
        Column(modifier = Modifier
            .weight(0.7f)
            .wrapContentSize()) {
            Text(
                text = "Tid ",
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                fontSize = 11.sp,
                fontFamily = font )
        }

        // Bade temperatur
        Column(modifier = Modifier
            .weight(1f)
            .wrapContentSize()) {
            Text(
                text = "Bade temp. °C",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 11.sp,
                fontFamily = font,
                color = Color.White, )
        }



        // Strøm reting
        Column( modifier = Modifier
            .weight(1f)
            .wrapContentSize() ) {
            Text(
                text = "Strøm retning",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 11.sp,
                fontFamily = font,
                color = Color.White,)
        }

        // Strøm hastighet
        Column( modifier = Modifier
            .weight(0.8f)
            .wrapContentSize() ) {
            Text(
                text = "Strøm m/s",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 11.sp,
                fontFamily = font,
                color = Color.White,)
        }
    }

}


@Composable
fun WeatherHeader(headerColor: Color, font: FontFamily) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(30.dp)
            .background(headerColor),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    )
    {

        // Tidspunkt
        Column(modifier = Modifier
            .weight(0.8f)
            .wrapContentSize()) {
            Text(
                text = "Tid ",
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                fontSize = 11.sp,
                fontFamily = font
            )
        }

        // Vær ikon
        Column( modifier = Modifier
            .weight(0.8f)
            .wrapContentSize() ) {
            Text(
                text = "Vær",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 11.sp,
                fontFamily = font,
                color = Color.White,
            )
        }

        // Temperatur
        Column(modifier = Modifier
            .weight(1f)
            .wrapContentSize()) {
            Text(
                text = "Temp. °C",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 11.sp,
                fontFamily = font,
                color = Color.White,
                )
        }


            // Nedbørsmengde
            Column(modifier = Modifier
                .weight(1.25f)
                .wrapContentSize()) {
                Text(
                    text = "Nedbør mm",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 11.sp,
                    fontFamily = font,
                    color = Color.White,
                    )
            }

            // Vind
            Column(modifier = Modifier
                .weight(1.2f)
                .wrapContentSize() ) {
                Text(
                    text = "Vind(kast) m/s",
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
    val iconName = forecastViewModel.getWeatherIcon(0)
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

/**
 * Knappene for å bytte mellom vær og hav skjerm. Ligger i scaffol BottomBar slik at den er festet til bunnen av siden.
 */
@Composable
fun BottomNavBar(currentScreen: MutableState<DisplayInfo>, font: FontFamily) {
    val pos = Color(69, 79, 92, 167) // Farge til header i tabellen
    val neg = Color(155, 163, 174, 255) // Farge til header i tabellen

    val weatherColor = if (currentScreen.value == DisplayInfo.Weather) pos else neg
    val oceanColor = if (currentScreen.value == DisplayInfo.Sea) pos else neg

    Box(
        modifier = Modifier
            .fillMaxWidth().padding(bottom = 7.dp)
    ) {
        Row(
            modifier = Modifier
                .width(280.dp)
                .height(38.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(neg)
                .align(Alignment.Center),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically


        ) {
            Button(
                onClick = { currentScreen.value = DisplayInfo.Weather },
                colors = ButtonDefaults.buttonColors(weatherColor),
                modifier = Modifier.weight(1f).padding(4.dp).fillMaxSize(),
                shape = RoundedCornerShape(10.dp) ) {
                Text(
                    text = "Vær",
                    modifier = Modifier.fillMaxHeight(),
                    color =  if (currentScreen.value == DisplayInfo.Weather) Color.White else Color.Black,
                    fontFamily = font,
                    textAlign = TextAlign.Center,
                    fontSize = 15.sp,
                    lineHeight = 15.sp,
                    fontWeight = if (currentScreen.value == DisplayInfo.Weather) FontWeight.ExtraBold else FontWeight.Normal



                )
            }
            Button(
                onClick = { currentScreen.value = DisplayInfo.Sea },
                colors = ButtonDefaults.buttonColors(oceanColor),
                modifier = Modifier.weight(1f).padding(4.dp),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    text = "Hav",
                    color =  if (currentScreen.value == DisplayInfo.Sea) Color.White else Color.Black,
                    fontFamily = font,
                    textAlign = TextAlign.Center,
                    fontSize = 15.sp,
                    lineHeight = 15.sp,
                    fontWeight = if (currentScreen.value == DisplayInfo.Sea) FontWeight.ExtraBold else FontWeight.Normal
                )
            }
        }
    }
}


/**
 * Knappen for å utvide og minke tabellen time for time
 */
@Composable
fun ShortToLongButton(expanded: MutableState<Expanded>, color: Color, font: FontFamily) {
    val pilopp = ImageVector.vectorResource(id = R.drawable.p1honsftvsnih1nss1kofsciqo4_page_145)
    val pilned = ImageVector.vectorResource(id = R.drawable.p1honsftvsnih1nss1kofsciqo4_page_146)
    Row( modifier = Modifier
        .fillMaxWidth()
        .height(30.dp)
        .background(color),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        when (expanded.value) {
            Expanded.Short -> {
                Button(
                    onClick = { expanded.value = Expanded.Long },
                    modifier = Modifier.fillMaxSize(),
                    colors = ButtonDefaults.buttonColors(color)
                )
                {
                    Text(
                        text = "Time for time ",
                        fontSize = 11.sp,
                        lineHeight = 11.sp,
                        fontFamily = font,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
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
                    onClick = { expanded.value = Expanded.Short },
                    modifier = Modifier.fillMaxSize(),
                    colors = ButtonDefaults.buttonColors(color))
                {
                    Text(
                        text = "Time for time ",
                        textAlign = TextAlign.Center,
                        fontSize = 11.sp,
                        lineHeight = 11.sp,
                        fontFamily = font,
                        fontWeight = FontWeight.Bold,
                    )
                    Image(
                        imageVector = pilopp,
                        contentDescription = "pil",
                        Modifier.size(25.dp) )
                }

            }
        }
    }
}

/**
 * Funksjon for å laste øverste del av vær / hav siden før tabellen.
 */
@Composable
fun ScreenTop(forecastViewModel: LocationForecastViewModel, displayInfo: MutableState<DisplayInfo>, fontNormal: FontFamily, fontBold: FontFamily) {


    Text(
        text = forecastViewModel.getPlaceName(),
        textAlign = TextAlign.Center,
        fontSize = 18.sp,
        fontFamily = fontNormal,
        fontWeight = FontWeight.ExtraBold,
        modifier = Modifier.padding(top = 10.dp)
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically )
    {
        if (forecastViewModel.forecastInfoUiState.collectAsState().value != null) {

            Column(modifier = Modifier.weight(1f)) {
                Row( modifier = Modifier
                    .align(Alignment.Start)
                    .padding(start = 26.dp, top = 5.dp) ) {
                    Text(
                        text = "I dag "+ forecastViewModel.getNorwegianTimeWeather(0) + "-" +forecastViewModel.getNorwegianTimeWeather(1),
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 14.sp,
                        fontFamily = fontBold )

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
                        fontFamily = fontBold )

                }
                Column( modifier = Modifier
                    .align(Alignment.Start)
                    .padding(start = 15.dp, top = 15.dp) ) {
                    when (displayInfo.value) {
                        DisplayInfo.Weather -> {
                            Text(
                                text = "Last updated:\n" + forecastViewModel.locationForecastLastUpdated(),
                                fontWeight = FontWeight.ExtraBold,
                                lineHeight = 13.sp,
                                fontSize = 9.sp,
                                color = Color(115, 115, 115, 255),
                                fontFamily = fontNormal )
                        }
                        DisplayInfo.Sea -> {
                            Text(
                                text = "Last updated:\n" + forecastViewModel.oceanForecastLastUpdated(),
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 9.sp,
                                color = Color(115, 115, 115, 255),
                                lineHeight = 13.sp,
                                fontFamily = fontNormal )
                        }
                    }

                }

            }

            Column (
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally )
            {
                GetWeatherIcon(forecastViewModel = forecastViewModel)
            }

            Spacer( modifier = Modifier.weight(1f) )

        }

    }

}



fun getFonts1(): Array<FontFamily> {
    val barlow1 = FontFamily( Font(R.font.barlow_thin, FontWeight.W400))
    val barlow2 = FontFamily( Font(R.font.barlow_extralight, FontWeight.W400))
    val barlow3 = FontFamily( Font(R.font.barlow_light, FontWeight.W400))
    val barlow4 = FontFamily( Font(R.font.barlow_regular, FontWeight.W400))
    return  arrayOf(barlow1, barlow2, barlow3, barlow4 )
}

fun getFonts2(): Array<FontFamily> {
    val barlowcondensed1 = FontFamily( Font(R.font.barlowcondensed_thin, FontWeight.W400))
    val barlowcondensed2 = FontFamily( Font(R.font.barlowcondensed_extralight, FontWeight.W400))
    val barlowcondensed3 = FontFamily( Font(R.font.barlowcondensed_light, FontWeight.W400))
    val barlowcondensed4 = FontFamily( Font(R.font.barlowcondensed_regular, FontWeight.W400))
    return  arrayOf(barlowcondensed1, barlowcondensed2, barlowcondensed3, barlowcondensed4 )
}


fun getFonts3(): Array<FontFamily> {
    val roboto1 = FontFamily( Font(R.font.robotocondensed_thin, FontWeight.W400))
    val roboto2 = FontFamily( Font(R.font.robotocondensed_extralight, FontWeight.W400))
    val roboto3 = FontFamily( Font(R.font.robotocondensed_light, FontWeight.W400))
    val roboto4 = FontFamily( Font(R.font.robotocondensed_regular, FontWeight.W400))
    return  arrayOf(roboto1, roboto2, roboto3, roboto4)
}

fun getFonts4(): Array<FontFamily> {
    val natoSansJP = FontFamily( Font(R.font.notosansjp_variablefont_wght, FontWeight.W400))
    val natoSansJPExtralight = FontFamily( Font(R.font.notosansjp_extralight, FontWeight.W400))
    val natoSansJPLight = FontFamily( Font(R.font.notosansjp_light, FontWeight.W400))
    val natoSansJPRegular = FontFamily( Font(R.font.notosansjp_regular, FontWeight.W400))
    val natoSansJPExtrabold = FontFamily( Font(R.font.notosansjp_extrabold, FontWeight.W400))
    return arrayOf( natoSansJP, natoSansJPExtralight, natoSansJPLight, natoSansJPRegular,  natoSansJPExtrabold )
}

fun getFonts5(): Array<FontFamily> {
    val poppinsExtralight = FontFamily( Font(R.font.poppins_extralight, FontWeight.W400))
    val poppinsLight = FontFamily( Font(R.font.poppins_light, FontWeight.W400))
    val poppinsRegular = FontFamily( Font(R.font.poppins_regular, FontWeight.W400))
    return  arrayOf(poppinsExtralight, poppinsLight, poppinsRegular )
}

fun getFonts6(): Array<FontFamily> {
    val truculentaRegular = FontFamily( Font(R.font.truculenta_regular, FontWeight.W400))
    val truculentaLight = FontFamily( Font(R.font.truculenta_semiexpanded_light, FontWeight.W400))
    val truculentaMedium = FontFamily( Font(R.font.truculenta_semiexpanded_medium, FontWeight.W400))
    val truculenta3 = FontFamily( Font(R.font.truculenta_semiexpanded_regular, FontWeight.W400))
    val truculenta4 = FontFamily( Font(R.font.truculenta_60pt_semicondensed_regular, FontWeight.W400))
    return  arrayOf(truculentaLight, truculenta4 ,truculentaRegular, truculenta3, truculentaMedium )
}


