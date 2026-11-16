package com.kieronquinn.app.taptap.ui.screens.settings.shared.internet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.kieronquinn.app.taptap.R

@Composable
fun InternetPermissionDialog(viewModel: SettingsSharedInternetPermissionDialogViewModel) {
    Surface(shape = MaterialTheme.shapes.extraLarge, color = MaterialTheme.colorScheme.surfaceContainerHigh) {
        Column(
            Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(painterResource(R.drawable.ic_internet), null, tint = MaterialTheme.colorScheme.secondary)
            Text(stringResource(R.string.dialog_internet_permission_text), style = MaterialTheme.typography.headlineSmall, textAlign = TextAlign.Center)
            Text(stringResource(R.string.dialog_internet_permission_desc), textAlign = TextAlign.Center)
            Button(onClick = viewModel::onAllowAlwaysClicked, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.dialog_internet_permission_allow_always))
            }
            FilledTonalButton(onClick = viewModel::onAllowRunningClicked, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.dialog_internet_permission_allow_open))
            }
            TextButton(onClick = viewModel::onDenyClicked, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.dialog_internet_permission_deny))
            }
        }
    }
}
