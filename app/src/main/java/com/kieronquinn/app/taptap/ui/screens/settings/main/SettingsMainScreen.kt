package com.kieronquinn.app.taptap.ui.screens.settings.main

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kieronquinn.app.taptap.R
import com.kieronquinn.app.taptap.ui.compose.OverflowMenu
import com.kieronquinn.app.taptap.ui.compose.ServiceSwitchCard
import com.kieronquinn.app.taptap.ui.compose.TopBarActions
import com.kieronquinn.app.taptap.ui.screens.container.ContainerSharedViewModel
import com.kieronquinn.app.taptap.ui.screens.settings.generic.GenericSettingsScreen
import com.kieronquinn.app.taptap.ui.screens.settings.generic.GenericSettingsViewModel.SettingsItem
import org.koin.compose.viewmodel.koinActivityViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SettingsMainScreen(viewModel: SettingsMainViewModel = koinViewModel()) {
    val context = LocalContext.current
    val sharedViewModel = koinActivityViewModel<ContainerSharedViewModel>()
    val notificationPermission = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {}
    LaunchedEffect(viewModel) {
        viewModel.checkInternetPermission()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
    TopBarActions {
        OverflowMenu(listOf(stringResource(R.string.menu_setup_wizard) to viewModel::onReRunSetupClicked))
    }
    val items = remember(viewModel) {
        listOf(
            SettingsItem.Info(
                icon = R.drawable.ic_settings_battery_optimisation,
                contentRes = R.string.settings_main_battery,
                isVisible = { !viewModel.batteryOptimisationDisabled },
                onClick = viewModel::onBatteryOptimisationClicked
            ),
            SettingsItem.Text(
                icon = R.drawable.ic_actions,
                titleRes = R.string.setting_actions_double,
                contentRes = R.string.settings_actions_desc_double,
                onClick = viewModel::onDoubleTapActionsClicked
            ),
            SettingsItem.Text(
                icon = R.drawable.ic_actions_triple,
                titleRes = R.string.setting_actions_triple,
                contentRes = R.string.settings_actions_desc_triple,
                onClick = viewModel::onTripleTapActionsClicked
            ),
            SettingsItem.Text(
                icon = R.drawable.ic_gates,
                titleRes = R.string.setting_gates,
                contentRes = R.string.setting_gates_desc,
                onClick = viewModel::onGatesClicked
            ),
            SettingsItem.Text(
                icon = R.drawable.ic_feedback,
                titleRes = R.string.setting_feedback,
                contentRes = R.string.setting_feedback_desc,
                onClick = viewModel::onFeedbackClicked
            )
        )
    }
    GenericSettingsScreen(viewModel, items, header = {
        val running by sharedViewModel.isServiceRunning.collectAsStateWithLifecycle()
        ServiceSwitchCard(running, { sharedViewModel.toggleServiceEnabledState(context) })
    })
}
