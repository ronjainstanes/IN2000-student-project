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

fun getWeatherIcon(timeseries: Timeseries): String? {
    return timeseries.data.next_1_hours?.summary?.symbol_code
}

fun getWeatherIconLongTerm(timeseries: Timeseries): String? {
    return timeseries.data.next_6_hours?.summary?.symbol_code
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
    val unit = "°"
    val temp = (timeseries.data.instant.details.air_temperature)?.roundToInt()
    return "$temp$unit"
}

fun getTemperatureLongTerm(timeseries: Timeseries): String { // grader er i celsius
    val unit = "°"
    val tempMin = (timeseries.data.next_6_hours?.details?.air_temperature_min)?.roundToInt()
    val tempMax = (timeseries.data.next_6_hours?.details?.air_temperature_max)?.roundToInt()
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
    val min =
        if (timeseries.data.next_1_hours?.details?.precipitation_amount_min == 0.0) 0 else timeseries.data.next_1_hours?.details?.precipitation_amount_min
    val max =
        if (timeseries.data.next_1_hours?.details?.precipitation_amount_max == 0.0) 0 else timeseries.data.next_1_hours?.details?.precipitation_amount_max
    return if (max == 0) " " else "$min-$max"
}

fun getPrecipitationAmountMaxMinLongTerm(timeseries: Timeseries): String {
    val min =
        if (timeseries.data.next_6_hours?.details?.precipitation_amount_min == 0.0) 0 else timeseries.data.next_6_hours?.details?.precipitation_amount_min
    val max =
        if (timeseries.data.next_6_hours?.details?.precipitation_amount_max == 0.0) 0 else timeseries.data.next_6_hours?.details?.precipitation_amount_max
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
    val temp = (timeseries.data.next_6_hours?.details?.air_temperature_min)?.roundToInt()
    return if (temp != null) {
        temp > 0
    } else {
        true
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
    val speed =
        if (timeseries.data.instant.details.sea_water_speed == 0.0) 0 else timeseries.data.instant.details.sea_water_speed
    return "$speed"
}



fun List<Timeseries>.getToday(): List<Timeseries> {
    val zoneId = ZoneId.of("Europe/Oslo")
    val today = LocalDate.now(zoneId)
    return this.filter { ZonedDateTime.parse(it.time).withZoneSameInstant(zoneId).toLocalDate() == today }
}
fun LastUpdates(today: String): String? {
    val parsedDate = ZonedDateTime.parse(today)
    val formats = DateTimeFormatter.ofPattern("d MMMM yyyy HH:mm",  Locale("no", "NO"))
    return parsedDate.withZoneSameInstant(ZoneId.of("Europe/Oslo")).format(formats)
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

/**
 * Finds and displays the right weather image for the screen top.
 *
 * Note: The function has an 'SuppressLint' annotation because 'getIdentifier'
 * is used to get the iconname in a dynamic way instead of static, which
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







fun getFonts1(): Array<FontFamily> {
    val barlow1 = FontFamily(Font(R.font.barlow_thin, FontWeight.W400))
    val barlow2 = FontFamily(Font(R.font.barlow_extralight, FontWeight.W400))
    val barlow3 = FontFamily(Font(R.font.barlow_light, FontWeight.W400))
    val barlow4 = FontFamily(Font(R.font.barlow_regular, FontWeight.W400))
    return arrayOf(barlow1, barlow2, barlow3, barlow4)
}

fun getFonts2(): Array<FontFamily> {
    val barlowcondensed1 = FontFamily(Font(R.font.barlowcondensed_thin, FontWeight.W400))
    val barlowcondensed2 = FontFamily(Font(R.font.barlowcondensed_extralight, FontWeight.W400))
    val barlowcondensed3 = FontFamily(Font(R.font.barlowcondensed_light, FontWeight.W400))
    val barlowcondensed4 = FontFamily(Font(R.font.barlowcondensed_regular, FontWeight.W400))
    return arrayOf(barlowcondensed1, barlowcondensed2, barlowcondensed3, barlowcondensed4)
}

fun getFonts3(): Array<FontFamily> {
    val roboto1 = FontFamily(Font(R.font.robotocondensed_thin, FontWeight.W400))
    val roboto2 = FontFamily(Font(R.font.robotocondensed_extralight, FontWeight.W400))
    val roboto3 = FontFamily(Font(R.font.robotocondensed_light, FontWeight.W400))
    val roboto4 = FontFamily(Font(R.font.robotocondensed_regular, FontWeight.W400))
    return arrayOf(roboto1, roboto2, roboto3, roboto4)
}

fun getFonts4(): Array<FontFamily> {
    val natoSansJP = FontFamily(Font(R.font.notosansjp_variablefont_wght, FontWeight.W400))
    val natoSansJPExtralight = FontFamily(Font(R.font.notosansjp_extralight, FontWeight.W400))
    val natoSansJPLight = FontFamily(Font(R.font.notosansjp_light, FontWeight.W400))
    val natoSansJPRegular = FontFamily(Font(R.font.notosansjp_regular, FontWeight.W400))
    val natoSansJPExtrabold = FontFamily(Font(R.font.notosansjp_extrabold, FontWeight.W400))
    return arrayOf(
        natoSansJP,
        natoSansJPExtralight,
        natoSansJPLight,
        natoSansJPRegular,
        natoSansJPExtrabold
    )
}

fun getFonts5(): Array<FontFamily> {
    val poppinsExtralight = FontFamily(Font(R.font.poppins_extralight, FontWeight.W400))
    val poppinsLight = FontFamily(Font(R.font.poppins_light, FontWeight.W400))
    val poppinsRegular = FontFamily(Font(R.font.poppins_regular, FontWeight.W400))
    return arrayOf(poppinsExtralight, poppinsLight, poppinsRegular)
}



