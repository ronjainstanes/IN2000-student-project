package no.uio.ifi.in2000.team11.havvarselapp.model

import com.google.android.gms.maps.model.LatLng
import java.time.ZonedDateTime

/**
 * Inneholder info om et farevarsel
 */
data class MetAlert(
    /**
     * Id
     */
    val id: String,

    /**
     * Området varselet gjelder for
     */
    val area: String,

    /**
     * Tittel
     */
    val title: String,

    /**
     * Liste med koordinater som rammer inn
     * området der varselet gjelder
     */
    val coordinates: List<LatLng>,

    /**
     * Beskrivelse av varselet
     */
    val description: String, // properties.description

    /**
     * Beskriver konsekvensene av været
     */
    val consequences: String,

    /**
     * Instruks til hva man bør gjøre,
     * f.eks. "ikke dra ut i småbåt"
     */
    val instruction: String,

    /**
     * Hvilket nivå varselet har,
     * f.eks. "2, yellow, Moderate"
     */
    val awarenessLevel: List<String>, //TODO kan gjøres om til enum med 3 nivåer

    /**
     * Type farevarsel, f.eks "1, wind" //TODO finn ut hvilke typer som finnes, gjøre om til enum?
     */
    val awarenessType: List<String>,

    /**
     * Et StartStopDate-objekt som inneholder
     * start- og sluttidspunkt der varselet gjelder
     */
    val duration: StartStopDate,

    /**
     * Meter per sekund
     */
    val triggerLevel: String
) {
    /**
     * Sammenligner to MetAlert-objekter
     * @return er dette samme farevarsel?
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MetAlert

        return id == other.id
    }

    /**
     * Returnerer hashkoden til MetAlert-objektet
     */
    override fun hashCode(): Int {
        return id.hashCode()
    }
}

data class StartStopDate(
    val start: ZonedDateTime,
    val stop: ZonedDateTime
)