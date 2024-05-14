package no.uio.ifi.in2000.team11.havvarselapp.ui.weather

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import no.uio.ifi.in2000.team11.havvarselapp.R
import no.uio.ifi.in2000.team11.havvarselapp.SharedUiState
import no.uio.ifi.in2000.team11.havvarselapp.model.locationForecast.Timeseries
import no.uio.ifi.in2000.team11.havvarselapp.model.oceanForecast.TimeseriesOcean
import no.uio.ifi.in2000.team11.havvarselapp.ui.locationForecast.LocationForecastViewModel
import no.uio.ifi.in2000.team11.havvarselapp.ui.navigation.NavigationBarWithButtons
import no.uio.ifi.in2000.team11.havvarselapp.ui.networkConnection.ConnectivityObserver
import no.uio.ifi.in2000.team11.havvarselapp.ui.networkConnection.NetworkConnectionStatus
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * To know if the user has chosen to display "weather" or "sea" info on screen
 */
enum class DisplayInfo {
    Weather, Sea
}

/**
 * To know if the user has clicked on "expand" to display more rows of data
 */
enum class Expanded {
    Long, Short
}

/**
 * Screen containing weather and ocean forecast
 */
@Composable
fun WeatherScreen(
    sharedUiState: SharedUiState,
    navController: NavController,
    forecastViewModel: LocationForecastViewModel = viewModel(),
    connectivityObserver: ConnectivityObserver
) {
    val displayInfo =
        remember { mutableStateOf(DisplayInfo.Weather) }
    val context = LocalContext.current


    val status by connectivityObserver.observe().collectAsState(
        initial = ConnectivityObserver.Status.Unavailable
    )


    // Using LaunchedEffect to load weather data when the position changes via search/pin
    LaunchedEffect(sharedUiState.currentLocation.hashCode(), status) {
        forecastViewModel.loadForecast(
                sharedUiState.currentLocation.latitude.toString(),
                sharedUiState.currentLocation.longitude.toString(),
                context
            )
            // Placename is updated when the location changes
            forecastViewModel.setCurrentPlaceName(
                context,
                sharedUiState.currentLocation.latitude,
                sharedUiState.currentLocation.longitude
            )

    }

    val fonts = getFonts()

    //To change the font of everything, just modify this variable!
    val fontNormal = fonts[0]
    val fontBold = fonts[1]



    Box(modifier = Modifier
        .fillMaxSize(),
        contentAlignment = Alignment.Center) {

        Column(modifier = Modifier.fillMaxSize()) {
            // buttons to change between weather and sea forecast, at the bottom of the screen
            Scaffold(modifier = Modifier.weight(1f),
                bottomBar = { BottomNavBar(currentScreen = displayInfo, fontNormal) }
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(innerPadding)
                        .background(Color(230, 240, 255))

                ) {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(2.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                )
                {

                    // this is the top part of the screen, above the weather-rows
                    ScreenTop(
                        forecastViewModel = forecastViewModel,
                        fontNormal = fontNormal,
                        fontBold = fontBold
                    )
                        // the content of the screen, which contain the cards for the weather and ocean forecast
                        ScreenContent(
                            forecastViewModel = forecastViewModel,
                            displayInfo = displayInfo,
                            fontNormal = fontNormal
                        )
                    }
                }
            }
            NavigationBarWithButtons(navController = navController)
        }
        // A pop-up window will appear if the app loses internet connection
        NetworkConnectionStatus(connectivityObserver,null)
    }
}

/**
 * The main part of the screen, containing rows (cards) with weather data
 */
