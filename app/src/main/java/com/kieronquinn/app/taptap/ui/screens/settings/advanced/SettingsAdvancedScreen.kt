package com.kieronquinn.app.taptap.ui.screens.settings.advanced

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.kieronquinn.app.taptap.R
import com.kieronquinn.app.taptap.ui.screens.settings.generic.GenericSettingsScreen
import com.kieronquinn.app.taptap.ui.screens.settings.generic.GenericSettingsViewModel
import com.kieronquinn.app.taptap.ui.screens.settings.generic.GenericSettingsViewModel.SettingsItem
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SettingsAdvancedScreen(viewModel: SettingsAdvancedViewModel = koinViewModel()) {
    val context = LocalContext.current
    val items = remember(viewModel) {
        listOf(
            SettingsItem.Info(
                contentRes = R.string.setting_advanced_warning_desc
            ),
            SettingsItem.Switch(
                icon = R.drawable.ic_settings_advanced_auto_restart,
                titleRes = R.string.setting_advanced_restart_service,
                contentRes = R.string.setting_advanced_restart_service_desc,
                isVisible = { !viewModel.isLowPowerModeEnabled },
                setting = viewModel.autoRestartServiceSetting
            ),
            SettingsItem.Text(
                icon = R.drawable.ic_gesture_sensitivity,
                titleRes = R.string.setting_advanced_custom_sensitivity,
                contentRes = R.string.setting_advanced_custom_sensitivity_desc,
                isVisible = { !viewModel.isLowPowerModeEnabled },
                onClick = viewModel::onCustomSensitivityClicked
            ),
            SettingsItem.Switch(
                icon = R.drawable.ic_feedback_wake_device,
                titleRes = R.string.setting_advanced_legacy_wake,
                contentRes = R.string.setting_advanced_legacy_wake_desc,
                setting = viewModel.legacyWakeSetting
            )
        )
    }
    GenericSettingsScreen(viewModel, items)
}
