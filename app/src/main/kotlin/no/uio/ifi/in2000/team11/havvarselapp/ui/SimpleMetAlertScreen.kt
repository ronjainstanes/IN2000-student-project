package no.uio.ifi.in2000.team11.havvarselapp.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Card
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import no.uio.ifi.in2000.team11.havvarselapp.model.MetAlert

@Composable
fun SimpleMetAlertScreen(
    simpleViewModel: SimpleViewModel = viewModel()
    //TODO legg til NavController
) {

    // Observe the UI state object from the ViewModel
    val appUiState: AppUiState by simpleViewModel.appUiState.collectAsState()

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding)
        ) {
            // Title
            Text(
                text = "Farevarsler",
                fontSize = 35.sp,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .paddingFromBaseline(50.dp, 10.dp)
            )

            // Display all parties
            LazyVerticalGrid(
                modifier = Modifier.padding(innerPadding),
                columns = GridCells.Fixed(1)
            ) {
                items(
                    count = appUiState.allMetAlerts.size,
                    key = { index -> appUiState.allMetAlerts[index].id }
                ) { index ->
                    MetAlertCard(metAlert = appUiState.allMetAlerts[index])
                }
            }
        }
    }
}

@Composable
fun MetAlertCard(metAlert: MetAlert) {
    Card(
        modifier = Modifier
            .clipToBounds()
            .padding(16.dp)
    ) {
        Text(
            text = metAlert.area,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
        )
        Text(
            text = metAlert.awarenessLevel.toString(),
            modifier = Modifier
                .padding(3.dp)
                .align(Alignment.CenterHorizontally),
            color = Color.Magenta
        )
        Text(
            text = metAlert.description,
            modifier = Modifier
                .padding(3.dp)
                .align(Alignment.CenterHorizontally)
        )
        Text(
            text = metAlert.instruction,
            modifier = Modifier
                .align(Alignment.CenterHorizontally),
            color = Color.Blue
        )
    }
}