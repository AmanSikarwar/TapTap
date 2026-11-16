package com.kieronquinn.app.taptap.ui.screens.settings.lowpower

import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.LifecycleResumeEffect
import com.kieronquinn.app.taptap.R
import com.kieronquinn.app.taptap.ui.compose.MessageCard
import com.kieronquinn.app.taptap.ui.compose.ModeScreen

@Composable
fun SettingsLowPowerModeScreen(viewModel: SettingsLowPowerModeViewModel, onRestartService: () -> Unit) {
    val context = LocalContext.current
    val checked by viewModel.lowPowerModeEnabled.collectAsState(viewModel.lowPowerModeInitialValue)
    val enabled by viewModel.switchEnabled.collectAsState(false)
    val shizukuInstalled by viewModel.shizukuInstalled.collectAsState(true)
    val compatible by viewModel.lowPowerModeCompatible.collectAsState(true)
    LifecycleResumeEffect(viewModel) {
        viewModel.checkShizukuState(context)
        onPauseOrDispose {}
    }
    LaunchedEffect(viewModel) {
        viewModel.restartService?.collect { onRestartService() }
    }
    ModeScreen(
        switchLabel = stringResource(R.string.settings_low_power_mode_enable),
        checked = checked,
        enabled = enabled,
        onToggle = { viewModel.onLowPowerSwitchClicked(context) }
    ) {
        ShizukuError(shizukuInstalled, { viewModel.onShizukuClicked(context) }, viewModel::onSuiClicked)
        if (!compatible) {
            MessageCard(R.drawable.ic_error, stringResource(R.string.settings_low_power_mode_error_not_compatible), error = true)
        }
        MessageCard(R.drawable.ic_about, stringResource(R.string.setting_low_power_mode_content))
    }
}

@Composable
fun ShizukuError(shizukuInstalled: Boolean, onShizuku: () -> Unit, onSui: () -> Unit) {
    if (shizukuInstalled) return
    MessageCard(R.drawable.ic_error, stringResource(R.string.settings_low_power_mode_error_shizuku), error = true) {
        OutlinedButton(onClick = onShizuku) { Text(stringResource(R.string.settings_low_power_mode_error_button_shizuku)) }
        OutlinedButton(onClick = onSui) { Text(stringResource(R.string.settings_low_power_mode_error_button_sui)) }
    }
}
