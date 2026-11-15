package com.kieronquinn.app.taptap.ui.screens.settings.options

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kieronquinn.app.taptap.ui.compose.ServiceSwitchCard
import com.kieronquinn.app.taptap.ui.screens.settings.generic.GenericSettingsFragment
import com.kieronquinn.app.taptap.R
import com.kieronquinn.app.taptap.components.settings.TapTapSettings
import com.kieronquinn.app.taptap.ui.base.CanShowSnackbar
import com.kieronquinn.app.taptap.ui.screens.container.ContainerSharedViewModel
import com.kieronquinn.app.taptap.ui.screens.settings.generic.GenericSettingsViewModel.SettingsItem
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsOptionsFragment: GenericSettingsFragment(), CanShowSnackbar {

    override val viewModel by viewModel<SettingsOptionsViewModel>()
    private val sharedViewModel by activityViewModel<ContainerSharedViewModel>()


    override val items by lazy {
        listOf(
            SettingsItem.Text(
                icon = R.drawable.ic_setting_low_power_mode,
                titleRes = R.string.setting_low_power_mode,
                content = { if(viewModel.isLowPowerModeAvailable){
                    getString(R.string.setting_low_power_mode_desc)
                }else{
                    getString(R.string.setting_low_power_mode_desc_unavailable)
                }},
                isEnabled = viewModel::isLowPowerModeAvailable,
                onClick = viewModel::onLowPowerModeClicked
            ),
            SettingsItem.Text(
                icon = R.drawable.ic_settings_native_mode,
                titleRes = R.string.settings_native_mode,
                content = { if(viewModel.isNativeModeAvailable){
                    getString(R.string.settings_native_mode_desc)
                }else{
                    getString(R.string.settings_native_mode_desc_unavailable)
                }},
                isVisible = viewModel::isLowPowerModeSupported,
                isEnabled = viewModel::isNativeModeAvailable,
                onClick = viewModel::onNativeModeClicked
            ),
            SettingsItem.Text(
                icon = R.drawable.ic_settings_device_model,
                titleRes = R.string.setting_gesture_model,
                content = {
                    when {
                        viewModel.isLowPowerModeEnabled -> {
                            getString(R.string.setting_gesture_model_desc_not_available)
                        }
                        viewModel.isNativeModeEnabled -> {
                            getString(R.string.setting_gesture_model_desc_not_available_native)
                        }
                        else -> {
                            getString(R.string.setting_gesture_model_desc)
                        }
                    }
                },
                onClick = viewModel::onModelClicked,
                isEnabled = { !viewModel.isLowPowerModeEnabled && !viewModel.isNativeModeEnabled }
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
                isVisible = { !viewModel.isLowPowerModeEnabled && !viewModel.isNativeModeEnabled },
                labelFormatter = {
                    getString(when {
                        it < 2 -> R.string.slider_sensitivity_very_low
                        it < 4 -> R.string.slider_sensitivity_low
                        it < 6 -> R.string.slider_sensitivity_medium
                        it < 8 -> R.string.slider_sensitivity_high
                        else -> R.string.slider_sensitivity_very_high
                    })
                }
            ),
            SettingsItem.Switch(
                icon = R.drawable.ic_gesture_sensitivity_lower,
                titleRes = R.string.setting_gesture_sensitivity_chre,
                content = { getString(R.string.setting_gesture_sensitivity_chre_desc) },
                setting = viewModel.sensitivitySettingCHRE,
                isVisible = { viewModel.isLowPowerModeEnabled && !viewModel.isNativeModeEnabled }
            ),
            SettingsItem.Text(
                icon = R.drawable.ic_settings_advanced,
                titleRes = R.string.setting_advanced,
                contentRes = R.string.setting_advanced_desc,
                onClick = viewModel::onAdvancedClicked
            )
        )
    }

    override val header: @Composable () -> Unit = {
        val running by sharedViewModel.isServiceRunning.collectAsStateWithLifecycle()
        ServiceSwitchCard(running, { sharedViewModel.toggleServiceEnabledState(requireContext()) })
    }

    override fun onResume() {
        super.onResume()
        refreshVisibleItems()
    }


}