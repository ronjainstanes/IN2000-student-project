package no.uio.ifi.in2000.team11.havvarselapp.model.locationForecast

/**
 *
Henter vær data for 9 dager frem i tid
 *
 */
data class LocationForecast(
    // Complete
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
    val air_temperature_max: String?, // COMPLETE
    val air_temperature_min: String?, // COMPLETE
    val air_temperature_percentile_10: String?, // COMPLETE
    val air_temperature_percentile_90: String?, // COMPLETE
    val cloud_area_fraction: String?,
    val cloud_area_fraction_high: String?, // COMPLETE
    val cloud_area_fraction_low: String?, // COMPLETE
    val cloud_area_fraction_medium: String?, // COMPLETE
    val dew_point_temperature: String?, // COMPLETE
    val fog_area_fraction: String?, // COMPLETE
    val precipitation_amount: String?,
    val precipitation_amount_max: String?,
    val precipitation_amount_min: String?,
    val probability_of_precipitation: String?,
    val probability_of_thunder: String?,
    val relative_humidity: String?,
    val ultraviolet_index_clear_sky: String?, // COMPLETE
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
    val next_12_hours: Next_12_hours,
    val next_1_hours: Next_1_hours?,
    val next_6_hours: Next_6_hours
)


data class Instant(
    val details: InstantDetails,
)


data class InstantDetails(
    val air_pressure_at_sea_level: Double?,
    val air_temperature: Double?,
    val air_temperature_percentile_10: Double?,
    val air_temperature_percentile_90: Double?,
    val cloud_area_fraction: Double?,
    val cloud_area_fraction_high: Double?, // COMPLETE
    val cloud_area_fraction_low: Double?, // COMPLETE
    val cloud_area_fraction_medium: Double?, // COMPLETE
    val dew_point_temperature: Double?, // COMPLETE
    val fog_area_fraction: Double?, // COMPLETE
    val relative_humidity: Double?,
    val ultraviolet_index_clear_sky: Double?, // COMPLETE
    val wind_from_direction: Double?,
    val wind_speed: Double?,
    val wind_speed_of_gust: Double?,
    val wind_speed_percentile_10: Double?,
    val wind_speed_percentile_90: Double?,
)


data class Next_12_hours(
    val summary: Summary12,
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

data class Summary1(
    val symbol_code: String?,
)


data class Details1(
    val precipitation_amount: Double?,
    val precipitation_amount_max: Double?,
    val precipitation_amount_min: Double?,
    val probability_of_precipitation: Double?,
    val probability_of_thunder: Double?,
)


data class Next_6_hours(
    val summary: Summary6,
    val details: Details6,
)


data class Summary6(
    val symbol_code: String?,
)

data class Details6(
    val air_temperature_max: Double?, // COMPLETE
    val air_temperature_min: Double?, // COMPLETE
    val precipitation_amount: Double?,
    val precipitation_amount_max: Double?,
    val precipitation_amount_min: Double?,
    val probability_of_precipitation: Double?,

    )



