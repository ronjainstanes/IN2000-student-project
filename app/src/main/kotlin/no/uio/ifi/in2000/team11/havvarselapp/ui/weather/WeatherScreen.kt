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
import androidx.compose.foundation.lazy.LazyColumn
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
import no.uio.ifi.in2000.team11.havvarselapp.model.locationForecast.Timeseries
import no.uio.ifi.in2000.team11.havvarselapp.model.oceanForecast.TimeseriesOcean
import no.uio.ifi.in2000.team11.havvarselapp.ui.locationForecast.LocationForecastViewModel
import no.uio.ifi.in2000.team11.havvarselapp.ui.map.SeaMapViewModel
import no.uio.ifi.in2000.team11.havvarselapp.ui.metalert.CurrentLocationAlert
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.roundToInt

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
fun WeatherScreen(
    forecastViewModel: LocationForecastViewModel = viewModel(),
    seaMapViewModel: SeaMapViewModel = viewModel()
){
    val displayInfo = remember { mutableStateOf(DisplayInfo.Weather) } // endret fra by til = slik at funksjonene kan være utenfor hovedmetoden
    val context = LocalContext.current

    // Henter MapUiState fra SeaMapViewModel
    val mapUiState by seaMapViewModel.mapUiState.collectAsState()
    val locationForecastUiState by forecastViewModel.forecastInfoUiState.collectAsState()
    val oceanForecastUiState by forecastViewModel.oceanForecastUiState.collectAsState()

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

                ScreenContent(
                    forecastViewModel = forecastViewModel,
                    displayInfo = displayInfo,
                    fontNormal = fontNormal)


            }
        }
    }
}

fun List<Timeseries>.groupByDay(): Map<LocalDate, List<Timeseries>> {
    val zoneId = ZoneId.of("Europe/Oslo")
    return this.groupBy {
        ZonedDateTime.parse(it.time).withZoneSameInstant(zoneId).toLocalDate()
    }
}
fun List<TimeseriesOcean>.groupByDayOcean(): Map<LocalDate, List<TimeseriesOcean>> {
    val zoneId = ZoneId.of("Europe/Oslo")
    return this.groupBy {
        ZonedDateTime.parse(it.time).withZoneSameInstant(zoneId).toLocalDate()
    }
}

