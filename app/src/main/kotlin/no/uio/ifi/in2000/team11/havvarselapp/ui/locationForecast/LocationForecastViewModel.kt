package no.uio.ifi.in2000.team11.havvarselapp.ui.locationForecast

import android.content.Context
import android.location.Geocoder
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.team11.havvarselapp.data.locationForecast.LocationForecastRepositoryImpl
import no.uio.ifi.in2000.team11.havvarselapp.data.oceanForecast.OceanForecastRepositoryImpl
import no.uio.ifi.in2000.team11.havvarselapp.model.locationForecast.LocationForecast
import no.uio.ifi.in2000.team11.havvarselapp.model.oceanForecast.OceanForecast
import java.io.IOException
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.roundToInt


class LocationForecastViewModel(
    private val repository: LocationForecastRepositoryImpl = LocationForecastRepositoryImpl(),
    private  val repositoryOcean: OceanForecastRepositoryImpl = OceanForecastRepositoryImpl()
) : ViewModel() {
    private val _forecastInfoUiState = MutableStateFlow<LocationForecast?>(null)
    val forecastInfoUiState: StateFlow<LocationForecast?> = _forecastInfoUiState.asStateFlow()
    private val _oceanForecastUiState = MutableStateFlow<OceanForecast?>(null)
    val oceanForecastUiState: StateFlow<OceanForecast?> = _oceanForecastUiState.asStateFlow()

    private val _placeNameState = MutableStateFlow<String>("Laster...")
    val placeNameState: StateFlow<String> = _placeNameState.asStateFlow()

    init {
        loadForecast("59.91", "10.75") // starter opp med Latitude og longitude tilsvarende Oslo
    }

    fun loadForecast(lat: String, lon: String) {
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    Log.e("API KALL TEST", "BRA")
                    val forecast = repository.getLocationForecast(lat, lon)
                    _forecastInfoUiState.update { forecast }
                    val oceanForecast = repositoryOcean.getOceanForecast(lat, lon)
                    _oceanForecastUiState.update { oceanForecast }
                    Log.e("VIEWMODEL", "API-kall")


                } catch (e: Exception) {
                    Log.e(
                        "ERROR ForeCast ViewModel",
                        "error in LocationForecastViewModel()loadForecast() ",
                        e
                    )
                }
            }

    }

    /**
     * Funksjon for å reverse geocode koordinatene til stedsnavn, gjorde den så bra som mulig.
     * Google sin geocoder er bedre men den koster penger uansett om man har få API-kall... :(
     */
    fun setCurrentPlaceName(context: Context, lat: Double, lon: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            val geocoder = Geocoder(context, Locale.getDefault())
            var placeName = try {
                val addresses = geocoder.getFromLocation(lat, lon, 1)
                if (addresses?.isNotEmpty() == true) {
                    // Returnerer et formatert adresse navn
                    val city = addresses[0].locality
                    val adminArea = addresses[0].adminArea
                    val subAdminArea = addresses[0].subAdminArea

                    val sublocality = addresses[0].subLocality
                    val country = addresses[0].countryName

                    if (sublocality != null) {
                        "$sublocality, $country"
                    }
                    else if (subAdminArea != null) {
                        "$subAdminArea, $country"
                    }
                    else if (adminArea != null) {
                        "$adminArea, $country"
                    }

                    else if (city != null) {
                        "$city, $country"
                    }

                    else {
                        addresses[0].getAddressLine(0)
                    }

                } else {
                    "Ukjent"
                }
            } catch (e: IOException) {
                e.printStackTrace()
                "Laster..."
            }
            _placeNameState.value = placeName
        }
    }
    fun getPlaceName(): String {
        return  _placeNameState.value
    }



    /**
     * Denne returnerer dato og tid typ: '20 March 2024 16:00' i NORSK TID
     */
    fun convertDateAndTime(time: Int): String {
        val currentForecast = _forecastInfoUiState.value
        val timeString = "${currentForecast?.properties?.timeseries?.get(time)?.time}"
        val parsedDate = ZonedDateTime.parse(timeString)
        val formats = DateTimeFormatter.ofPattern("d MMMM yyyy")
        return parsedDate.withZoneSameInstant(ZoneId.of("Europe/Oslo")).format(formats)
    }


    /**
     * Denne returnerer norsk tid - f.eks '16' brukes øverst på vær-siden - I dag 14-15
     */
    fun getNorwegianTimeWeather(time: Int): String {
        val currentForecast = _forecastInfoUiState.value
        val timeString = "${currentForecast?.properties?.timeseries?.get(time)?.time}"
        val parsedDate = ZonedDateTime.parse(timeString)
        val formats = DateTimeFormatter.ofPattern("HH")
        return parsedDate.withZoneSameInstant(ZoneId.of("Europe/Oslo")).format(formats)
    }

    fun getCordinates(): List<Double>? {
        val currentForecast = _forecastInfoUiState.value
        return currentForecast?.geometry?.coordinates
    }

    fun getTemperature(time: Int): String { // grader er i celsius
        val currentForecast = _forecastInfoUiState.value
        val unit: String? = if (currentForecast?.properties?.meta?.units?.air_temperature == "celsius") "°" else currentForecast?.properties?.meta?.units?.air_temperature
        val temp = (currentForecast?.properties?.timeseries?.get(time)?.data?.instant?.details?.air_temperature)?.roundToInt()
        return "$temp$unit"
    }

    fun temperaturePositive(time: Int): Boolean {
        val currentForecast = _forecastInfoUiState.value
        val temp = (currentForecast?.properties?.timeseries?.get(time)?.data?.instant?.details?.air_temperature)?.roundToInt()
        return if (temp != null) {
            temp > 0
        } else {
            true
        }
    }

    fun getWindSpeed(time: Int): String { // UV-indexen under klare himmelforhold
        val currentForecast = _forecastInfoUiState.value
        val avrSpeed = if (currentForecast?.properties?.timeseries?.get(time)?.data?.instant?.details?.wind_speed == 0.0) 0 else (currentForecast?.properties?.timeseries?.get(time)?.data?.instant?.details?.wind_speed)?.roundToInt()
        val highSpeed = if (currentForecast?.properties?.timeseries?.get(time)?.data?.instant?.details?.wind_speed_of_gust == 0.0) 0 else (currentForecast?.properties?.timeseries?.get(time)?.data?.instant?.details?.wind_speed_of_gust)?.roundToInt()
        return "$avrSpeed ($highSpeed)"
    }

    fun getWindDirection(time: Int): String {
        val currentForecast = _forecastInfoUiState.value
        val direction = currentForecast?.properties?.timeseries?.get(time)?.data?.instant?.details?.wind_from_direction
        return if (direction != null) getNortEastVestSouthFromDegrees(direction) else " "
    }

    fun getUVindex(time: Int): Double? { // UV-indexen under klare himmelforhold
        val currentForecast = _forecastInfoUiState.value
        val uv = if (currentForecast?.properties?.timeseries?.get(time)?.data?.instant?.details?.ultraviolet_index_clear_sky == 0.0) 0 else currentForecast?.properties?.timeseries?.get(time)?.data?.instant?.details?.ultraviolet_index_clear_sky
        return currentForecast?.properties?.timeseries?.get(time)?.data?.instant?.details?.ultraviolet_index_clear_sky
    }

    // bruker ikke denne. Kan gjerne slette den men beholder den i tilfellet vi skulle trenge den senere
    fun getFogAreaFraction(time: Int): String {
        val currentForecast = _forecastInfoUiState.value
        return "${currentForecast?.properties?.timeseries?.get(time)?.data?.instant?.details?.fog_area_fraction} ${currentForecast?.properties?.meta?.units?.fog_area_fraction}"
    }

    // bruker ikke denne. Kan gjerne slette den men beholder den i tilfellet vi skulle trenge den senere
    fun getRelativeHumidity(time: Int): String { //Relativ fuktighet
        val currentForecast = _forecastInfoUiState.value
        return "${currentForecast?.properties?.timeseries?.get(time)?.data?.instant?.details?.relative_humidity} ${currentForecast?.properties?.meta?.units?.relative_humidity}"
    }


    /**
     * Returnerer nedbørsmengde for neste time.
     * F.eks "0.2-0.6" eller "0-0.3", ingenting  om vind er 0.
     */
    fun getPrecipitationAmountMaxMin(time: Int): String{
        val currentForecast = _forecastInfoUiState.value
        val min = if (currentForecast?.properties?.timeseries?.get(time)?.data?.next_1_hours?.details?.precipitation_amount_min == 0.0) 0 else currentForecast?.properties?.timeseries?.get(time)?.data?.next_1_hours?.details?.precipitation_amount_min
        val max = if (currentForecast?.properties?.timeseries?.get(time)?.data?.next_1_hours?.details?.precipitation_amount_max == 0.0) 0 else currentForecast?.properties?.timeseries?.get(time)?.data?.next_1_hours?.details?.precipitation_amount_max
        return if (max == 0) " " else "$min-$max"
    }

    // bruker ikke denne. Kan gjerne slette den men beholder den i tilfellet vi skulle trenge den senere
    fun getCloudAreaFraction(time: Int): String{
        val currentForecast = _forecastInfoUiState.value
        return "${currentForecast?.properties?.timeseries?.get(time)?.data?.instant?.details?.cloud_area_fraction} ${currentForecast?.properties?.meta?.units?.cloud_area_fraction}"
    }

    // bruker ikke denne. Kan gjerne slette den men beholder den i tilfellet vi skulle trenge den senere
    fun getProbabilityOfThunder(time: Int): String{
        val currentForecast = _forecastInfoUiState.value
        return "${currentForecast?.properties?.timeseries?.get(time)?.data?.next_1_hours?.details?.probability_of_thunder} ${currentForecast?.properties?.meta?.units?.probability_of_thunder}"
    }

    fun getWeatherIcon(time: Int) : String {
        val currentForecast = _forecastInfoUiState.value
        return if (currentForecast != null) {
            currentForecast.properties.timeseries[time].data.next_1_hours.summary.symbol_code
        } else {
            "fair_day"
        }
    }

    // bruker ikke denne. Kan gjerne slette den men beholder den i tilfellet vi skulle trenge den senere
    fun probabilityOfPrecipitation12hours(): Double? { // Sannsynlighet for nedbør om 12 timer
        val currentForecast = _forecastInfoUiState.value
        return currentForecast?.properties?.timeseries?.firstOrNull()?.data?.next_12_hours?.details?.probability_of_precipitation
    }


    fun getCoordinatesOcean(): List<Double>? {
        val oceanForecast = _oceanForecastUiState.value
        return  oceanForecast?.geometry?.coordinates
    }


    fun getSeaWaterTemperature(time: Int): String {
        val oceanForecast = _oceanForecastUiState.value
        val unit: String? = if (oceanForecast?.properties?.meta?.units?.sea_water_temperature == "celsius") "°" else oceanForecast?.properties?.meta?.units?.sea_water_temperature
        val temp = (oceanForecast?.properties?.timeseries?.get(time)?.data?.instant?.details?.sea_water_temperature)?.roundToInt()
        return "$temp$unit"
    }
    fun seaTemperaturePositive(time: Int): Boolean {
        val oceanForecast = _oceanForecastUiState.value
        val temp = (oceanForecast?.properties?.timeseries?.get(time)?.data?.instant?.details?.sea_water_temperature)?.roundToInt()
        return if (temp != null) {
            temp > 0
        } else {
            true
        }
    }

    fun getCurrentSpeed(time: Int): String {
        val oceanForecast = _oceanForecastUiState.value
        val speed = if (oceanForecast?.properties?.timeseries?.get(time)?.data?.instant?.details?.sea_water_speed == 0.0) 0 else oceanForecast?.properties?.timeseries?.get(time)?.data?.instant?.details?.sea_water_speed
        return "$speed"
    }

    fun getCurrentDirectionTowards(time: Int): String {
        val oceanForecast = _oceanForecastUiState.value
        val direction = oceanForecast?.properties?.timeseries?.get(time)?.data?.instant?.details?.sea_water_to_direction
        return  if (direction != null) getNortEastVestSouthFromDegrees(direction) else " "
    }

    fun getCurrentDirectionFrom(time: Int): String {
        val oceanForecast = _oceanForecastUiState.value
        val direction = oceanForecast?.properties?.timeseries?.get(time)?.data?.instant?.details?.sea_surface_wave_from_direction
        return  if (direction != null) getNortEastVestSouthFromDegrees(direction) else " "
    }

    // bruker ikke denne. Kan gjerne slette den men beholder den i tilfellet vi skulle trenge den senere
    fun getSeaWaveHeight(time: Int): String {
        val oceanForecast = _oceanForecastUiState.value
        val unit: String? = if (oceanForecast?.properties?.meta?.units?.sea_surface_wave_height == "meter") "m" else oceanForecast?.properties?.meta?.units?.sea_surface_wave_height
        val height = if (oceanForecast?.properties?.timeseries?.get(time)?.data?.instant?.details?.sea_surface_wave_height == 0.0) 0 else oceanForecast?.properties?.timeseries?.get(time)?.data?.instant?.details?.sea_surface_wave_height
        return "${oceanForecast?.properties?.timeseries?.get(time)?.data?.instant?.details?.sea_surface_wave_height} $unit"
    }


    /**
     *  Nord (N):       fra 337.5° til 22.5°.
     *  Nord-øst (NE):  fra 22.5° til 67.5°.
     *  Øst (E):        fra 67.5° til 112.5°.
     *  Sør-øst (SE)    fra 112.5° til 157.5°.
     *  Sør (S):        fra 157.5° til 202.5°.
     *  Sør-vest (SW):  fra 202.5° til 247.5°.
     *  Vest (W):       fra 247.5° til 292.5°.
     *  Nord-vest(NW):  fra 292.5° til 337.5°.
     */
    private fun getNortEastVestSouthFromDegrees(degree: Double): String {
        return when  {
            degree >= 337.5 || degree < 22.5   -> "N"
            degree >= 22.5 && degree < 67.5    -> "NØ"
            degree >= 67.5 && degree < 112.5   -> "Ø"
            degree >= 112.5 && degree < 157.5  -> "SØ"
            degree >= 157.5 && degree < 202.5  -> "S"
            degree >= 202.5 && degree < 247.5  -> "SV"
            degree >= 247.5 && degree < 292.5  -> "V"
            degree >= 292.5                    -> "NV"
            else                               -> degree.toString()
        }
    }

    /**
     * Denne returnerer norsk tid - f.eks '16:00'
     */
    fun getNorwegianTimeOcean(time: Int): String {
        val currentForecast = _oceanForecastUiState.value
        val timeString = "${currentForecast?.properties?.timeseries?.get(time)?.time}"
        val parsedDate = ZonedDateTime.parse(timeString)
        val formats = DateTimeFormatter.ofPattern("HH")
        return parsedDate.withZoneSameInstant(ZoneId.of("Europe/Oslo")).format(formats)
    }

    fun locationForecastLastUpdated(): String? {
        val currentForecast = _forecastInfoUiState.value
        val timeString: String = currentForecast?.properties?.meta?.updated_at ?: " "
        val parsedDate = ZonedDateTime.parse(timeString)
        val formats = DateTimeFormatter.ofPattern("d MMMM yyyy HH:mm")
        return parsedDate.withZoneSameInstant(ZoneId.of("Europe/Oslo")).format(formats)
    }

    fun oceanForecastLastUpdated(): String? {
        val currentForecast = _oceanForecastUiState.value
        val timeString: String = currentForecast?.properties?.meta?.updated_at ?: " "
        val parsedDate = ZonedDateTime.parse(timeString)
        val formats = DateTimeFormatter.ofPattern("d MMMM yyyy HH:mm")
        return parsedDate.withZoneSameInstant(ZoneId.of("Europe/Oslo")).format(formats)
    }
}
