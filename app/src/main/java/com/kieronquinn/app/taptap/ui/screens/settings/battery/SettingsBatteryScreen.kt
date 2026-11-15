package com.kieronquinn.app.taptap.ui.screens.settings.battery

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kieronquinn.app.taptap.R
import com.kieronquinn.app.taptap.ui.screens.settings.generic.GenericSettingsScreen
import com.kieronquinn.app.taptap.ui.screens.settings.generic.GenericSettingsViewModel
import com.kieronquinn.app.taptap.ui.screens.settings.generic.GenericSettingsViewModel.SettingsItem
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SettingsBatteryScreen(viewModel: SettingsBatteryViewModel = koinViewModel()) {
    val context = LocalContext.current
    val showHeader by viewModel.showHeader.collectAsStateWithLifecycle()
    val items = remember(viewModel) {
        listOf(
            SettingsItem.Info(
                contentRes = R.string.settings_battery_header,
                linkClicked = viewModel::onHeaderLinkClicked,
                icon = R.drawable.ic_warning,
                onDismissClicked = viewModel::onHeaderDismissed,
                isVisible = { viewModel.showHeader.value }
            ),
            SettingsItem.Info(
                contentRes = R.string.settings_battery_info,
                linkClicked = viewModel::onDontKillLinkClicked
            ),
            SettingsItem.Switch(
                titleRes = R.string.settings_battery_disable_optimisation,
                content = {
                    if (viewModel.batteryOptimisationDisabled.value) {
                        context.getString(R.string.settings_battery_disable_optimisation_desc_disabled)
                    } else {
                        context.getString(R.string.settings_battery_disable_optimisation_desc_enabled)
                    }
                },
                isEnabled = { !viewModel.batteryOptimisationDisabled.value },
                setting = viewModel.batteryOptimisationSetting,
                icon = R.drawable.ic_settings_battery_android
            ),
            SettingsItem.Info(
                isVisible = { viewModel.oemBatteryAvailable },
                contentRes = R.string.settings_battery_oem_info
            ),
            SettingsItem.Text(
                titleRes = R.string.settings_battery_oem_button,
                isVisible = { viewModel.oemBatteryAvailable },
                icon = R.drawable.ic_settings_battery_oem,
                onClick = { viewModel.onOemClicked(context) }
            )
        )
    }
    GenericSettingsScreen(viewModel, items, refreshKey = showHeader, onResume = viewModel::onResume)
}
