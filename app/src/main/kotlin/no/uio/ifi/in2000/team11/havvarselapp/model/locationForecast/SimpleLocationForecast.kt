package no.uio.ifi.in2000.team11.havvarselapp.model.locationForecast

data class SimpleLocationForecast (
    val temp: String?,
    val windSpeed: String?,
    val windDirection: String?,

    /** TODO sjekk hvorfor dette alltid blir null :) */
    val uv: String?,
)