@Composable
fun ScreenContent(
    forecastViewModel: LocationForecastViewModel,
    displayInfo: MutableState<DisplayInfo>,
    fontNormal: FontFamily
) {
    val locationForecastUiState by forecastViewModel.forecastInfoUiState.collectAsState()
    val oceanForecastUiState by forecastViewModel.oceanForecastUiState.collectAsState()
    val forecastGroupedDayByDay = locationForecastUiState?.properties?.timeseries?.groupByDay()
    val oceanGroupedDayByDay = oceanForecastUiState?.properties?.timeseries?.groupByDayOcean()

    when (displayInfo.value) {
        DisplayInfo.Weather -> {
            if (!forecastGroupedDayByDay.isNullOrEmpty()) {
                LazyColumn {
                    var todayOrTomorrow = 0
                    forecastGroupedDayByDay.entries.take(3).forEach { (day, timeseriesList) ->
                        when (todayOrTomorrow) {
                            0 -> item {
                                DayWeatherCard(
                                    day = day,
                                    timeseriesList = timeseriesList,
                                    fontNormal = fontNormal,
                                    todayOrTmr = "I dag"
                                )
                            }
                            1 -> {
                                item {
                                    DayWeatherCard(
                                        day = day,
                                        timeseriesList = timeseriesList,
                                        fontNormal = fontNormal,
                                        todayOrTmr = "I morgen"
                                    )
                                }
                            }
                            else -> {
                                item {
                                    DayWeatherCard(
                                        day = day,
                                        timeseriesList = timeseriesList,
                                        fontNormal = fontNormal
                                    )
                                }
                            }
                        }
                        todayOrTomorrow++
                    }
                    forecastGroupedDayByDay.entries.drop(3).take(6)
                        .forEach { (day, timeseriesList) ->
                            item {
                                DayWeatherCardLongTerm(
                                    day = day,
                                    timeseriesList = timeseriesList,
                                    fontNormal = fontNormal
                                )
                            }
                        }
                    item {
                        DataLastUpdated(
                            forecastViewModel = forecastViewModel,
                            displayInfo = displayInfo,
                            fontNormal = fontNormal
                        )
                    }
                }
            }


        }
        // End of the weather-screen


        // OCEAN-SCREEN
        DisplayInfo.Sea -> {
            if (!oceanGroupedDayByDay.isNullOrEmpty()) {
                LazyColumn {
                    var todayOrTommorow = 0
                    oceanGroupedDayByDay.entries.take(2).forEach { (day, timeseriesOceanList) ->
                        if (todayOrTommorow == 0) {
                            item {
                                DayOceanCard(
                                    day = day,
                                    timeseriesList = timeseriesOceanList,
                                    fontNormal = fontNormal,
                                    todayOrTmr = "I dag"
                                )
                            }

                        } else if (todayOrTommorow == 1) {
                            item {
                                DayOceanCard(
                                    day = day,
                                    timeseriesList = timeseriesOceanList,
                                    fontNormal = fontNormal,
                                    todayOrTmr = "I morgen"
                                )
                            }
                        }
                        todayOrTommorow++
                    }
                    oceanGroupedDayByDay.entries.drop(2).forEach { (day, timeseriesOceanList) ->
                        item {
                            DayOceanCard(
                                day = day,
                                timeseriesList = timeseriesOceanList,
                                fontNormal = fontNormal
                            )
                        }
                    }
                    item {
                        DataLastUpdated(
                            forecastViewModel = forecastViewModel,
                            displayInfo = displayInfo,
                            fontNormal = fontNormal
                        )
                    }
                }
            }
        }
        // End og the ocean-screen
    }
}

/**
 * A card containing multiple rows with weather info, for the first three days
 */
