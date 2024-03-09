package no.uio.ifi.in2000.team11.havvarselapp.model.weather_current_waves

data class WeatherCurrentWaves_Objekt(
    val groupedWeatherCurrentWaves: Map<String, List<WeatherCurrentWaves>>
)
// grouped by area --> (oslofjord: <oslofjord, weather, updated, uri>,  <oslofjord, current,..>,  <oslofjord, waves,..>)


data class WeatherCurrentWavesParams(
    val area: String,
    val content: String
)

