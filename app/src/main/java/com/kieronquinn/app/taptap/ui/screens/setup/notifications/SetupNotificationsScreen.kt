package com.kieronquinn.app.taptap.ui.screens.setup.notifications

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LifecycleResumeEffect
import com.kieronquinn.app.taptap.R
import com.kieronquinn.app.taptap.ui.compose.SetupScaffold
import com.kieronquinn.app.taptap.ui.compose.TintedLottie

@Composable
fun SetupNotificationsScreen(viewModel: SetupNotificationsViewModel) {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
        viewModel.onPermissionResult(context, it)
    }
    LifecycleResumeEffect(viewModel) {
        viewModel.checkPermission(context)
        onPauseOrDispose {}
    }
    SetupScaffold(onBack = { viewModel.onBackPressed() }) {
        TintedLottie(R.raw.setup_notifications, MaterialTheme.colorScheme.primary, "*", modifier = Modifier.size(240.dp))
        Text(stringResource(R.string.setup_notifications_title), style = MaterialTheme.typography.headlineMedium, textAlign = TextAlign.Center)
        Text(stringResource(R.string.setup_notifications_content), textAlign = TextAlign.Center, modifier = Modifier.padding(vertical = 16.dp))
        Button(onClick = { viewModel.onGrantClicked(launcher) }) {
            Text(stringResource(R.string.setup_notifications_request))
        }
    }
}
