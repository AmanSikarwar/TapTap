package com.kieronquinn.app.taptap.ui.screens.settings.actions.selector

import com.kieronquinn.app.taptap.ui.navigation.*

import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kieronquinn.app.taptap.R
import com.kieronquinn.app.taptap.components.navigation.ContainerNavigation
import com.kieronquinn.app.taptap.models.action.ActionDataTypes
import com.kieronquinn.app.taptap.models.action.ActionRequirement
import com.kieronquinn.app.taptap.models.action.ActionSupportedRequirement
import com.kieronquinn.app.taptap.models.action.TapTapActionDirectory
import com.kieronquinn.app.taptap.models.shared.SharedArgument
import com.kieronquinn.app.taptap.repositories.actions.ActionsRepository
import com.kieronquinn.app.taptap.utils.extensions.Shell_isRooted
import com.topjohnwu.superuser.Shell
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.dinglisch.android.tasker.TaskerIntent

abstract class SettingsActionsAddGenericViewModel: ViewModel() {

    abstract fun showAppShortcutPicker()
    abstract fun showShortcutPicker()
    abstract fun showShizukuPermission(action: TapTapActionDirectory)
    abstract fun showSnapchatFlow(action: TapTapActionDirectory)
    abstract fun showAppPicker(action: TapTapActionDirectory)
    abstract fun showTaskerTaskPicker(contract: ActivityResultLauncher<Intent>)
    abstract fun showQuickSettingPicker()
    abstract fun showNoRoot()
    abstract fun unwindToActions()
    abstract suspend fun checkRoot(): Boolean
    abstract fun getFormattedDescriptionForAction(context: Context, action: TapTapActionDirectory, data: String?): CharSequence
    abstract fun isActionDataSatisfied(context: Context, data: ActionDataTypes, extraData: String): Boolean
    abstract fun getActionSupportedRequirement(context: Context, action: TapTapActionDirectory): ActionSupportedRequirement?

}

abstract class SettingsActionsAddGenericViewModelImpl(private val navigation: ContainerNavigation, private val actionsRepository: ActionsRepository): SettingsActionsAddGenericViewModel() {

    override fun unwindToActions() {
        viewModelScope.launch {
            navigation.navigateUpTo(ContainerKey.ActionsAddCategory::class, true)
        }
    }

    override fun showAppShortcutPicker() {
        viewModelScope.launch {
            navigation.navigate(ContainerKey.AppShortcutPicker)
        }
    }

    override fun showShortcutPicker() {
        viewModelScope.launch {
            navigation.navigate(ContainerKey.ShortcutPicker)
        }
    }

    override fun showShizukuPermission(action: TapTapActionDirectory) {
        viewModelScope.launch {
            navigation.navigate(ContainerKey.ShizukuPermission(SharedArgument(action = action)))
        }
    }

    override fun showSnapchatFlow(action: TapTapActionDirectory) {
        viewModelScope.launch {
            navigation.navigate(ContainerKey.Snapchat(SharedArgument(action = action)))
        }
    }

    override fun showAppPicker(action: TapTapActionDirectory) {
        viewModelScope.launch {
            navigation.navigate(ContainerKey.PackagePicker(SharedArgument(action = action), R.string.action_launch_app, false))
        }
    }

    override fun showTaskerTaskPicker(contract: ActivityResultLauncher<Intent>) {
        contract.launch(TaskerIntent.getTaskSelectIntent())
    }

    override suspend fun checkRoot(): Boolean {
        return withContext(Dispatchers.IO){
            Shell_isRooted()
        }
    }

    override fun showQuickSettingPicker() {
        viewModelScope.launch {
            navigation.navigate(ContainerKey.QuickSettingPicker)
        }
    }

    override fun showNoRoot() {
        viewModelScope.launch {
            navigation.navigate(ContainerKey.NoRoot)
        }
    }

    override fun isActionDataSatisfied(context: Context, data: ActionDataTypes, extraData: String): Boolean {
        return actionsRepository.isActionDataSatisfied(context, data, extraData)
    }

    override fun getActionSupportedRequirement(context: Context, action: TapTapActionDirectory): ActionSupportedRequirement? {
        return actionsRepository.getUnsupportedReason(context, action)
    }

    override fun getFormattedDescriptionForAction(
        context: Context,
        action: TapTapActionDirectory,
        data: String?
    ): CharSequence {
        return actionsRepository.getFormattedDescriptionForAction(context, action, data)
    }

}