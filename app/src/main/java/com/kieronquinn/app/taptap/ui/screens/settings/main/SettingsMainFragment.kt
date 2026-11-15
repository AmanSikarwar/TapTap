package com.kieronquinn.app.taptap.ui.screens.settings.main

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kieronquinn.app.taptap.ui.compose.ServiceSwitchCard
import com.kieronquinn.app.taptap.ui.screens.settings.generic.GenericSettingsFragment
import com.kieronquinn.app.taptap.R
import com.kieronquinn.app.taptap.ui.base.CanShowSnackbar
import com.kieronquinn.app.taptap.ui.base.ProvidesOverflow
import com.kieronquinn.app.taptap.ui.screens.container.ContainerSharedViewModel
import com.kieronquinn.app.taptap.ui.screens.settings.generic.GenericSettingsViewModel.SettingsItem
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsMainFragment : GenericSettingsFragment(), CanShowSnackbar, ProvidesOverflow {

    override val viewModel by viewModel<SettingsMainViewModel>()
    private val sharedViewModel by activityViewModel<ContainerSharedViewModel>()

    private val notificationPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ){
        //We don't actually care about the result here
    }

    override val items by lazy {
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

    override val header: @Composable () -> Unit = {
        val running by sharedViewModel.isServiceRunning.collectAsStateWithLifecycle()
        ServiceSwitchCard(running, { sharedViewModel.toggleServiceEnabledState(requireContext()) })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.checkInternetPermission()
        requestNotificationPermissionIfPossible()
    }

    override fun onResume() {
        super.onResume()
        refreshVisibleItems()
    }

    override fun inflateMenu(menuInflater: MenuInflater, menu: Menu) {
        menuInflater.inflate(R.menu.menu_settings, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        when(menuItem.itemId){
            R.id.menu_settings_rerun_setup -> {
                viewModel.onReRunSetupClicked()
                return true
            }
        }
        return false
    }

    private fun requestNotificationPermissionIfPossible() {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return
        notificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
    }

}