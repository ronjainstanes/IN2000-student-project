package no.uio.ifi.in2000.team11.havvarselapp.ui.weather

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import no.uio.ifi.in2000.team11.havvarselapp.R
import no.uio.ifi.in2000.team11.havvarselapp.model.locationForecast.Timeseries
import no.uio.ifi.in2000.team11.havvarselapp.model.oceanForecast.TimeseriesOcean
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.roundToInt

/**
 * Gets the symbol_code that determines what icon we use to illustrate the forecast on WeatherScreen for the first (almost) three days
 */
fun getWeatherIcon(timeseries: Timeseries): String? {
    return timeseries.data.next_1_hours?.summary?.symbol_code
}

/**
 * Gets the symbol_code that determines what icon we use to illustrate the forecast on WeatherScreen after the first three days
 */
fun getWeatherIconLongTerm(timeseries: Timeseries): String? {
    return timeseries.data.next_6_hours?.summary?.symbol_code
}

/**
 * Gets the wind direction to display on WeatherScreen first (almost) three days.
 * Convert the degrees to letters: N - equals north, etc
 */
fun getWindDirection(timeseries: Timeseries): String {
    val direction = timeseries.data.instant.details.wind_from_direction
    return if (direction != null) getNorthEastVestSouthFromDegrees(direction) else " "
}

/**
 * This function convert degrees to letters which represent the different cardinal directions.
 */
