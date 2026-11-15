package com.kieronquinn.app.taptap.ui.screens.setup.complete

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.kieronquinn.app.taptap.R
import com.kieronquinn.app.taptap.ui.compose.SetupScaffold
import com.kieronquinn.app.taptap.ui.compose.TintedLottie
import com.kieronquinn.app.taptap.ui.screens.setup.info.InfoCard

@Composable
fun SetupCompleteScreen(viewModel: SetupCompleteViewModel) {
    SetupScaffold(onBack = { viewModel.onBackPressed() }) {
        TintedLottie(
            R.raw.setup_complete, MaterialTheme.colorScheme.primary, "Background Circle (Blue)", "Background(Blue)",
            modifier = Modifier.size(240.dp), iterations = 1
        )
        Text(stringResource(R.string.setup_complete_title), style = MaterialTheme.typography.headlineMedium, textAlign = TextAlign.Center)
        InfoCard(R.drawable.ic_taptap_logo, stringResource(R.string.setup_complete_card))
        Button(onClick = viewModel::onCloseClicked, modifier = Modifier.padding(top = 16.dp)) {
            Text(stringResource(R.string.close))
        }
    }
}