@Composable
fun DayWeatherCard(
    day: LocalDate,
    timeseriesList: List<Timeseries>,
    fontNormal: FontFamily,
    todayOrTmr: String? = " "
) {
    val expanded = remember { mutableStateOf(Expanded.Short) }
    val weatherRow1 = Color(204, 241, 214)
    val weatherRow2 = Color(153, 222, 173)
    val weatherHeader = Color(65, 95, 78)
    var formattedDay = day.format(DateTimeFormatter.ofPattern("EEEE d. MMMM", Locale("no", "NO")))
    if (todayOrTmr == " ") {
        formattedDay =
            formattedDay.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.titlecase() }
    }

    Column(
        modifier = if (todayOrTmr == "I dag") Modifier.padding(top = 0.dp) else Modifier.padding(top = 10.dp)
    ) {

        Row(
            modifier =
            if (todayOrTmr == "I dag") Modifier
                .height(23.dp)
                .fillMaxWidth()
                .background(color = Color.Transparent)
                .padding(start = 18.dp)
            else Modifier
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
                fontSize = 15.sp,
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
            // ROW WITH ICON AT TOP
            WeatherHeader(headerColor = weatherHeader, font = fontNormal, shortTerm = true)
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

/**
 *  A card containing multiple rows with weather info, after the first three days
 */
@Composable
fun DayWeatherCardLongTerm(
    day: LocalDate,
    timeseriesList: List<Timeseries>,
    fontNormal: FontFamily
) {
    val weatherRow1 = Color(204, 241, 214)
    val weatherRow2 = Color(153, 222, 173)
    val weatherHeader = Color(65, 95, 78)
    var formattedDay = day.format(DateTimeFormatter.ofPattern("EEEE d. MMMM", Locale("no", "NO")))
    formattedDay =
        formattedDay.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.titlecase() }

    Column(modifier = Modifier.padding(top = 10.dp)) {
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
                fontSize = 15.sp,
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
            // ROW WITH ICON AT TOP
            WeatherHeader(headerColor = weatherHeader, font = fontNormal, shortTerm = false)
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

/**
 * A row displaying the weather in the upcoming days.
 *
 * Note: The function has an 'SuppressLint' annotation because 'getIdentifier'
 * is used to get the iconname in a dynamic way instead of static, which
 * causes a warning. But the icon is dependent on the API-data that is always changing.
 */
@SuppressLint("DiscouragedApi")
@Composable
fun WeatherRow(data: Timeseries, font: FontFamily, rowColor: Color) {
    val iconName = getWeatherIcon(data)
    val context = LocalContext.current
    val resId = context.resources.getIdentifier(iconName, "drawable", context.packageName)
    val weatherIcon: ImageVector = if (resId != 0) {
        ImageVector.vectorResource(id = resId)
    } else {
        ImageVector.vectorResource(id = R.drawable.fair_day)
    }

    val pos = Color(159, 8, 8, 255) // Color for positive temperatures, 0 degrees or more
    val neg = Color(40, 75, 202, 255) // Color for negative temperatures

    val north = ImageVector.vectorResource(id = R.drawable.north)
    val south = ImageVector.vectorResource(id = R.drawable.south)
    val west = ImageVector.vectorResource(id = R.drawable.west)
    val east = ImageVector.vectorResource(id = R.drawable.oest)
    val northWest = ImageVector.vectorResource(id = R.drawable.northwest)
    val northEast = ImageVector.vectorResource(id = R.drawable.northeast)
    val southWest = ImageVector.vectorResource(id = R.drawable.southwest)
    val southEast = ImageVector.vectorResource(id = R.drawable.southeast)

    val windIcon: ImageVector = when (getWindDirection(data)) {
        "N" -> north
        "NØ" -> northEast
        "Ø" -> east
        "SØ" -> southEast
        "S" -> south
        "SV" -> southWest
        "V" -> west
        "NV" -> northWest
        else -> north
    }


    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(42.dp)
            .background(rowColor),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {

        // time
        Column(
            modifier = Modifier
                .weight(0.8f)
                .wrapContentSize()
        ) {
            Text(
                text = getNorwegianTimeWeather(data),
                fontWeight = FontWeight.ExtraBold,
                fontSize = 14.sp,
                fontFamily = font
            )
        }


        // Icon for weather
        Column(
            modifier = Modifier
                .weight(0.8f)
                .wrapContentSize()
        ) {
            Image(
                imageVector = weatherIcon, contentDescription = "image",
                Modifier.size(30.dp)
            )
        }

        // Temperature
        Column(
            modifier = Modifier
                .weight(1f)
                .wrapContentSize()
        ) {
            Text(
                text = getTemperature(data),
                fontWeight = FontWeight.ExtraBold,
                fontSize = 14.sp,
                fontFamily = font,
                color = if (temperaturePositive(data)) pos else neg
            )
        }

        // Rain / precipitation amount
        Column(
            modifier = Modifier
                .weight(1.25f)
                .wrapContentSize()
        ) {
            Text(
                text = getPrecipitationAmountMaxMin(data),
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                fontFamily = font,
                color = neg
            )
        }

        // Wind-speed
        Column(
            modifier = Modifier
                .weight(1.2f)
                .wrapContentSize()
        ) {
            Row {
                Text(
                    text = getWindSpeed(data),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    fontFamily = font
                )
                Image(
                    imageVector = windIcon, contentDescription = "image",
                    Modifier.size(15.dp)
                )
            }
        }
    }
}

/**
 * A row displaying the weather multiple days ahead where there is less data
 * from the API compared to the first days.
 *
 * Note: The function has an 'SuppressLint' annotation because 'getIdentifier'
 * is used to get the iconname in a dynamic way instead of static, which
 * causes a warning. But the icon is dependent on the API-data that is always changing.
 */
@SuppressLint("DiscouragedApi")
@Composable
fun WeatherRowLongTerm(data: Timeseries, font: FontFamily, rowColor: Color) {
    val iconName = getWeatherIconLongTerm(data)
    val resId = if (!iconName.isNullOrBlank()) {
        val context = LocalContext.current
        context.resources.getIdentifier(iconName, "drawable", context.packageName)
    } else {
        0
    }
    val weatherIcon: ImageVector = if (resId != 0) {
        ImageVector.vectorResource(id = resId)
    } else {
        ImageVector.vectorResource(id = R.drawable.fair_day)
    }

    val pos = Color(159, 8, 8, 255) // Color for positive temperatures, 0 and above
    val neg = Color(40, 75, 202, 255) // Color for negative temperatures
    val lastHour = (getNorwegianTimeWeather(data).toInt() + 6) % 24
    val lastHourString = if (lastHour < 10) "0$lastHour" else "$lastHour"


    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(42.dp)
            .background(rowColor),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {

        // time
        Column(
            modifier = Modifier
                .weight(0.8f)
                .wrapContentSize()
        ) {
            Text(
                text = getNorwegianTimeWeather(data) + " - $lastHourString",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 14.sp,
                fontFamily = font
            )
        }


        // Icon for weather
        Column(
            modifier = Modifier
                .weight(0.8f)
                .wrapContentSize()
        ) {
            Image(
                imageVector = weatherIcon, contentDescription = "image",
                Modifier.size(30.dp)
            )
        }

        // Temperature
        Column(
            modifier = Modifier
                .weight(1f)
                .wrapContentSize()
        ) {
            Text(
                text = getTemperatureLongTerm(data),
                fontWeight = FontWeight.ExtraBold,
                fontSize = 14.sp,
                fontFamily = font,
                color = if (temperaturePositiveLongTerm(data)) pos else neg
            )
        }

        // Rain / precipitation amount
        Column(
            modifier = Modifier
                .weight(1.25f)
                .wrapContentSize()
        ) {
            Text(
                text = getPrecipitationAmountMaxMinLongTerm(data),
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                fontFamily = font,
                color = neg
            )
        }
        Spacer(modifier = Modifier.weight(1.2f))
    }
}


/**
 * The header for each card containing weather info
 */
@Composable
fun WeatherHeader(headerColor: Color, font: FontFamily, shortTerm: Boolean) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(30.dp)
            .background(headerColor),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    )
    {

        // Time
        Column(
            modifier = Modifier
                .weight(0.8f)
                .wrapContentSize()
        ) {
            Text(
                text = "Tid ",
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                fontSize = 12.sp,
                fontFamily = font
            )
        }

        // Weather icon
        Column(
            modifier = Modifier
                .weight(0.8f)
                .wrapContentSize()
        ) {
            Text(
                text = "Vær",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 12.sp,
                fontFamily = font,
                color = Color.White,
            )
        }

        // Temperature
        Column(
            modifier = Modifier
                .weight(1f)
                .wrapContentSize()
        ) {
            Text(
                text = "Temp. °C",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 12.sp,
                fontFamily = font,
                color = Color.White,
            )
        }


        // Rain / perciption amount
        Column(
            modifier = Modifier
                .weight(1.25f)
                .wrapContentSize()
        ) {
            Text(
                text = "Nedbør mm",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 12.sp,
                fontFamily = font,
                color = Color.White,
            )
        }

        if (shortTerm) {
            // Wind
            Column(
                modifier = Modifier
                    .weight(1.2f)
                    .wrapContentSize()
            ) {
                Text(
                    text = "Vind(kast) m/s",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 12.sp,
                    fontFamily = font,
                    color = Color.White,
                )
            }
        }
        else {
            Spacer(modifier = Modifier.weight(1.2f))
        }
    }
}

/**
 *  A card for the ocean page, containing multiple rows with ocean data,
 */
@Composable
fun DayOceanCard(
    day: LocalDate,
    timeseriesList: List<TimeseriesOcean>,
    fontNormal: FontFamily,
    todayOrTmr: String? = " "
) {
    val expanded = remember { mutableStateOf(Expanded.Short) }
    val oceanRow1 = Color(204, 225, 255)
    val oceanRow2 = Color(154, 195, 255)
    val oceanHeader = Color(0, 86, 179)
    var formattedDay = day.format(DateTimeFormatter.ofPattern("EEEE d. MMMM", Locale("no", "NO")))

    if (todayOrTmr == " ") {
        formattedDay =
            formattedDay.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.titlecase() }
    }

    Column(
        modifier = if (todayOrTmr == "I dag") Modifier.padding(top = 0.dp) else Modifier.padding(
            top = 10.dp
        )
    ) {
        Row(
            modifier =
            if (todayOrTmr == "I dag") Modifier
                .height(23.dp)
                .fillMaxWidth()
                .background(color = Color.Transparent)
                .padding(start = 18.dp)
            else Modifier
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
                fontSize = 15.sp,
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
            // ROW WITH ICON AT TOP Oceanforecast
            OceanHeader(headerColor = oceanHeader, font = fontNormal)

            // LOADING THE ROWS WITH OCEAN INFO
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


/**
 *  A row displaying the data for a specific time for the ocean card
 */
@Composable
fun OceanRow(data: TimeseriesOcean, font: FontFamily, rowColor: Color) {
    val pos = Color(159, 8, 8, 255) // Red color for positive temperatures, 0 and above
    val neg = Color(40, 75, 202, 255) // Blue color for negative temperatures
    val arrow = ImageVector.vectorResource(id = R.drawable.oest)
    val currentFrom = getCurrentDirectionFrom(data)
    val currentTowards = getCurrentDirectionTowards(data)


    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(42.dp)
            .background(rowColor),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    )
    {
        // time
        Column(
            modifier = Modifier
                .weight(0.7f)
                .wrapContentSize()
        ) {
            Text(
                text = getNorwegianTimeOcean(data),
                fontWeight = FontWeight.ExtraBold,
                fontSize = 14.sp,
                fontFamily = font
            )
        }

        // Water temperature
        Column(
            modifier = Modifier
                .weight(1f)
                .wrapContentSize()
        ) {
            Text(
                text = getSeaWaterTemperature(data),
                fontWeight = FontWeight.ExtraBold,
                fontSize = 14.sp,
                fontFamily = font,
                color = if (seaTemperaturePositive(data)) pos else neg
            )
        }


        // Current from
        Column(
            modifier = Modifier
                .weight(1f)
                .wrapContentSize()
        ) {
            Row {
                Text(
                    text = "$currentFrom ",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 12.sp,
                    fontFamily = font
                )

                Image(
                    imageVector = arrow, contentDescription = "image",
                    Modifier
                        .size(15.dp)
                        .align(Alignment.CenterVertically)
                )
                Text(
                    text = " $currentTowards",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 12.sp,
                    fontFamily = font
                )
            }
        }

        // Current speed
        Column(
            modifier = Modifier
                .weight(0.8f)
                .wrapContentSize()
        ) {
            Text(
                text = getCurrentSpeed(data),
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                fontFamily = font
            )
        }
    }
}

/**
 * The header for each card containing ocean data
 */
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

        // Time icon
        Column(
            modifier = Modifier
                .weight(0.7f)
                .wrapContentSize()
        ) {
            Text(
                text = "Tid ",
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                fontSize = 12.sp,
                fontFamily = font
            )
        }

        // Water temperature
        Column(
            modifier = Modifier
                .weight(1f)
                .wrapContentSize()
        ) {
            Text(
                text = "Vanntemp. °C",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 12.sp,
                fontFamily = font,
                color = Color.White,
            )
        }

        // Current direction
        Column(
            modifier = Modifier
                .weight(1f)
                .wrapContentSize()
        ) {
            Text(
                text = "Strømretning",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 12.sp,
                fontFamily = font,
                color = Color.White,
            )
        }

        // Current speed
        Column(
            modifier = Modifier
                .weight(0.8f)
                .wrapContentSize()
        ) {
            Text(
                text = "Strøm m/s",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 12.sp,
                fontFamily = font,
                color = Color.White,
            )
        }
    }
}


