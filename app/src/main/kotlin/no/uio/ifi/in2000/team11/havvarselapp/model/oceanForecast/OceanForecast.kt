package no.uio.ifi.in2000.team11.havvarselapp.model.oceanForecast

/**
 * Contains all data from OceanForecast API.
 * Data class is generated with the JSON to Kotlin converter to
 * match the output of the .body() function used in the DataSource.
 *
 * NOTE: this file contains many warnings, because the data from the API
 * contains variables with underscores. However, the names need to match
 * perfectly for the .body() function to work.
 */
data class OceanForecast(
    val type: String,
    val geometry: GeometryOcean,
    val properties: PropertiesOcean
)

data class GeometryOcean(
    val type: String,
    val coordinates: List<Double>,
)


data class PropertiesOcean(
    val meta: MetaOcean,
    val timeseries: List<TimeseriesOcean>,
)


data class MetaOcean(
    val updated_at: String,
    val units: UnitsOcean,
)


data class UnitsOcean(
    val sea_surface_wave_from_direction: String?,
    val sea_surface_wave_height: String?,
    val sea_water_speed: String?,
    val sea_water_temperature: String?,
    val sea_water_to_direction: String?,
)


data class TimeseriesOcean(
    val time: String,
    val data: DataOcean,
)

data class DataOcean(
    val instant: InstantOcean,
)

data class InstantOcean(
    val details: DetailsOcean,
)

// The variables we use to show the ocean forecast for a specific place
data class DetailsOcean(
    val sea_surface_wave_from_direction: Double?, // Wave direction follows meteorological convention. It is given as the direction the waves are coming from (0° is north, 90° east, etc.)
    val sea_surface_wave_height: Double?, // (meter) Significant wave height defined as the average of the highest one-third (33%) of waves (measured from trough to crest)
    val sea_water_speed: Double?, // Speed of sea water (current)
    val sea_water_temperature: Double?, //  (celsius) Surface temperature of sea water
    val sea_water_to_direction: Double?, //Sea water (current) direction follows oceanographic convention. It is given as the direction the sea water is moving towards (0° is north, 90° east, etc.)
)
