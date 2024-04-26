package no.uio.ifi.in2000.team11.havvarselapp.model.harbor

data class Harbor(
    val id: Int,
    val name: String,
    val location: DoubleArray?,
    val description: String
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Harbor

        if (id != other.id) return false
        if (name != other.name) return false
        if (location != null) {
            if (other.location == null) return false
            if (!location.contentEquals(other.location)) return false
        } else if (other.location != null) return false
        if (description != other.description) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + name.hashCode()
        result = 31 * result + (location?.contentHashCode() ?: 0)
        result = 31 * result + description.hashCode()
        return result
    }
}

