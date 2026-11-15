package com.kieronquinn.app.taptap.ui.screens.settings.nativemode

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kieronquinn.app.taptap.R
import com.kieronquinn.app.taptap.ui.compose.MessageCard
import com.kieronquinn.app.taptap.ui.compose.ModeScreen
import com.kieronquinn.app.taptap.ui.screens.settings.lowpower.ShizukuError

@Composable
fun SettingsNativeModeScreen(viewModel: SettingsNativeModeViewModel, onSetupNotification: suspend () -> Unit) {
    val context = LocalContext.current
    val checked by viewModel.nativeModeEnabled.collectAsStateWithLifecycle()
    val enabled by viewModel.switchEnabled.collectAsState(false)
    val shizukuInstalled by viewModel.shizukuInstalled.collectAsState(true)
    LifecycleResumeEffect(viewModel) {
        viewModel.checkState(context)
        onPauseOrDispose {}
    }
    LaunchedEffect(viewModel) {
        viewModel.toastBus.collect { Toast.makeText(context, it, Toast.LENGTH_LONG).show() }
    }
    LaunchedEffect(viewModel) {
        viewModel.setupNotificationBus.collect { onSetupNotification() }
    }
    ModeScreen(
        switchLabel = stringResource(R.string.settings_native_mode_enable),
        checked = checked,
        enabled = enabled,
        onToggle = { viewModel.onSwitchClicked(context) }
    ) {
        ShizukuError(shizukuInstalled, { viewModel.onShizukuClicked(context) }, viewModel::onSuiClicked)
        MessageCard(R.drawable.ic_about, stringResource(R.string.setting_native_mode_content))
    }
}
