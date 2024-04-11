package no.uio.ifi.in2000.team11.havvarselapp.ui.metalert

import android.annotation.SuppressLint
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

/**
 * Henter riktig ikon for farevarselet.
 * NB: Funksjonen har "SuppressLint" annotering fordi ikonnavn
 * og ID er dynamiske i stedet for statiske, noe som gir en warning hvis
 * man ikke har SuppressLint annoteringen.
 */
@SuppressLint("DiscouragedApi")
@Composable
fun GetIcon(type: String, color: String) {
    val context = LocalContext.current
    val resources = context.resources

    // Bygger strengen som representerer filnavnet til ikonet
    val iconName = "icon_warning_${type.trim()}_${color}".lowercase()

    // Få Drawable ressurs ID for det dynamisk konstruerte ikonnavnet
    @DrawableRes
    val drawableResId: Int = resources.getIdentifier(
        iconName,
        "drawable",
        context.packageName
    )
    // Samme prinsipp, men hvis fare-type ikke finnes
    val otherIconName = "icon_warning_generic_${color}".lowercase()

    @DrawableRes
    val otherDrawableResId: Int = resources.getIdentifier(
        otherIconName,
        "drawable",
        context.packageName
    )

    // Kontroller at ressursen finnes før den brukes
    if (drawableResId != 0) {
        // Bruker ikonet i Image komponenten
        Image(
            painter = painterResource(id = drawableResId),
            contentDescription = "Farevarsel Ikon",
            modifier = Modifier
                .size(100.dp)
                .padding(20.dp)
        )
    } else {
        Image(
            painter = painterResource(id = otherDrawableResId),
            contentDescription = "Farevarsel Ikon",
            modifier = Modifier
                .size(100.dp)
                .padding(20.dp)
        )
    }
}