private fun getNorthEastVestSouthFromDegrees(degree: Double): String {
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

/**
 * Converts the time format 'ISO 8601' to only show the hour in norwegian time, for WeatherScreen
 */
fun getNorwegianTimeWeather(timeseries: Timeseries): String {
    val timeString = timeseries.time
    val parsedDate = ZonedDateTime.parse(timeString)
    val formats = DateTimeFormatter.ofPattern("HH")
    return parsedDate.withZoneSameInstant(ZoneId.of("Europe/Oslo")).format(formats)
}

/**
 * Gets temperature in Celsius
 */
fun getTemperature(timeseries: Timeseries): String {
    val unit = "°"
    val temp = (timeseries.data.instant.details.air_temperature)?.roundToInt()
    return "$temp$unit"
}

/**
 * Gets temperature in Celsius long term
 */
fun getTemperatureLongTerm(timeseries: Timeseries): String {
    val unit = "°"
    val tempMin = (timeseries.data.next_6_hours?.details?.air_temperature_min)?.roundToInt()
    val tempMax = (timeseries.data.next_6_hours?.details?.air_temperature_max)?.roundToInt()
    return "$tempMin - $tempMax$unit"
}

/**
 * Gets wind speed
 */
fun getWindSpeed(timeseries: Timeseries): String { //
    val avrSpeed =
        if (timeseries.data.instant.details.wind_speed == 0.0) 0 else (timeseries.data.instant.details.wind_speed)?.roundToInt()
    val highSpeed =
        if (timeseries.data.instant.details.wind_speed_of_gust == 0.0) 0 else (timeseries.data.instant.details.wind_speed_of_gust)?.roundToInt()
    return "$avrSpeed ($highSpeed)"
}

/**
 * Gets precipitation amount (rain)
 */
fun getPrecipitationAmountMaxMin(timeseries: Timeseries): String {
    val min =
        if (timeseries.data.next_1_hours?.details?.precipitation_amount_min == 0.0) 0 else timeseries.data.next_1_hours?.details?.precipitation_amount_min
    val max =
        if (timeseries.data.next_1_hours?.details?.precipitation_amount_max == 0.0) 0 else timeseries.data.next_1_hours?.details?.precipitation_amount_max
    return if (max == 0) " " else "$min-$max"
}

/**
 * Gets precipitation amount long term (rain)
 */
fun getPrecipitationAmountMaxMinLongTerm(timeseries: Timeseries): String {
    val min =
        if (timeseries.data.next_6_hours?.details?.precipitation_amount_min == 0.0) 0 else timeseries.data.next_6_hours?.details?.precipitation_amount_min
    val max =
        if (timeseries.data.next_6_hours?.details?.precipitation_amount_max == 0.0) 0 else timeseries.data.next_6_hours?.details?.precipitation_amount_max
    return if (max == 0) " " else "$min-$max"
}

/**
 * Determines what color the temperature in the weather tables are shown in. If temperature > 0° --> red, else --> blue
 * short term
 */
fun temperaturePositive(timeseries: Timeseries): Boolean {
    val temp = (timeseries.data.instant.details.air_temperature)?.roundToInt()
    return if (temp != null) {
        temp > 0
    } else {
        true
    }
}

/**
 * Determines what color the temperature in the weather tables are shown in. If temperature > 0° --> red, else --> blue
 * long term
 */
fun temperaturePositiveLongTerm(timeseries: Timeseries): Boolean {
    val temp = (timeseries.data.next_6_hours?.details?.air_temperature_min)?.roundToInt()
    return if (temp != null) {
        temp > 0
    } else {
        true
    }
}

/**
 * Converts the time format 'ISO 8601' to only show the hour in norwegian time, for OceanForecast in WeatherScreen
 */
fun getNorwegianTimeOcean(timeseries: TimeseriesOcean): String {
    val timeString = timeseries.time
    val parsedDate = ZonedDateTime.parse(timeString)
    val formats = DateTimeFormatter.ofPattern("HH")
    return parsedDate.withZoneSameInstant(ZoneId.of("Europe/Oslo")).format(formats)
}

/**
 * Gets water temperature for sea table
 */
fun getSeaWaterTemperature(timeseries: TimeseriesOcean): String {
    val unit = "°"
    val temp = (timeseries.data.instant.details.sea_water_temperature)?.roundToInt()
    return "$temp$unit"
}

/**
 * Determines what color the sea temperature in the ocean tables are shown in. If temperature > 0° --> red, else --> blue
 */
fun seaTemperaturePositive(timeseries: TimeseriesOcean): Boolean {
    val temp = (timeseries.data.instant.details.sea_water_temperature)?.roundToInt()
    return if (temp != null) {
        temp > 0
    } else {
        true
    }
}

/**
 * Gets current direction towards
 */
fun getCurrentDirectionTowards(timeseries: TimeseriesOcean): String {
    val direction = timeseries.data.instant.details.sea_water_to_direction
    return if (direction != null) getNorthEastVestSouthFromDegrees(direction) else " "
}

/**
 * Gets current direction from
 */
fun getCurrentDirectionFrom(timeseries: TimeseriesOcean): String {
    val direction = timeseries.data.instant.details.sea_surface_wave_from_direction
    return if (direction != null) getNorthEastVestSouthFromDegrees(direction) else " "
}

/**
 * Gets current speed
 */
fun getCurrentSpeed(timeseries: TimeseriesOcean): String {
    val speed =
        if (timeseries.data.instant.details.sea_water_speed == 0.0) 0 else timeseries.data.instant.details.sea_water_speed
    return "$speed"
}


/**
 * Groups data for today in a List
 */
fun List<Timeseries>.getToday(): List<Timeseries> {
    val zoneId = ZoneId.of("Europe/Oslo")
    val today = LocalDate.now(zoneId)
    return this.filter { ZonedDateTime.parse(it.time).withZoneSameInstant(zoneId).toLocalDate() == today }
}

/**
 * Shows when the data were updated (at the very bottom of the screen)
 * Converts the time format 'ISO 8601' to norwegian time
 */
fun lastUpdates(today: String): String? {
    val parsedDate = ZonedDateTime.parse(today)
    val formats = DateTimeFormatter.ofPattern("d MMMM yyyy HH:mm",  Locale("no", "NO"))
    return parsedDate.withZoneSameInstant(ZoneId.of("Europe/Oslo")).format(formats)
}

/**
 * Groups weather forecast data day by day
 */
fun List<Timeseries>.groupByDay(): Map<LocalDate, List<Timeseries>> {
    val zoneId = ZoneId.of("Europe/Oslo")
    return this.groupBy {
        ZonedDateTime.parse(it.time).withZoneSameInstant(zoneId).toLocalDate()
    }
}

/**
 * Groups ocean forecast data day by day
 */
fun List<TimeseriesOcean>.groupByDayOcean(): Map<LocalDate, List<TimeseriesOcean>> {
    val zoneId = ZoneId.of("Europe/Oslo")
    return this.groupBy {
        ZonedDateTime.parse(it.time).withZoneSameInstant(zoneId).toLocalDate()
    }
}

/**
 * Finds and displays the right weather image for the screen top.
 *
 * Note: The function has an 'SuppressLint' annotation because 'getIdentifier'
 * is used to get the icon name in a dynamic way instead of static, which
 * causes a warning. But the icon is dependent on the API-data that is always changing.
 */
@SuppressLint("DiscouragedApi")
@Composable
fun GetWeatherIconTopPage(timeseries: Timeseries) {
    val iconName = timeseries.data.next_1_hours?.summary?.symbol_code
    val context = LocalContext.current
    val resId = context.resources.getIdentifier(iconName, "drawable", context.packageName)

    val weatherIcon: ImageVector = if (resId != 0) {
        ImageVector.vectorResource(id = resId)
    } else {
        ImageVector.vectorResource(id = R.drawable.fair_day) }
    Image(imageVector = weatherIcon, contentDescription = "image",
        Modifier.size(110.dp).padding(top = 3.dp, bottom = 10.dp)) }

/**
 * Finds and displays the right weather image for the screen top,
 * when the screen is tilted horizontally.
 *
 * Note: The function has an 'SuppressLint' annotation because 'getIdentifier'
 * is used to get the icon name in a dynamic way instead of static, which
 * causes a warning. But the icon is dependent on the API-data that is always changing.
 */
@SuppressLint("DiscouragedApi")
@Composable
fun GetWeatherIconTopPageHorizontal(timeseries: Timeseries) {
    val iconName = timeseries.data.next_1_hours?.summary?.symbol_code
    val context = LocalContext.current
    val resId = context.resources.getIdentifier(iconName, "drawable", context.packageName)

    val weatherIcon: ImageVector = if (resId != 0) {
        ImageVector.vectorResource(id = resId)
    } else {
        ImageVector.vectorResource(id = R.drawable.fair_day) }
    Image(imageVector = weatherIcon, contentDescription = "image",
        Modifier.size(53.dp)) }

/**
 * Function to get fonts
 */
fun getFonts(): Array<FontFamily> {
    val roboto0 = FontFamily(Font(R.font.robotocondensed_light, FontWeight.W400))
    val roboto1 = FontFamily(Font(R.font.robotocondensed_regular, FontWeight.W400))
    return arrayOf(roboto0, roboto1)
}