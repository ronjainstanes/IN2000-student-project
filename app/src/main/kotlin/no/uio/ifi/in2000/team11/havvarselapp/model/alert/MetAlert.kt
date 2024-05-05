package no.uio.ifi.in2000.team11.havvarselapp.model.alert

/**
 * Contains information about a met-alert
 */
data class MetAlert(
    /**
     * Id for the met-alert
     */
    val id: String,

    /**
     * The area to which the alert applies
     */
    val area: String,

    /**
     * Title of the alert
     */
    val title: String,

    /**
     * Description
     */
    val description: String,

    /**
     * This name corresponds to the right icon to
     * display for this met-alert
     */
    val iconName: String,

    /**
     * Type of event. The combination of color and event is
     * used to fetch the right image for the alert.
     */
    val event: String,

    /**
     * Describes the consequences following the extreme weather.
     * This can be null.
     */
    val consequences: String?,

    /**
     * Instruction of how to handle this weather.
     * For example: "ikke dra ut i småbåt"
     */
    val instruction: String,

    /**
     * Which
     * f.eks. "2, yellow, Moderate"
     */
    val awarenessLevel: List<String>,

    /**
     * The color (danger level) of the met-alert, possible values:
     * Green, Yellow, Orange, Red
     */
    val riskMatrixColor: String,

    /**
     * Type of met-alert, for example "1, wind".
     *
     * All possible types: wind, extreme high temperature, rain,
     * extreme low temperature, snow/ice, coastal event,
     * thunderstorms, forestfire, fog, avalanches
     */
    val awarenessType: List<String>,

    /**
     * Meter per second, can be null
     */
    val triggerLevel: String?
) {
    /**
     * Compares two met alert objects to find out if
     * they are the same alert.
     * @return is this the same met-alert?
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MetAlert

        return id == other.id
    }

    /**
     * Returns the hashcode of the met-alert object
     */
    override fun hashCode(): Int {
        return id.hashCode()
    }
}