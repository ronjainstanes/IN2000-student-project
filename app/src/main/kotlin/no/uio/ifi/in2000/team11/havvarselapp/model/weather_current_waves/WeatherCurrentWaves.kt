package no.uio.ifi.in2000.team11.havvarselapp.model.weather_current_waves


data class WeatherCurrentWaves(
    val labels: Labels,
    val params: Params,
    val updated: String,
    val uri: String
)


data class Labels(
    val area: String
)

data class Params(
    val area: String,
    val content: String
)

data class WeatherCurrentWaves_Objekt(
    val groupedWeatherCurrentWaves: Map<String, List<WeatherCurrentWaves>>
)

data class WeatherCurrentWaves_Objekt_liste(
    val liste: List<WeatherCurrentWaves_Objekt>
)
// grouped by area --> (oslofjord: <oslofjord, weather, updated, uri>,  <oslofjord, current,..>,  <oslofjord, waves,..>)


/**
 * AREA: området for data - oslofjord, skagerrak, sorlandet, west_norway, n-northsea, troms-finnmark, nordland
 * CONTENT: type data - Weather, Current eller Waves
 * UPDATED: timestamp for når data asist ble oppdatert
 * URI: lenke til selve dataen
 */