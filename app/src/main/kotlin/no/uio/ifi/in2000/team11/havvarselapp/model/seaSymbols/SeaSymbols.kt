package no.uio.ifi.in2000.team11.havvarselapp.model.seaSymbols

import androidx.annotation.DrawableRes
import no.uio.ifi.in2000.team11.havvarselapp.R

data class SeaSymbolsPair (

    @DrawableRes val symbol: Int,
    val description: String
    )

data class SeaSymbolsList(
    val symbolDescription: List<SeaSymbolsPair>

)
