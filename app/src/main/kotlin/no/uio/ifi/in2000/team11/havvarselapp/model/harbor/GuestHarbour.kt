package no.uio.ifi.in2000.team11.havvarselapp.model.harbor

/**
 * A data class containing information about a guest harbour
 */
data class Harbor(
    val id: Int,
    val name: String,
    val location: Array<Double>,
    val description: String
) {

    /**
     * Compares two objects of the GuestHarbour data class,
     * and returns true or false if these are the same guest harbour
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Harbor

        return id == other.id
    }

    /**
     * Generates and returns a hash code for the GuestHarbour object
     */
    override fun hashCode(): Int {
        return id
    }
}

