package com.kieronquinn.app.taptap.ui.screens.setup.landing

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.kieronquinn.app.taptap.R
import com.kieronquinn.app.taptap.ui.compose.DrawableImage

@Composable
fun SetupLandingScreen(viewModel: SetupLandingViewModel) {
    Surface(Modifier.fillMaxSize()) {
        Column(
            Modifier.safeDrawingPadding().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                Modifier.weight(1f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                DrawableImage(R.mipmap.ic_launcher, 144.dp)
                Spacer(Modifier.height(24.dp))
                Text(stringResource(R.string.landing_title), style = MaterialTheme.typography.displaySmall, textAlign = TextAlign.Center)
            }
            Button(
                onClick = viewModel::onStartClicked,
                modifier = Modifier.fillMaxWidth().height(ButtonDefaults.MediumContainerHeight),
                contentPadding = ButtonDefaults.MediumContentPadding
            ) { Text(stringResource(R.string.landing_button_get_started), style = ButtonDefaults.textStyleFor(ButtonDefaults.MediumContainerHeight)) }
            TextButton(onClick = viewModel::onSkipClicked, modifier = Modifier.padding(top = 8.dp)) {
                Text(stringResource(R.string.landing_button_skip_setup))
            }
        }
    }
}
