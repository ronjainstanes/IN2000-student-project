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


@Composable
fun MetAlertsDialog(
    sharedUiState: SharedUiState,
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
                // Tittel
                Text(
                    text = "Farevarsler",
                    fontSize = 35.sp,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .paddingFromBaseline(50.dp, 10.dp)
                )

                // Viser frem alle farevarsler
                LazyVerticalGrid(
                    modifier = Modifier.padding(innerPadding),
                    columns = GridCells.Fixed(1)
                ) {
                    items(
                        count = sharedUiState.allMetAlerts.size,
                        key = { index -> sharedUiState.allMetAlerts[index].id }
                    ) { index ->
                        MetAlertCard(metAlert = sharedUiState.allMetAlerts[index])
                    }
                }
            }
        }

        Button(
            onClick = onDismiss,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF_13_23_2C)
            )
        ) {
            Text("Skjønner")
        }
    }
}


@Composable
fun MetAlertCard(metAlert: MetAlert) {

    Card(
        modifier = Modifier
            .fillMaxWidth() // Fylle maksimal bredde som er tilgjengelig
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth() // Fylle maksimal bredde som er tilgjengelig
                .padding(16.dp) // Sett padding for innholdet i kortet
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(100.dp) // Sett en fast størrelse for boksen som ikonet skal være inne i
            ) {
                // Ikonet hentes og vises
                GetIcon(type = metAlert.awarenessType[1], color = metAlert.riskMatrixColor)
            }
            // Spacer legger til et mellomrom mellom ikonet og teksten
            Spacer(Modifier.width(8.dp))

            // Sett resten av teksten til å ta opp så mye plass som mulig
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f) // Sett et vektsystem for å fylle tilgjengelig plass
                    .padding(start = 8.dp) // Sett padding mellom ikonet og teksten
            ) {
                Text(
                    text = metAlert.area,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .align(Alignment.Start) // Venstrejuster teksten
                )

                Text(
                    text = metAlert.description,
                    modifier = Modifier
                        .padding(top = 3.dp) // Legger til litt plass over denne teksten
                        .align(Alignment.Start) // Venstrejuster teksten
                )
                Text(
                    text = metAlert.instruction,
                    modifier = Modifier
                        .align(Alignment.Start), // Venstrejuster teksten
                    color = Color.Blue
                )
            }
        }
    }
}