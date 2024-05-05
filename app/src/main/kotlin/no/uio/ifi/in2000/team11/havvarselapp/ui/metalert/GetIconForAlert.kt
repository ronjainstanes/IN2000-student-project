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
 * Returns the right icon for the met alert
 *
 * Note: The function has an 'SuppressLint' annotation because image
 * identifier is created in a dynamic way instead of static, based on the
 * data from the API. This causes a warning because it makes it harder to
 * perform build optimizations.
 */
@SuppressLint("DiscouragedApi")
@Composable
fun GetIconForAlert(iconName: String, color: String, small: Boolean) {
    val context = LocalContext.current
    val resources = context.resources

    // specifies the size of the image
    val imagesize: Int = if (small) {
        30 // this is the tiny image on the map screen
    }
    else {
        70 // the larger image that is in the met-alerts dialog-window
    }

    // Get Drawable resource ID for the given icon name
    @DrawableRes
    val drawableResId: Int = resources.getIdentifier(
        iconName,
        "drawable",
        context.packageName
    )
    // Same principle, but if the met-alert type does not exist
    val otherIconName = "icon_warning_generic_${color}".lowercase()

    @DrawableRes
    val otherDrawableResId: Int = resources.getIdentifier(
        otherIconName,
        "drawable",
        context.packageName
    )

    // Check that image exists
    if (drawableResId != 0) {
        // Display image
        Image(
            painter = painterResource(id = drawableResId),
            contentDescription = "Farevarsel Ikon",
            modifier = Modifier
                .size(imagesize.dp)
                .padding(1.dp)
        )
    // Generic met-alert icon with right color
    } else {
        Image(
            painter = painterResource(id = otherDrawableResId),
            contentDescription = "Farevarsel Ikon",
            modifier = Modifier
                .size(imagesize.dp)
                .padding(1.dp)
        )
    }
}
