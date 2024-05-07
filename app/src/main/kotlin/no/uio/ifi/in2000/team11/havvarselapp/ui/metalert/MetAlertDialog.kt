package no.uio.ifi.in2000.team11.havvarselapp.ui.metalert

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import no.uio.ifi.in2000.team11.havvarselapp.SharedUiState
import no.uio.ifi.in2000.team11.havvarselapp.model.alert.MetAlert

/**
 * A dialog that appears when clicking on the met-alerts icon on the seamap screen.
 * Displays information about the met alerts that are currently active at this location.
 */
@Composable
fun MetAlertsDialog(
    // met alerts are saved in the sharedUiState
    sharedUiState: SharedUiState,
    // function to close the dialog
    onDismiss:() -> Unit,
){
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .zIndex(2f)
            .heightIn(max = 700.dp)
            .padding(16.dp)
            .background(Color.White, shape = RoundedCornerShape(8.dp))
            .shadow(4.dp, shape = RoundedCornerShape(8.dp)),
        contentAlignment = Alignment.Center
    ) {
        Scaffold { innerPadding ->
            Column(
                modifier = Modifier.padding(innerPadding)
            ) {

                // Title of the dialog
                Text(
                    text = "Farevarsler",
                    fontSize = 35.sp,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .paddingFromBaseline(50.dp, 10.dp)
                )

                // displays all met-alerts in a scrollable dialog window
                LazyVerticalGrid(
                    modifier = Modifier.padding(innerPadding),
                    columns = GridCells.Fixed(1)
                ) {

                    // contains a card for each met-alert
                    items(
                        count = sharedUiState.allMetAlerts.size,
                        key = { index -> sharedUiState.allMetAlerts[index].id }
                    ) { index ->
                        MetAlertCard(metAlert = sharedUiState.allMetAlerts[index])
                    }
                }
            }
        }

        // button to close dialog
        Button(
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 7.dp),
            onClick = onDismiss,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF_13_23_2C)
            )
        ) {
            Text("Skjønner")
        }
    }
}

/**
 * A card containing all information about an active met-alert
 */
@Composable
fun MetAlertCard(metAlert: MetAlert) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(100.dp)
            ) {

                // Get the right icon to display in the card
                GetIconForAlert(
                    iconName = metAlert.iconName,
                    color = metAlert.riskMatrixColor,
                    false
                )
            }
            Spacer(Modifier.width(8.dp))

            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
                    .padding(start = 8.dp)
            ) {

                // The area where the met-alert is active
                Text(
                    text = metAlert.area,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .align(Alignment.Start)
                )

                // Description of what the met-alert is about
                Text(
                    text = metAlert.description,
                    modifier = Modifier
                        .padding(top = 3.dp)
                        .align(Alignment.Start)
                )

                // Instruction for how to respond
                Text(
                    text = metAlert.instruction,
                    modifier = Modifier
                        .align(Alignment.Start),
                    color = Color.Blue
                )
            }
        }
    }
}