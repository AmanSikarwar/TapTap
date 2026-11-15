package com.kieronquinn.app.taptap.ui.screens.settings.actions.selector

import android.Manifest
import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import com.google.gson.Gson
import com.kieronquinn.app.taptap.components.navigation.NavigationResults
import com.kieronquinn.app.taptap.components.navigation.NavigationResults.Key
import com.kieronquinn.app.taptap.models.action.ActionDataTypes
import com.kieronquinn.app.taptap.models.action.ActionRequirement
import com.kieronquinn.app.taptap.models.action.TapTapActionDirectory
import com.kieronquinn.app.taptap.models.action.TapTapUIAction
import com.kieronquinn.app.taptap.models.columbus.AppShortcutData
import com.kieronquinn.app.taptap.models.shared.SharedArgument
import com.kieronquinn.app.taptap.repositories.quicksettings.QuickSettingsRepository
import com.kieronquinn.app.taptap.ui.screens.container.ContainerSharedViewModel
import com.kieronquinn.app.taptap.ui.screens.settings.shared.RequirementRequester
import com.kieronquinn.app.taptap.ui.screens.settings.shared.rememberRequirementRequester
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinActivityViewModel

class ActionAddFlow(
    private val context: Context,
    private val scope: CoroutineScope,
    private val viewModel: SettingsActionsAddGenericViewModel,
    private val requester: RequirementRequester,
    private val results: NavigationResults,
    private val sharedViewModel: ContainerSharedViewModel,
    private val launchTaskerPicker: () -> Unit
) {

    fun onActionClicked(action: TapTapActionDirectory) {
        scope.launch { handleAction(action) }
    }

    fun onChipClicked(requirement: ActionRequirement.UserDisplayedActionRequirement) {
        sharedViewModel.showSnackbar(context.getText(requirement.desc))
    }

    suspend fun handleAction(
        action: TapTapActionDirectory,
        extraData: String? = null,
        isReturningData: Boolean = false,
        isReturningRequirement: Boolean = false
    ) {
        if (!handleActionRequirement(action, isReturningRequirement)) return
        if (action.dataType != null && !viewModel.isActionDataSatisfied(context, action.dataType, extraData ?: "")) {
            if (isReturningData) return
            handleActionData(action)
            return
        }
        val description = viewModel.getFormattedDescriptionForAction(context, action, extraData)
        //Index & id are handled later
        results.send(Key.ACTION, TapTapUIAction(action, -1, -1, extraData ?: "", description, 0))
        viewModel.unwindToActions()
    }

    private suspend fun handleActionRequirement(action: TapTapActionDirectory, isReturning: Boolean): Boolean {
        val requirements = action.actionRequirement ?: return true
        return requirements.all { requirement ->
            when (requirement) {
                is ActionRequirement.AnswerPhoneCallsPermission ->
                    requester.requestPermission(Manifest.permission.ANSWER_PHONE_CALLS, Manifest.permission.READ_PHONE_STATE)
                is ActionRequirement.CameraPermission -> requester.requestPermission(Manifest.permission.CAMERA)
                is ActionRequirement.AccessNotificationPolicyPermission -> requester.requestNotificationPolicy()
                is ActionRequirement.Accessibility -> requester.requestAccessibilityService()
                is ActionRequirement.GestureAccessibility -> requester.requestGestureService()
                is ActionRequirement.DrawOverOtherAppsPermission -> requester.requestDisplayOverOtherApps()
                is ActionRequirement.TaskerPermission -> requester.requestTaskerPermission()
                is ActionRequirement.WriteSystemSettingsPermission -> requester.requestWriteSystemSettings()
                is ActionRequirement.Shizuku -> {
                    if (isReturning) return@all true
                    viewModel.showShizukuPermission(action)
                    return false
                }
                is ActionRequirement.Snapchat -> {
                    if (isReturning) return@all true
                    viewModel.showSnapchatFlow(action)
                    return false
                }
                is ActionRequirement.Root -> viewModel.checkRoot().also { if (!it) viewModel.showNoRoot() }
                is ActionRequirement.Tasker -> true
                is ActionRequirement.UserDisplayedActionRequirement, is ActionRequirement.Permission ->
                    throw NotImplementedError("Requirement $requirement is not implemented")
            }
        }
    }

    private fun handleActionData(action: TapTapActionDirectory) {
        when (action.dataType) {
            ActionDataTypes.PACKAGE_NAME -> viewModel.showAppPicker(action)
            ActionDataTypes.APP_SHORTCUT -> viewModel.showAppShortcutPicker()
            ActionDataTypes.SHORTCUT -> viewModel.showShortcutPicker()
            ActionDataTypes.TASKER_TASK -> launchTaskerPicker()
            ActionDataTypes.QUICK_SETTING -> viewModel.showQuickSettingPicker()
            else -> {}
        }
    }

}

@Composable
fun rememberActionAddFlow(viewModel: SettingsActionsAddGenericViewModel): ActionAddFlow {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val requester = rememberRequirementRequester()
    val results = koinInject<NavigationResults>()
    val gson = koinInject<Gson>()
    val sharedViewModel = koinActivityViewModel<ContainerSharedViewModel>()
    lateinit var flow: ActionAddFlow
    val tasker = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        val taskName = it.data?.dataString ?: return@rememberLauncherForActivityResult
        scope.launch { flow.handleAction(TapTapActionDirectory.TASKER_TASK, taskName, isReturningData = true) }
    }
    flow = remember(viewModel, requester) {
        ActionAddFlow(context, scope, viewModel, requester, results, sharedViewModel) {
            viewModel.showTaskerTaskPicker(tasker)
        }
    }
    LaunchedEffect(flow) {
        launch {
            results.receive<AppShortcutData>(Key.APP_SHORTCUT).collect {
                flow.handleAction(TapTapActionDirectory.LAUNCH_APP_SHORTCUT, gson.toJson(it), isReturningRequirement = true)
            }
        }
        launch {
            results.receive<String>(Key.SHORTCUT).collect {
                flow.handleAction(TapTapActionDirectory.LAUNCH_SHORTCUT, it, isReturningRequirement = true)
            }
        }
        launch {
            results.receive<QuickSettingsRepository.QuickSetting>(Key.QUICK_SETTING).collect {
                flow.handleAction(TapTapActionDirectory.QUICK_SETTING, it.component.flattenToString(), isReturningRequirement = true)
            }
        }
        launch {
            results.receive<Pair<SharedArgument, String>>(Key.PACKAGE).collect { (argument, packageName) ->
                flow.handleAction(argument.action ?: return@collect, packageName, isReturningRequirement = true)
            }
        }
        launch {
            results.receive<Pair<SharedArgument, Boolean>>(Key.SHIZUKU).collect { (argument, granted) ->
                if (granted) flow.handleAction(argument.action ?: return@collect, isReturningRequirement = true)
            }
        }
        launch {
            results.receive<Pair<SharedArgument, Boolean>>(Key.SNAPCHAT).collect { (argument, available) ->
                if (available) flow.handleAction(argument.action ?: return@collect, isReturningRequirement = true)
            }
        }
    }
    return flow
}
