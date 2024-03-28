package no.uio.ifi.in2000.team11.havvarselapp.data.location

import com.google.android.gms.maps.model.LatLng

interface LocationRepository {
    /** returnerer den nåværende posisjonen i appen */
    fun getCurrentLocation(): LatLng

    /** oppdaterer den nåværende posisjonen i appen */
    fun setCurrentLocation(location: LatLng)
}

class LocationRepositoryImpl(
    /**
     * Den valgte posisjonen i appen, endres hver
     * gang brukeren velger en ny posisjon.
     */
    private var currentLocation: LatLng = LatLng(59.9, 10.73),

) : LocationRepository {

    override fun getCurrentLocation(): LatLng {
        return currentLocation
    }

    override fun setCurrentLocation(location: LatLng) {
        currentLocation = location
    }
}