@Composable
fun ScreenContent(forecastViewModel: LocationForecastViewModel, displayInfo: MutableState<DisplayInfo>, fontNormal: FontFamily) {
    val locationForecastUiState by forecastViewModel.forecastInfoUiState.collectAsState()
    val oceanForecastUiState by forecastViewModel.oceanForecastUiState.collectAsState()
    val forecastGroupedDayByDay = locationForecastUiState?.properties?.timeseries?.groupByDay()
    val oceanGroupedDayByDay = oceanForecastUiState?.properties?.timeseries?.groupByDayOcean()


    when (displayInfo.value) {
        DisplayInfo.Weather -> {
            if (!forecastGroupedDayByDay.isNullOrEmpty())
            {
                LazyColumn {
                    var todayOrTommorow = 0
                    forecastGroupedDayByDay.entries.take(3).forEach() { (day, timeseriesList) ->
                        if (todayOrTommorow == 0) {
                            item {
                                DayWeatherCard(
                                    day = day,
                                    timeseriesList = timeseriesList,
                                    fontNormal = fontNormal,
                                    todayOrTmr = "I dag"
                                )
                            }
                        }
                        else if (todayOrTommorow == 1) {
                            item {
                                DayWeatherCard(
                                    day = day,
                                    timeseriesList = timeseriesList,
                                    fontNormal = fontNormal,
                                    todayOrTmr = "I morgen"
                                )
                            }

                        }
                        else {
                            item {
                                DayWeatherCard(
                                    day = day,
                                    timeseriesList = timeseriesList,
                                    fontNormal = fontNormal
                                )
                            }
                        }
                       todayOrTommorow++
                    }
                    forecastGroupedDayByDay.entries.drop(3).take(6).forEach() { (day, timeseriesList) ->
                        item {
                            DayWeatherCardLongTerm(
                                day = day,
                                timeseriesList = timeseriesList,
                                fontNormal = fontNormal
                            )
                        }
                    }

                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 30.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically) {
                            CurrentLocationAlert(
                                region = "oslo",
                                TextStyle( fontSize = 22.sp, fontFamily = fontNormal, fontWeight = FontWeight.ExtraBold )
                            )

                        }

                    }

                }
            }
        }
        // VÆR-SKJERM SLUTT


        // OCEAN-SCREEN
        DisplayInfo.Sea -> {
            if (!oceanGroupedDayByDay.isNullOrEmpty()) {
                LazyColumn {
                    var todayOrTommorow = 0
                    oceanGroupedDayByDay.entries.take(2).forEach() { (day, timeseriesOceanList) ->
                        if (todayOrTommorow == 0) {
                            item {
                                DayOceanCard(day = day,
                                    timeseriesList = timeseriesOceanList,
                                    fontNormal = fontNormal,
                                    todayOrTmr = "I dag")
                            }

                        }
                        else if (todayOrTommorow == 1) {
                            item {
                                DayOceanCard(day = day,
                                    timeseriesList = timeseriesOceanList,
                                    fontNormal = fontNormal,
                                    todayOrTmr = "I morgen")
                            }
                        }
                        todayOrTommorow++
                    }
                    oceanGroupedDayByDay.entries.drop(2).forEach() { (day, timeseriesOceanList) ->
                        item {
                            DayOceanCard(
                                day = day,
                                timeseriesList = timeseriesOceanList,
                                fontNormal = fontNormal )
                        }
                    }

                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 30.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically)
                        {
                            CurrentLocationAlert(
                                region = "oslo",
                                TextStyle(
                                    fontSize = 22.sp,
                                    fontFamily = fontNormal,
                                    fontWeight = FontWeight.ExtraBold) )
                        }
                    }
                }
            }
        }
        // HAV-SKJERM SLUTT
    }
}

@SuppressLint("SuspiciousIndentation")
@Composable
fun DayWeatherCard(
    day: LocalDate,
    timeseriesList: List<Timeseries>,
    fontNormal: FontFamily,
    todayOrTmr: String? = " "
) {
    val expanded = remember { mutableStateOf(Expanded.Short) }
    val weatherRow1 = Color(234, 236, 255, 255)
    val weatherRow2 = Color(218, 222, 255, 255)
    val weatherHeader = Color(120, 133, 191, 255)
    var formattedDay = day.format(DateTimeFormatter.ofPattern("EEEE d. MMMM", Locale("no", "NO")))
    if (todayOrTmr == " ") {
        formattedDay =
            formattedDay.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.titlecase() }
    }

    Column(
        modifier = if (todayOrTmr == "I dag") Modifier.padding(top = 0.dp) else Modifier.padding(top = 10.dp)
    ) {


        Row(
            modifier = Modifier
                .height(30.dp)
                .fillMaxWidth()
                .background(color = Color.Transparent)
                .padding(start = 18.dp),
            horizontalArrangement = Arrangement.Start, verticalAlignment = Alignment.Bottom
        ) {
            Text(
                text = if (todayOrTmr == " ") formattedDay else "$todayOrTmr $formattedDay",
                fontFamily = fontNormal,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 14.sp,
                lineHeight = 25.sp
            )
        }

        Card(
            modifier = Modifier
                .padding(start = 8.dp, end = 8.dp)
                .border(width = 1.dp, color = weatherRow2, shape = RoundedCornerShape(8.dp))
                .fillMaxWidth()
        )
        {
            // RAD MED IKON ØVERST
            WeatherHeader(headerColor = weatherHeader, font = fontNormal)
            when (expanded.value) {

                Expanded.Short -> {
                    if (timeseriesList[0].data.next_1_hours != null) {
                        WeatherRow(timeseriesList[0], fontNormal, weatherRow1)
                    } else {
                        WeatherRowLongTerm(timeseriesList[0], fontNormal, weatherRow1)
                    }

                    if (timeseriesList.size > 1) {
                        if (timeseriesList[1].data.next_1_hours != null) {
                            WeatherRow(timeseriesList[1], fontNormal, weatherRow2)
                        } else {
                            WeatherRowLongTerm(timeseriesList[1], fontNormal, weatherRow2)
                        }
                    }

                    if (timeseriesList.size > 2) {
                        ShortToLongButton(expanded, weatherHeader, fontNormal)
                    }
                }

                Expanded.Long -> {
                    var farge = true
                    timeseriesList.forEach { timeseries ->
                        farge = if (farge) {
                            if (timeseries.data.next_1_hours != null) {
                                WeatherRow(timeseries, fontNormal, weatherRow1)
                            } else {
                                WeatherRowLongTerm(
                                    data = timeseries,
                                    font = fontNormal,
                                    rowColor = weatherRow1
                                )
                            }
                            false
                        } else {
                            if (timeseries.data.next_1_hours != null) {
                                WeatherRow(timeseries, fontNormal, weatherRow2)
                            } else {
                                WeatherRowLongTerm(
                                    data = timeseries,
                                    font = fontNormal,
                                    rowColor = weatherRow2
                                )
                            }
                            true
                        }
                    }
                    if (timeseriesList.size > 2) {
                        ShortToLongButton(expanded, weatherHeader, fontNormal)
                    }
                }
            }

        }
    }
}


