package no.uio.ifi.in2000.team11.havvarselapp.model.harbor

data class Harbor(
    val id: Int,
    val name: String,
    val location: Array<Double>,
    val description: String
)

