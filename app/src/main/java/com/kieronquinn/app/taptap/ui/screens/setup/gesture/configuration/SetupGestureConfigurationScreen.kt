package com.kieronquinn.app.taptap.ui.screens.setup.gesture.configuration

import com.kieronquinn.app.taptap.utils.extensions.getHtmlText
import androidx.compose.ui.platform.LocalResources
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.kieronquinn.app.taptap.R
import com.kieronquinn.app.taptap.components.settings.TapTapSettings
import com.kieronquinn.app.taptap.ui.screens.settings.generic.GenericSettingsScreen
import com.kieronquinn.app.taptap.ui.screens.settings.generic.GenericSettingsViewModel.SettingsItem
import com.kieronquinn.app.taptap.utils.extensions.getText
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SetupGestureConfigurationScreen(viewModel: SetupGestureConfigurationViewModel = koinViewModel()) {
    val context = LocalContext.current
    val resources = LocalResources.current
    val items = remember(viewModel) {
        listOf(
            SettingsItem.Text(
                icon = R.drawable.ic_settings_device_model,
                titleRes = R.string.setting_gesture_model,
                content = {
                    resources.getHtmlText(R.string.setup_gesture_device_size, resources.getString(viewModel.selectedModel))
                },
                onClick = viewModel::onDeviceSizeClicked
            ),
            SettingsItem.Slider(
                icon = R.drawable.ic_gesture_sensitivity,
                titleRes = R.string.setting_gesture_sensitivity,
                content = { if(viewModel.isCustomSensitivitySet) {
                    resources.getString(R.string.setting_gesture_sensitivity_desc_disabled)
                }else{
                    resources.getString(R.string.setting_gesture_sensitivity_desc)
                }},
                setting = viewModel.sensitivitySetting as TapTapSettings.TapTapSetting<Number>,
                stepSize = 1.0f,
                isEnabled = { !viewModel.isCustomSensitivitySet },
                labelFormatter = {
                    resources.getString(when {
                        it < 2 -> R.string.slider_sensitivity_very_low
                        it < 4 -> R.string.slider_sensitivity_low
                        it < 6 -> R.string.slider_sensitivity_medium
                        it < 8 -> R.string.slider_sensitivity_high
                        else -> R.string.slider_sensitivity_very_high
                    })
                }
            )
        )
    }
    GenericSettingsScreen(viewModel, items, contentPadding = WindowInsets.navigationBars.asPaddingValues())
}