@Composable
fun DayWeatherCardLongTerm(
    day: LocalDate,
    timeseriesList: List<Timeseries>,
    fontNormal: FontFamily
) {
    val weatherRow1 = Color(234, 236, 255, 255)
    val weatherRow2 = Color(218, 222, 255, 255)
    val weatherHeader = Color(120, 133, 191, 255)
    var formattedDay = day.format(DateTimeFormatter.ofPattern("EEEE d. MMMM", Locale("no", "NO")))
    formattedDay =
        formattedDay.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.titlecase() }

    Column( modifier = Modifier.padding(top = 10.dp) ) {
        Row(
            modifier = Modifier
                .height(30.dp)
                .fillMaxWidth()
                .background(color = Color.Transparent)
                .padding(start = 18.dp),
            horizontalArrangement = Arrangement.Start, verticalAlignment = Alignment.Bottom
        ) {
            Text(
                text = formattedDay,
                fontFamily = fontNormal,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 14.sp,
                lineHeight = 25.sp
            )
        }
        Card(
            modifier = Modifier
                .padding(start = 8.dp, end = 8.dp)
                .border(width = 1.dp, color = weatherRow2, shape = RoundedCornerShape(8.dp))
                .fillMaxWidth()
        )
        {
            // RAD MED IKON ØVERST
            WeatherHeaderLongTerm(headerColor = weatherHeader, font = fontNormal)
            var farge = true
            timeseriesList.forEach { timeseries ->
                farge = if (farge) {
                    WeatherRowLongTerm(timeseries, fontNormal, weatherRow1)
                    false
                } else {
                    WeatherRowLongTerm(timeseries, fontNormal, weatherRow2)
                    true
                }
            }

        }
    }
}


