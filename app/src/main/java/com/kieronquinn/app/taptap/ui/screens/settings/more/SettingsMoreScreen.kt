package com.kieronquinn.app.taptap.ui.screens.settings.more

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
fun SettingsMoreScreen(viewModel: SettingsMoreViewModel = koinViewModel()) {
    val context = LocalContext.current
    val items = remember(viewModel) {
        listOf(
            GenericSettingsViewModel.SettingsItem.Text(
                icon = R.drawable.ic_settings_battery_optimisation,
                titleRes = R.string.battery_and_optimisation,
                contentRes = R.string.battery_and_optimisation_desc,
                onClick = viewModel::onBatteryClicked
            ),
            GenericSettingsViewModel.SettingsItem.Text(
                icon = R.drawable.ic_backup_restore,
                titleRes = R.string.settings_backup_restore,
                contentRes = R.string.settings_backup_restore_desc,
                onClick = viewModel::onBackupRestoreClicked
            ),
            GenericSettingsViewModel.SettingsItem.Switch(
                icon = R.drawable.ic_internet,
                titleRes = R.string.setting_internet,
                contentRes = R.string.setting_internet_desc,
                setting = viewModel.internetAccessEnabled
            ),
            GenericSettingsViewModel.SettingsItem.Switch(
                icon = R.drawable.ic_settings_more_crash_reporting,
                titleRes = R.string.setting_crash_reporting,
                contentRes = R.string.setting_crash_reporting_desc,
                setting = viewModel.crashReportingEnabled
            ),
            GenericSettingsViewModel.SettingsItem.About(
                onContributorsClicked = viewModel::onAboutContributorsClicked,
                onDonateClicked = viewModel::onAboutDonateClicked,
                onGitHubClicked = viewModel::onAboutGitHubClicked,
                onLibrariesClicked = viewModel::onAboutLibrariesClicked,
                onTwitterClicked = viewModel::onAboutTwitterClicked,
                onXdaClicked = viewModel::onAboutXDAClicked
            )
        )
    }
    GenericSettingsScreen(viewModel, items)
}
