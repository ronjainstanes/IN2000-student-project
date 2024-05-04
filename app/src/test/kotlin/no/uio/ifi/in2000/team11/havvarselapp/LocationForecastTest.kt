package no.uio.ifi.in2000.team11.havvarselapp

import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import no.uio.ifi.in2000.team11.havvarselapp.model.locationForecast.Data
import no.uio.ifi.in2000.team11.havvarselapp.model.locationForecast.Details1
import no.uio.ifi.in2000.team11.havvarselapp.model.locationForecast.Details12
import no.uio.ifi.in2000.team11.havvarselapp.model.locationForecast.Details6
import no.uio.ifi.in2000.team11.havvarselapp.model.locationForecast.Instant
import no.uio.ifi.in2000.team11.havvarselapp.model.locationForecast.InstantDetails
import no.uio.ifi.in2000.team11.havvarselapp.model.locationForecast.Next_12_hours
import no.uio.ifi.in2000.team11.havvarselapp.model.locationForecast.Next_1_hours
import no.uio.ifi.in2000.team11.havvarselapp.model.locationForecast.Next_6_hours
import no.uio.ifi.in2000.team11.havvarselapp.model.locationForecast.Summary1
import no.uio.ifi.in2000.team11.havvarselapp.model.locationForecast.Summary12
import no.uio.ifi.in2000.team11.havvarselapp.model.locationForecast.Summary6
import no.uio.ifi.in2000.team11.havvarselapp.model.locationForecast.Timeseries
import no.uio.ifi.in2000.team11.havvarselapp.ui.weather.getWeatherIcon
import no.uio.ifi.in2000.team11.havvarselapp.ui.weather.getWeatherIconLongTerm
import no.uio.ifi.in2000.team11.havvarselapp.ui.weather.getWindDirection
import no.uio.ifi.in2000.team11.havvarselapp.ui.weather.temperaturePositiveLongTerm
import org.junit.Test

//This file creates Timeseries object from class defined in LocalForecast.kt file
//and further tests utils functions in the UtilitiesWeatherScreen.kt
//Each functions takes original predifine Timeseries object and change it a bit while
//testing spesific files
class LocationForecastTest {

    // Sets up data and variables needed as arguments
    private var summary12: Summary12 = Summary12("x1", "99")
    private var details12: Details12 = Details12(0.2)
    private var next12hours: Next_12_hours = Next_12_hours(summary12, details12)

    private var summary1: Summary1 = Summary1("x2")
    private var details1: Details1 = Details1(0.1,0.2,0.4,0.5,0.7)
    private var next1hours: Next_1_hours = Next_1_hours(summary1, details1)

    private var summary6: Summary6 = Summary6("x3")
    private var details6: Details6 = Details6(0.3, 0.6, 0.9, 1.15, 4.25, 7.85)
    private var next6hours: Next_6_hours = Next_6_hours(summary6, details6)

    private var detailsObj: InstantDetails = InstantDetails(
        air_pressure_at_sea_level = 23.3,
        air_temperature = 55.6,
        air_temperature_percentile_10 = 31.6,
        air_temperature_percentile_90 = 74.7,
        cloud_area_fraction = 42.3,
        cloud_area_fraction_high = 94.7,
        cloud_area_fraction_low = 34.6,
        cloud_area_fraction_medium = 34.6,
        dew_point_temperature = 23.3,
        fog_area_fraction = 21.5,
        relative_humidity = 14.4,
        ultraviolet_index_clear_sky = 29.4,
        wind_from_direction = 78.9,
        wind_speed = 9.4,
        wind_speed_of_gust = 53.4,
        wind_speed_percentile_10 = 8.9,
        wind_speed_percentile_90 = 7.8,
    )

    private var instantObj: Instant = Instant(detailsObj)
    private var dataObj: Data = Data(instantObj, next12hours, next1hours, next6hours)
    private var timeseriesObj: Timeseries = Timeseries("time1", dataObj)


    @Test
    fun testGetWeatherIcon(){
        assertEquals("x2",getWeatherIcon(timeseriesObj))
        timeseriesObj.data.next_1_hours?.summary?.symbol_code = "y10"
        assertEquals("y10",getWeatherIcon(timeseriesObj))

        assertEquals("x3",getWeatherIconLongTerm(timeseriesObj))
        timeseriesObj.data.next_6_hours?.summary?.symbol_code = "y150"
        assertEquals("y150",getWeatherIconLongTerm(timeseriesObj))
    }


    @Test
    fun testGetWindDirection(){
        assertEquals("Ø",getWindDirection(timeseriesObj))
        timeseriesObj.data.instant.details.wind_from_direction = null
        assertEquals(" ",getWindDirection(timeseriesObj))
        timeseriesObj.data.instant.details.wind_from_direction = 112.5
        assertEquals("SØ",getWindDirection(timeseriesObj))
    }


    @Test
    fun temperaturePositiveLongTerm(){
        assertTrue(temperaturePositiveLongTerm(timeseriesObj))
        timeseriesObj.data.next_6_hours?.details?.air_temperature_min = 0.1
        assertFalse(temperaturePositiveLongTerm(timeseriesObj))
        timeseriesObj.data.next_6_hours?.details?.air_temperature_min = null
        assertTrue(temperaturePositiveLongTerm(timeseriesObj))
    }
}