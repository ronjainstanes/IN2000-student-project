package no.uio.ifi.in2000.team11.havvarselapp.model.locationForecast

// locationForecast.properties.timeseries.data.next_1_hours.summary
/**
 *
 *      Si at objektet heter: 'val forecast'
 *
 *                                      For å hente NÅVÆRENDE vær-data:
 *
 *      Temperatur:             forecast?.properties?.timeseries?.firstOrNull()?.data?.instant?.details?.air_temperature
 *      Temperatur unit:        forecast?.properties?.meta?.units?.air_temperature
 *
 *
 *      UV-index:               forecast?.properties?.timeseries?.firstOrNull()?.data?.instant?.details?.ultraviolet_index_clear_sky
 *      UV-index unit:          forecast?.properties?.meta?.units?.ultraviolet_index_clear_sky
 *
 *
 *      Wind-speed:               forecast?.properties?.timeseries?.firstOrNull()?.data?.instant?.details?.wind_speed
 *      Wind-speed unit:          forecast?.properties?.meta?.units?.wind_speed
 *
 *
 *      Wind-direction:               forecast?.properties?.timeseries?.firstOrNull()?.data?.instant?.details?.wind_from_direction
 *      Wind-direction unit:          forecast?.properties?.meta?.units?.wind_from_direction
 *      -   Forklaring Wind-direction:  0° (north), 90° (east), 180° (south), 270° (west)
 *
 *
 *
 *
 */
data class LocationForecast(
    val type: String,
    val geometry: Geometry,
    val properties: Properties,
)

data class LocationforecastList(
    val locationforecastList: List<LocationForecast>
)



// TODO: Ny
data class Geometry(
    val type: String,
    val coordinates: List<Double>,
)

// TODO: Ny
data class Properties(
    val meta: Meta,
    val timeseries: List<Timeseries>,
)

// TODO: Ny
data class Meta(
    val updated_at: String,
    val units: Units,
)

// TODO: Ny
data class Units (
    val air_pressure_at_sea_level : String?,
    val air_temperature : String?,
    val air_temperature_max : String?,
    val air_temperature_min : String?,
    val air_temperature_percentile_10 : String?,
    val air_temperature_percentile_90 : String?,
    val cloud_area_fraction : String?,
    val cloud_area_fraction_high : String?,
    val cloud_area_fraction_low : String?,
    val cloud_area_fraction_medium : String?,
    val dew_point_temperature : String?,
    val fog_area_fraction : String?,
    val precipitation_amount : String?,
    val precipitation_amount_max : String?,
    val precipitation_amount_min : String?,
    val probability_of_precipitation : String?,
    val probability_of_thunder : String?,
    val relative_humidity : String?,
    val ultraviolet_index_clear_sky : String?,
    val wind_from_direction : String?,
    val wind_speed : String?,
    val wind_speed_of_gust : String?,
    val wind_speed_percentile_10 : String?,
    val wind_speed_percentile_90 : String?
)

// TODO: Ny
data class Timeseries(
   // val time: JavaLocalDateTime,
    val time: String,
    val data: Data,
)
// JavaLocalDateTime.parse("2024-03-09T21:00:00")




// TODO: Ny
data class Data (
    val instant : Instant,
    val next_12_hours : Next_12_hours,
    val next_1_hours : Next_1_hours,
    val next_6_hours : Next_6_hours
)

// TODO: Ny
data class Instant(
    val details: InstantDetails,
)



data class InstantDetails (
    val air_pressure_at_sea_level : Double?,
    val air_temperature : Double?,
    val air_temperature_percentile_10 : Double?,
    val air_temperature_percentile_90 : Double?,
    val cloud_area_fraction : Double?,
    val cloud_area_fraction_high : Double?,
    val cloud_area_fraction_low : Double?,
    val cloud_area_fraction_medium : Double?,
    val dew_point_temperature : Double?,
    val fog_area_fraction : Double?,
    val relative_humidity : Double?,
    val ultraviolet_index_clear_sky : Double?,
    val wind_from_direction : Double?,
    val wind_speed : Double?,
    val wind_speed_of_gust : Double?,
    val wind_speed_percentile_10 : Double?,
    val wind_speed_percentile_90 : Double?

)


// TODO: Ny
data class Next_12_hours(
    val summary: Summary12,
    val details: Details12,
)

// TODO: Ny
data class Summary12(
    val symbol_code: String,
    val symbol_confidence: String
)




// TODO: Ny
data class Details12 (
    val probability_of_precipitation : Double
)



// TODO: Ny
data class Next_1_hours(
    val summary: Summary1,
    val details: Details1,
)

data class Summary1(
    val symbol_code: String,
)


data class Details1 (
    val precipitation_amount : Double,
    val precipitation_amount_max : Double,
    val precipitation_amount_min : Double,
    val probability_of_precipitation : Double,
    val probability_of_thunder: Double
)


// TODO: Ny
data class Next_6_hours(
    val summary: Summary6,
    val details: Details6,
)


data class Summary6(
    val symbol_code: String,
)

data class Details6 (
    val air_temperature_max : Double,
    val air_temperature_min : Double,
    val precipitation_amount : Double,
    val precipitation_amount_max : Double,
    val precipitation_amount_min : Double,
    val probability_of_precipitation : Double
)



