package no.uio.ifi.in2000.team11.havvarselapp.ui.networkConnection

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import no.uio.ifi.in2000.team11.havvarselapp.R

/**
 * A dialog that informs the user that the app has lost internet connection.
 * It disappears if the app gains access again.
 */
@Composable
fun NetworkConnectionStatus(
    connectivityObserver: ConnectivityObserver,
    enableSearch: MutableState<Boolean>?
) {
    val coroutineScope = rememberCoroutineScope()

    var shouldShowWarning by remember { mutableStateOf(true) } // State to control the warning box visibility

    val isDelayOver = remember { mutableStateOf(false) }

    val status by connectivityObserver.observe().collectAsState(
        initial = ConnectivityObserver.Status.Unavailable
    )

    // Start the delay when the composable enters the composition
    LaunchedEffect(coroutineScope) {
        delay(2000)
        isDelayOver.value = true
    }

    val network = ImageVector.vectorResource(id = R.drawable.network2)

    // Close the warning and it will not be shown again unless you reset the state
    fun dismissDialog() {
        shouldShowWarning = false
    }

    if (isDelayOver.value && shouldShowWarning) {
        // Condition to show the dialog
        if (status == ConnectivityObserver.Status.Lost ||
            status == ConnectivityObserver.Status.Losing ||
            status == ConnectivityObserver.Status.Unavailable
        ) {
            enableSearch?.value = false

            // Dialog Content
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(340.dp)
                    .padding(8.dp)
                    .background(Color.White, shape = RoundedCornerShape(8.dp))
                    .shadow(4.dp, shape = RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Column (modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally){

                    Image(
                        imageVector = network, contentDescription = "no internet connection",
                        Modifier.size(120.dp)
                    )

                    Text(
                        text = "Oisann!",
                        style = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    Text(
                        text = "Ingen internett-tilkobling",
                        style = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    Text(
                        text = "Uten nett får du ikke oppdatert",
                        style = TextStyle(fontSize = 16.sp),
                        modifier = Modifier.padding(bottom = 10.dp),
                    )
                    Text(
                        text = "værmelding, havforhold og farevarslinger",
                        style = TextStyle(fontSize = 16.sp),
                        modifier = Modifier.padding(bottom = 16.dp),
                    )
                    // Button to dismiss dialog
                    Button(
                        // Close dialog on click
                        onClick = { dismissDialog() },
                        modifier = Modifier.padding(bottom = 10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF_13_23_2C))
                    ) {
                        Text(text = "Skjønner", fontSize = (15.sp))
                    }
                }
            }
        } else {
            enableSearch?.value = true
        }
    }

    // Cancel the coroutine when the composable is disposed
    DisposableEffect(Unit) {
        onDispose {
            coroutineScope.cancel()
        }
    }
}


