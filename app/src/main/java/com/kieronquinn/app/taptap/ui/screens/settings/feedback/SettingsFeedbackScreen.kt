package com.kieronquinn.app.taptap.ui.screens.settings.feedback

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
fun SettingsFeedbackScreen(viewModel: SettingsFeedbackViewModel = koinViewModel()) {
    val context = LocalContext.current
    val items = remember(viewModel) {
        listOf(
            GenericSettingsViewModel.SettingsItem.Switch(
                icon = R.drawable.ic_feedback,
                titleRes = R.string.setting_feedback_vibrate,
                contentRes = R.string.setting_feedback_vibrate_desc,
                setting = viewModel.vibrateSetting
            ),
            GenericSettingsViewModel.SettingsItem.Switch(
                icon = R.drawable.ic_feedback_dnd,
                titleRes = R.string.setting_feedback_override_dnd,
                contentRes = R.string.setting_feedback_override_dnd_desc,
                setting = viewModel.vibrateDNDSetting
            ),
            GenericSettingsViewModel.SettingsItem.Switch(
                icon = R.drawable.ic_feedback_wake_device,
                titleRes = R.string.setting_feedback_wake,
                contentRes = R.string.setting_feedback_wake_desc,
                setting = viewModel.wakeDeviceSetting
            )
        )
    }
    GenericSettingsScreen(viewModel, items)
}
