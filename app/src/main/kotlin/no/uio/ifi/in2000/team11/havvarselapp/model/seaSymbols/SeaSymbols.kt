package no.uio.ifi.in2000.team11.havvarselapp.model.seaSymbols

import androidx.annotation.DrawableRes
import no.uio.ifi.in2000.team11.havvarselapp.R

/**
 * Data class containing symbol and description
 * of a symbol on the map
 */
data class SeaSymbolsPair(
    @DrawableRes val symbol: Int,
    val description: String
)

/**
 * A list of all the sea symbols on the map, and a description for each one
 */
data class SeaSymbolsList(
    val symbolDescription: List<SeaSymbolsPair> = listOf(
        SeaSymbolsPair(R.drawable.with_gas_harbor, "Parkering med bensinstasjon"),
        SeaSymbolsPair(R.drawable.without_gas_harbor, "Parkering uten bensinstasjon"),
        SeaSymbolsPair(R.drawable.east_cardinal, "Øst kardinal"),
        SeaSymbolsPair(R.drawable.west_cardinal, "Vest kardinal"),
        SeaSymbolsPair(R.drawable.north_cardinal, "Nord kardinal"),
        SeaSymbolsPair(R.drawable.south_cardinal, "Sør kardinal"),
        SeaSymbolsPair(R.drawable.isolatedcardinal, "Isolert kardinal"),
        SeaSymbolsPair(R.drawable.lateral_safewater, "Lateral senterledsmerke"),
        SeaSymbolsPair(R.drawable.port_mark, "Portmerke"),
        SeaSymbolsPair(R.drawable.preferred_channel_port, "Foretrukket kanal babord"),
        SeaSymbolsPair(R.drawable.preferred_channel_starboard, "Foretrukket kanal styrbord"),
        SeaSymbolsPair(R.drawable.special_mark, "Spesielt merke"),
        SeaSymbolsPair(R.drawable.starboardmark, "Styrbord merke"),
        SeaSymbolsPair(R.drawable.red_beacon, "Rødt signallys"),
        SeaSymbolsPair(R.drawable.yellow_beacon, "Gult signallys"),
        SeaSymbolsPair(R.drawable.green_beacon, "Grønt signallys"),
        SeaSymbolsPair(R.drawable.harbour, "Havn"),
        SeaSymbolsPair(R.drawable.anchorage, "Ankerplass"),
        SeaSymbolsPair(R.drawable.fishing_harbour, "Fiskehavn"),
        SeaSymbolsPair(R.drawable.marina, "Marina"),
        SeaSymbolsPair(R.drawable.breakwater, "Molo"),
        SeaSymbolsPair(R.drawable.pier, "Brygge"),
        SeaSymbolsPair(R.drawable.crane, "Kran"),
        SeaSymbolsPair(R.drawable.slipway, "Slipp"),
        SeaSymbolsPair(R.drawable.harbour_master, "Havnemester"),
        SeaSymbolsPair(R.drawable.waste_disposal, "Avfallshåndtering")
    )
)
