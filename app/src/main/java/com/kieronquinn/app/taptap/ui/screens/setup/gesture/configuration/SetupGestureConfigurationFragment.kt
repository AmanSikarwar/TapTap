package com.kieronquinn.app.taptap.ui.screens.setup.gesture.configuration

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.runtime.Composable
import com.kieronquinn.app.taptap.R
import com.kieronquinn.app.taptap.components.settings.TapTapSettings
import com.kieronquinn.app.taptap.ui.screens.settings.generic.GenericSettingsFragment
import com.kieronquinn.app.taptap.ui.screens.settings.generic.GenericSettingsViewModel.SettingsItem
import com.kieronquinn.app.taptap.utils.extensions.getText
import org.koin.androidx.viewmodel.ext.android.viewModel

class SetupGestureConfigurationFragment: GenericSettingsFragment() {

    override val viewModel by viewModel<SetupGestureConfigurationViewModel>()

    override val items by lazy {
        listOf(
            SettingsItem.Text(
                icon = R.drawable.ic_settings_device_model,
                titleRes = R.string.setting_gesture_model,
                content = {
                    requireContext().getText(R.string.setup_gesture_device_size, getString(viewModel.selectedModel))
                },
                onClick = viewModel::onDeviceSizeClicked
            ),
            SettingsItem.Slider(
                icon = R.drawable.ic_gesture_sensitivity,
                titleRes = R.string.setting_gesture_sensitivity,
                content = { if(viewModel.isCustomSensitivitySet) {
                    getString(R.string.setting_gesture_sensitivity_desc_disabled)
                }else{
                    getString(R.string.setting_gesture_sensitivity_desc)
                }},
                setting = viewModel.sensitivitySetting as TapTapSettings.TapTapSetting<Number>,
                stepSize = 1.0f,
                isEnabled = { !viewModel.isCustomSensitivitySet },
                labelFormatter = {
                    getString(when {
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

    @Composable
    override fun contentPadding() = WindowInsets.navigationBars.asPaddingValues()

}