/**
 * The top of the screen on the weather and ocean page
 */
@Composable
fun ScreenTop(forecastViewModel: LocationForecastViewModel, fontNormal: FontFamily, fontBold: FontFamily) {
    val placeNameUiState by forecastViewModel.placeNameState.collectAsState()
    val locationForecastUiState by forecastViewModel.forecastInfoUiState.collectAsState()
    val today = locationForecastUiState?.properties?.timeseries?.getToday()

    val configuration = LocalConfiguration.current

    // Screen in laying mode
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    Row {
        if (isLandscape) {
            Spacer(modifier = Modifier.weight(1.2f))
        }
            Text(
        text = placeNameUiState,
        textAlign = TextAlign.Center,
        fontSize = 18.sp,
        lineHeight = 18.sp,
        fontFamily = fontNormal,
        fontWeight = FontWeight.ExtraBold,
        modifier = if (isLandscape) Modifier
            .padding(top = 10.dp)
            .weight(0.6f) else Modifier.padding(top = 10.dp)
    )
    if (isLandscape) {
            Box(
                modifier = Modifier
                    .weight(1.2f).padding(top = 5.dp)
            ) {
                Row {
                if (today != null) {
                    Text(
                        modifier = Modifier.padding(start = 35.dp, top = 15.dp, end = 20.dp),
                        text = "I dag kl. " + getNorwegianTimeWeather(today[0]) + "-" + getNorwegianTimeWeather(
                            today[1]
                        ),
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 14.sp,
                        lineHeight = 14.sp,
                        fontFamily = fontBold
                    )

                    GetWeatherIconTopPageHorizontal(timeseries = today[0])
                    Text(
                        modifier = Modifier.padding(start = 15.dp, top = 10.dp),
                        text = getTemperature(today[0]),
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 22.sp,
                        lineHeight = 22.sp,
                        fontFamily = fontBold
                    )

                }
            }
        }
    }
}


    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    )
    {
        if (forecastViewModel.forecastInfoUiState.collectAsState().value != null) {

            if (!isLandscape) {
                Column(modifier = Modifier.weight(1f)) {
                    if (today != null) {
                        Row(
                            modifier = Modifier
                                .align(Alignment.Start)
                                .padding(start = 26.dp, top = 5.dp)
                        ) {

                            Text(
                                text = "I dag kl. " + getNorwegianTimeWeather(today[0]) + "-" + getNorwegianTimeWeather(
                                    today[1]
                                ),
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 14.sp,
                                fontFamily = fontBold
                            )
                        }

                    }

                    Row(
                        Modifier
                            .align(Alignment.Start)
                            .padding(start = 40.dp)
                    ) {
                        if (today != null) {
                            Text(
                                text = getTemperature(today[0]),
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 25.sp,
                                fontFamily = fontBold
                            )
                        }
                    }
                }
            }

            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            )
            {
                if (today != null && (!isLandscape)) {
                    GetWeatherIconTopPage(timeseries = today[0])
                }
            }
            if (!isLandscape) {

                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}




/**
 * The bottom navigation bar to navigate between the weather and ocean data
 */
@Composable
fun BottomNavBar(currentScreen: MutableState<DisplayInfo>, font: FontFamily) {
    val pos = Color(19, 35, 44, 255) // Color of the navigation button, when it is active
    val neg = Color(155, 163, 174, 255) // Color of button when it is inactive

    val weatherColor = if (currentScreen.value == DisplayInfo.Weather) pos else neg
    val oceanColor = if (currentScreen.value == DisplayInfo.Sea) pos else neg

    Box(
        modifier =  Modifier
            .fillMaxWidth()
            .background(Color(230, 240, 255))
            .padding(top = 7.dp, bottom = 7.dp)
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
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    text = "Vær",
                    modifier = Modifier.fillMaxHeight(),
                    color = if (currentScreen.value == DisplayInfo.Weather) Color.White else Color.Black,
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
                    color = if (currentScreen.value == DisplayInfo.Sea) Color.White else Color.Black,
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
 * The button which expand and shorten the number of rows which are shown in a card
 */
@Composable
fun ShortToLongButton(expanded: MutableState<Expanded>, color: Color, font: FontFamily) {
    val pilopp = ImageVector.vectorResource(id = R.drawable.p1honsftvsnih1nss1kofsciqo4_page_145)
    val pilned = ImageVector.vectorResource(id = R.drawable.p1honsftvsnih1nss1kofsciqo4_page_146)
    Row(
        modifier = Modifier
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
                        fontSize = 12.sp,
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
                            .padding(top = 0.dp)
                    )
                }
            }

            Expanded.Long -> {
                Button(
                    onClick = { expanded.value = Expanded.Short },
                    modifier = Modifier.fillMaxSize(),
                    colors = ButtonDefaults.buttonColors(color)
                )
                {
                    Text(
                        text = "Time for time ",
                        textAlign = TextAlign.Center,
                        fontSize = 12.sp,
                        lineHeight = 11.sp,
                        fontFamily = font,
                        fontWeight = FontWeight.Bold,
                    )
                    Image(
                        imageVector = pilopp,
                        contentDescription = "pil",
                        Modifier.size(25.dp)
                    )
                }
            }
        }
    }
}

/**
 * Shows shen the data for the weather and ocean forecast were last updated on the bottom of the page
 */
@Composable
fun DataLastUpdated(forecastViewModel: LocationForecastViewModel, displayInfo: MutableState<DisplayInfo>, fontNormal: FontFamily) {
    val locationForecastUiState by forecastViewModel.forecastInfoUiState.collectAsState()
    val oceanForecastUiState by forecastViewModel.oceanForecastUiState.collectAsState()


    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 20.dp, bottom = 40.dp)
            .background(Color(230, 240, 255)) )
    {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
                .align(Alignment.Center),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically)
        {
            val updateText = when (displayInfo.value) {

                DisplayInfo.Weather -> {
                            "Vær data sist oppdatert:\n" +
                            "${locationForecastUiState?.properties?.meta?.updated_at?.let { lastUpdates(it) }}"
                }

                DisplayInfo.Sea -> {
                             "Hav data sist oppdatert:\n" +
                            "${oceanForecastUiState?.properties?.meta?.updated_at?.let { lastUpdates(it) }}"
                }
            }

            Text(
                text = updateText,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 15.sp,
                color = Color(115, 115, 115, 255),
                fontFamily = fontNormal,
                textAlign = TextAlign.Center
            )
        }
    }
}



