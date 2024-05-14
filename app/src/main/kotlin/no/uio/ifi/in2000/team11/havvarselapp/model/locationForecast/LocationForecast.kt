package no.uio.ifi.in2000.team11.havvarselapp.model.locationForecast

/**
 * Contains all data from LocationForecast API.
 * Data class is generated with the JSON to Kotlin converter to
 * match the output of the .body() function used in the DataSource.
 *
 * NOTE: this file contains many warnings, because the data from the API
 * contains variables with underscores. However, the names need to match
 * perfectly for the .body() function to work.
 */
data class LocationForecast(
    val type: String,
    val geometry: Geometry,
    val properties: Properties,
)

data class Geometry(
    val type: String,
    val coordinates: List<Double>,
)

data class Properties(
    val meta: Meta,
    val timeseries: List<Timeseries>,
)

data class Meta(
    val updated_at: String,
    val units: Units,
)

data class Units(
    val air_pressure_at_sea_level: String?,
    val air_temperature: String?,
    val air_temperature_max: String?,
    val air_temperature_min: String?,
    val air_temperature_percentile_10: String?,
    val air_temperature_percentile_90: String?,
    val cloud_area_fraction: String?,
    val cloud_area_fraction_high: String?,
    val cloud_area_fraction_low: String?,
    val cloud_area_fraction_medium: String?,
    val dew_point_temperature: String?,
    val fog_area_fraction: String?,
    val precipitation_amount: String?,
    val precipitation_amount_max: String?,
    val precipitation_amount_min: String?,
    val probability_of_precipitation: String?,
    val probability_of_thunder: String?,
    val relative_humidity: String?,
    val ultraviolet_index_clear_sky: String?,
    val wind_from_direction: String?,
    val wind_speed: String?,
    val wind_speed_of_gust: String?,
    val wind_speed_percentile_10: String?,
    val wind_speed_percentile_90: String?,
)

data class Timeseries(
    val time: String,
    val data: Data,
)

data class Data(
    val instant: Instant,
    val next_12_hours: Next_12_hours?,
    val next_1_hours: Next_1_hours?,
    val next_6_hours: Next_6_hours?
)

data class Instant(
    val details: InstantDetails,
)

// The variables we use to show the forecast for a specific place for the first (almost) three days
data class InstantDetails(
    val air_pressure_at_sea_level: Double?,
    val air_temperature: Double?,
    val air_temperature_percentile_10: Double?,
    val air_temperature_percentile_90: Double?,
    val cloud_area_fraction: Double?,
    val cloud_area_fraction_high: Double?,
    val cloud_area_fraction_low: Double?,
    val cloud_area_fraction_medium: Double?,
    val dew_point_temperature: Double?,
    val fog_area_fraction: Double?,
    val relative_humidity: Double?,
    val ultraviolet_index_clear_sky: Double?,
    var wind_from_direction: Double?,
    val wind_speed: Double?,
    val wind_speed_of_gust: Double?,
    val wind_speed_percentile_10: Double?,
    val wind_speed_percentile_90: Double?,
)

data class Next_12_hours(
    val summary: Summary12?,
    val details: Details12?,
)

data class Summary12(
    val symbol_code: String?,
    val symbol_confidence: String?
)

data class Details12(
    val probability_of_precipitation: Double?
)

data class Next_1_hours(
    val summary: Summary1?,
    val details: Details1?,
)

// symbol_code determines what icon we use to illustrate the forecast for the first (almost) three days
data class Summary1(
    var symbol_code: String?,
)

data class Details1(
    val precipitation_amount: Double?,
    val precipitation_amount_max: Double?,
    val precipitation_amount_min: Double?,
    val probability_of_precipitation: Double?,
    val probability_of_thunder: Double?,
)


data class Next_6_hours(
    val summary: Summary6?,
    val details: Details6?,
)

// symbol_code determines what icon we use to illustrate the forecast after the first three days
data class Summary6(
    var symbol_code: String?,
)

// The variables we use to show the forecast for a specific place after the first three days
data class Details6(
    val air_temperature_max: Double?,
    var air_temperature_min: Double?,
    val precipitation_amount: Double?,
    val precipitation_amount_max: Double?,
    val precipitation_amount_min: Double?,
    val probability_of_precipitation: Double?,
)
