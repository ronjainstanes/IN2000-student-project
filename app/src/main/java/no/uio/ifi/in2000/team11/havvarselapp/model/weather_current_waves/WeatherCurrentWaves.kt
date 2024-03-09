package no.uio.ifi.in2000.team11.havvarselapp.model.weather_current_waves


data class WeatherCurrentWaves(
    val area: String,
    val content: String,
    val updated: String,
    val uri: String
)

/**
 * AREA: området for data - oslofjord, skagerrak, sorlandet, west_norway, n-northsea, troms-finnmark, nordland
 * CONTENT: type data - Weather, Current eller Waves
 * UPDATED: timestamp for når data asist ble oppdatert
 * URI: lenke til selve dataen
 */