@SuppressLint("DiscouragedApi")
@Composable
fun WeatherRow(data: Timeseries, font: FontFamily, rowColor: Color) {
    val iconName = getWeatherIcon(data)
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

    val windIcon: ImageVector = when (getWindDirection(data)) {
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
            .height(42.dp)
            .background(rowColor),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically) {

        // tidspunkt
        Column(modifier = Modifier
            .weight(0.8f)
            .wrapContentSize() ) {
            Text(
                text = getNorwegianTimeWeather(data),
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
                Modifier.size(30.dp))
        }

        // Temnperatur
        Column(modifier = Modifier
            .weight(1f)
            .wrapContentSize()) {
            Text(
                text = getTemperature(data),
                fontWeight = FontWeight.ExtraBold,
                fontSize = 13.sp,
                fontFamily = font,
                color = if (temperaturePositive(data)) pos else neg )
        }

        // Rain / perciption amount
        Column(modifier = Modifier
            .weight(1.25f)
            .wrapContentSize()) {
            Text(
                text = getPrecipitationAmountMaxMin(data),
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
                    text = getWindSpeed(data),
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

@SuppressLint("DiscouragedApi")
@Composable
fun WeatherRowLongTerm(data: Timeseries, font: FontFamily, rowColor: Color) {
    val iconName = getWeatherIconLongTerm(data)
    val context = LocalContext.current
    val resId = context.resources.getIdentifier(iconName, "drawable", context.packageName)
    val weatherIcon: ImageVector = if (resId != 0) {
        ImageVector.vectorResource(id = resId)
    } else {
        ImageVector.vectorResource(id = R.drawable.fair_day) }

    val pos = Color(159, 8, 8, 255) // Farge til positiv temp som i YR
    val neg = Color(40, 75, 202, 255) // Farge til negativ temp
    val lastHour = (getNorwegianTimeWeather(data).toInt() + 6) % 24
    val lastHourString = if (lastHour < 10) "0$lastHour" else "$lastHour"




    // Rad x
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(42.dp)
            .background(rowColor),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically) {

        // tidspunkt
        Column(modifier = Modifier
            .weight(0.8f)
            .wrapContentSize() ) {
            Text(
                text = getNorwegianTimeWeather(data) + " - $lastHourString",
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
                Modifier.size(30.dp))
        }

        // Temnperatur
        Column(modifier = Modifier
            .weight(1f)
            .wrapContentSize()) {
            Text(
                text = getTemperatureLongTerm(data),
                fontWeight = FontWeight.ExtraBold,
                fontSize = 13.sp,
                fontFamily = font,
                color = if (temperaturePositiveLongTerm(data)) pos else neg )
        }

        // Rain / perciption amount
        Column(modifier = Modifier
            .weight(1.25f)
            .wrapContentSize()) {
            Text(
                text = getPrecipitationAmountMaxMinLongTerm(data),
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                fontFamily = font,
                color = neg )
        }
        Spacer(modifier = Modifier.weight(1.2f) )


    }

}

fun getWeatherIcon(timeseries: Timeseries): String? {
    return timeseries.data.next_1_hours?.summary?.symbol_code
}

fun getWeatherIconLongTerm(timeseries: Timeseries): String? {
    return timeseries.data.next_6_hours.summary.symbol_code
}

fun getWindDirection(timeseries: Timeseries): String {
    val direction = timeseries.data.instant.details.wind_from_direction
    return if (direction != null) getNortEastVestSouthFromDegrees(direction) else " "
}


private fun getNortEastVestSouthFromDegrees(degree: Double): String {
    return when {
        degree >= 337.5 || degree < 22.5 -> "N"
        degree >= 22.5 && degree < 67.5 -> "NØ"
        degree >= 67.5 && degree < 112.5 -> "Ø"
        degree >= 112.5 && degree < 157.5 -> "SØ"
        degree >= 157.5 && degree < 202.5 -> "S"
        degree >= 202.5 && degree < 247.5 -> "SV"
        degree >= 247.5 && degree < 292.5 -> "V"
        degree >= 292.5 -> "NV"
        else -> degree.toString()
    }
}

fun getNorwegianTimeWeather(timeseries: Timeseries): String {
    val timeString = timeseries.time
    val parsedDate = ZonedDateTime.parse(timeString)
    val formats = DateTimeFormatter.ofPattern("HH")
    return parsedDate.withZoneSameInstant(ZoneId.of("Europe/Oslo")).format(formats)
}

fun getTemperature(timeseries: Timeseries): String { // grader er i celsius
    val unit =  "°"
    val temp = (timeseries.data.instant.details.air_temperature)?.roundToInt()
    return "$temp$unit"
}

fun getTemperatureLongTerm(timeseries: Timeseries): String { // grader er i celsius
    val unit =  "°"
    val tempMin = (timeseries.data.next_6_hours.details.air_temperature_min)?.roundToInt()
    val tempMax = (timeseries.data.next_6_hours.details.air_temperature_max)?.roundToInt()
    return "$tempMin - $tempMax$unit"
}
fun getWindSpeed(timeseries: Timeseries): String { //
    val avrSpeed =
        if (timeseries.data.instant.details.wind_speed == 0.0) 0 else (timeseries.data.instant.details.wind_speed)?.roundToInt()
    val highSpeed =
        if (timeseries.data.instant.details.wind_speed_of_gust == 0.0) 0 else (timeseries.data.instant.details.wind_speed_of_gust)?.roundToInt()
    return "$avrSpeed ($highSpeed)"
}


fun getPrecipitationAmountMaxMin(timeseries: Timeseries): String {
    val min = if (timeseries.data.next_1_hours?.details?.precipitation_amount_min == 0.0) 0 else timeseries.data.next_1_hours?.details?.precipitation_amount_min
    val max = if (timeseries.data.next_1_hours?.details?.precipitation_amount_max == 0.0) 0 else timeseries.data.next_1_hours?.details?.precipitation_amount_max
    return if (max == 0) " " else "$min-$max"
}

fun getPrecipitationAmountMaxMinLongTerm(timeseries: Timeseries): String {
    val min = if (timeseries.data.next_6_hours.details.precipitation_amount_min == 0.0) 0 else timeseries.data.next_6_hours.details.precipitation_amount_min
    val max = if (timeseries.data.next_6_hours.details.precipitation_amount_max == 0.0) 0 else timeseries.data.next_6_hours.details.precipitation_amount_max
    return if (max == 0) " " else "$min-$max"
}

fun temperaturePositive(timeseries: Timeseries): Boolean {
    val temp = (timeseries.data.instant.details.air_temperature)?.roundToInt()
    return if (temp != null) {
        temp > 0
    } else {
        true
    }
}

fun temperaturePositiveLongTerm(timeseries: Timeseries): Boolean {
    val temp = (timeseries.data.next_6_hours.details.air_temperature_min)?.roundToInt()
    return if (temp != null) {
        temp > 0
    } else {
        true
    }
}

@Composable
fun DayOceanCard(
    day: LocalDate,
    timeseriesList: List<TimeseriesOcean>,
    fontNormal: FontFamily,
    todayOrTmr: String? = " "
) {
    val expanded = remember { mutableStateOf(Expanded.Short) }
    val oceanRow1 = Color(212, 225, 255, 255)
    val oceanRow2 = Color(190, 210, 254, 255)
    val oceanHeader = Color(105, 131, 187, 232)
    var formattedDay = day.format(DateTimeFormatter.ofPattern("EEEE d. MMMM", Locale("no", "NO")))

    if (todayOrTmr == " ") {
        formattedDay =
            formattedDay.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.titlecase() }
    }

    Column( modifier = if (todayOrTmr == "I dag") Modifier.padding(top = 0.dp) else Modifier.padding(top = 10.dp) ) {
        Row(
            modifier = Modifier
                .height(30.dp)
                .fillMaxWidth()
                .background(color = Color.Transparent)
                .padding(start = 18.dp),
            horizontalArrangement = Arrangement.Start, verticalAlignment = Alignment.Bottom
        ) {
            Text(
                text = if (todayOrTmr == " ") formattedDay else "$todayOrTmr $formattedDay",
                fontFamily = fontNormal,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 14.sp,
                lineHeight = 25.sp
            )
        }

        Card(
            modifier = Modifier
                .padding(start = 8.dp, end = 8.dp)
                .border(width = 1.dp, color = oceanRow2, shape = RoundedCornerShape(8.dp))
                .fillMaxWidth()
        )
        {
            // RAD MED IKON ØVERST Oceanforecast
            OceanHeader(headerColor = oceanHeader, font = fontNormal)

            // LASTER INN RADENE MED HAV-INFO
            when (expanded.value) {
                Expanded.Short -> {
                    OceanRow(data = timeseriesList[0], font = fontNormal, rowColor = oceanRow1)
                    if (timeseriesList.size > 1) {
                        OceanRow(data = timeseriesList[1], font = fontNormal, rowColor = oceanRow2)
                    }
                    if (timeseriesList.size > 2) {
                        ShortToLongButton(expanded, oceanHeader, fontNormal)
                    }
                }

                Expanded.Long -> {
                    var farge = true
                    timeseriesList.forEach { timeseries ->
                        farge = if (farge) {
                            OceanRow(data = timeseries, font = fontNormal, rowColor = oceanRow1)
                            false
                        } else {
                            OceanRow(data = timeseries, font = fontNormal, rowColor = oceanRow2)
                            true
                        }
                    }
                    if (timeseriesList.size > 2) {
                        ShortToLongButton(expanded, oceanHeader, fontNormal)
                    }
                }
            }

        }
    }
}

@Composable
fun OceanRow(data: TimeseriesOcean, font: FontFamily, rowColor: Color) {
    val pos = Color(159, 8, 8, 255) // Farge til positiv temp som i YR
    val neg = Color(40, 75, 202, 255) // Farge til negativ temp
    val arrow  = ImageVector.vectorResource(id = R.drawable.oest)
    val currentFrom = getCurrentDirectionFrom(data)
    val currentTowards = getCurrentDirectionTowards(data)


    // Rad x
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(42.dp)
            .background(rowColor),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically)
    {
        // tidspunkt
        Column(modifier = Modifier
            .weight(0.7f)
            .wrapContentSize()) {
            Text(
                text = getNorwegianTimeOcean(data),
                fontWeight = FontWeight.ExtraBold,
                fontSize = 13.sp,
                fontFamily = font ) }

        // Vann Temnperatur
        Column(modifier = Modifier
            .weight(1f)
            .wrapContentSize() ) {
            Text(
                text = getSeaWaterTemperature(data),
                fontWeight = FontWeight.ExtraBold,
                fontSize = 13.sp,
                fontFamily = font,
                color = if (seaTemperaturePositive(data)) pos else neg
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

            }
        }

        // Current speed
        Column( modifier = Modifier
            .weight(0.8f)
            .wrapContentSize() ) {
            Text(
                text = getCurrentSpeed(data),
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                fontFamily = font ) }
    }

}

fun getNorwegianTimeOcean(timeseries: TimeseriesOcean): String {
    val timeString = timeseries.time
    val parsedDate = ZonedDateTime.parse(timeString)
    val formats = DateTimeFormatter.ofPattern("HH")
    return parsedDate.withZoneSameInstant(ZoneId.of("Europe/Oslo")).format(formats)
}

fun getSeaWaterTemperature(timeseries: TimeseriesOcean): String {
    val unit = "°"
    val temp = (timeseries.data.instant.details.sea_water_temperature)?.roundToInt()
    return "$temp$unit"
}

fun seaTemperaturePositive(timeseries: TimeseriesOcean): Boolean {
    val temp = (timeseries.data.instant.details.sea_water_temperature)?.roundToInt()
    return if (temp != null) {
        temp > 0
    } else {
        true
    }
}

fun getCurrentDirectionTowards(timeseries: TimeseriesOcean): String {
        val direction = timeseries.data.instant.details.sea_water_to_direction
        return if (direction != null) getNortEastVestSouthFromDegrees(direction) else " "
}

fun getCurrentDirectionFrom(timeseries: TimeseriesOcean): String {
        val direction = timeseries.data.instant.details.sea_surface_wave_from_direction
        return if (direction != null) getNortEastVestSouthFromDegrees(direction) else " "
}



fun getCurrentSpeed(timeseries: TimeseriesOcean): String {
    val speed = if (timeseries.data.instant.details.sea_water_speed == 0.0) 0 else timeseries.data.instant.details.sea_water_speed
    return "$speed"
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



@Composable
fun WeatherHeaderLongTerm(headerColor: Color, font: FontFamily) {

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

       Spacer(modifier = Modifier.weight(1.2f))

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
            .fillMaxWidth()
            .padding(bottom = 7.dp)
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
                modifier = Modifier
                    .weight(1f)
                    .padding(4.dp)
                    .fillMaxSize(),
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
                modifier = Modifier
                    .weight(1f)
                    .padding(4.dp),
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
        .height(28.dp)
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


