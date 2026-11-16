package com.kieronquinn.app.taptap.ui.navigation

import androidx.compose.ui.platform.LocalResources
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.window.DialogProperties
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.scene.DialogSceneStrategy
import com.kieronquinn.app.taptap.R
import com.kieronquinn.app.taptap.components.navigation.BaseNavigation
import com.kieronquinn.app.taptap.components.navigation.ContainerNavigation
import com.kieronquinn.app.taptap.ui.compose.SheetMessage
import com.kieronquinn.app.taptap.ui.screens.container.ContainerSharedViewModel
import com.kieronquinn.app.taptap.ui.screens.settings.actions.SettingsActionsRoute
import com.kieronquinn.app.taptap.ui.screens.settings.actions.doubletap.SettingsActionsDoubleViewModel
import com.kieronquinn.app.taptap.ui.screens.settings.actions.selector.SettingsActionsActionSelectorScreen
import com.kieronquinn.app.taptap.ui.screens.settings.actions.selector.SettingsActionsAddCategorySelectorScreen
import com.kieronquinn.app.taptap.ui.screens.settings.actions.selector.SettingsActionsAddCategorySelectorViewModel
import com.kieronquinn.app.taptap.ui.screens.settings.actions.selector.actions.SettingsActionsActionSelectorViewModel
import com.kieronquinn.app.taptap.ui.screens.settings.actions.selector.rememberActionAddFlow
import com.kieronquinn.app.taptap.ui.screens.settings.actions.tripletap.SettingsActionsTripleViewModel
import com.kieronquinn.app.taptap.ui.screens.settings.actions.whengates.SettingsActionsWhenGatesRoute
import com.kieronquinn.app.taptap.ui.screens.settings.advanced.SettingsAdvancedScreen
import com.kieronquinn.app.taptap.ui.screens.settings.advanced.customsensitivity.CustomSensitivitySheet
import com.kieronquinn.app.taptap.ui.screens.settings.backuprestore.SettingsBackupRestoreScreen
import com.kieronquinn.app.taptap.ui.screens.settings.backuprestore.backup.SettingsBackupRestoreBackupRoute
import com.kieronquinn.app.taptap.ui.screens.settings.backuprestore.restore.SettingsBackupRestoreRestoreRoute
import com.kieronquinn.app.taptap.ui.screens.settings.battery.SettingsBatteryScreen
import com.kieronquinn.app.taptap.ui.screens.settings.contributions.SettingsContributionsScreen
import com.kieronquinn.app.taptap.ui.screens.settings.feedback.SettingsFeedbackScreen
import com.kieronquinn.app.taptap.ui.screens.settings.gates.SettingsGatesRoute
import com.kieronquinn.app.taptap.ui.screens.settings.gates.selector.SettingsGatesAddCategorySelectorScreen
import com.kieronquinn.app.taptap.ui.screens.settings.gates.selector.SettingsGatesAddCategorySelectorViewModel
import com.kieronquinn.app.taptap.ui.screens.settings.gates.selector.SettingsGatesGateSelectorScreen
import com.kieronquinn.app.taptap.ui.screens.settings.gates.selector.gates.SettingsGatesGateSelectorViewModel
import com.kieronquinn.app.taptap.ui.screens.settings.gates.selector.rememberGateAddFlow
import com.kieronquinn.app.taptap.ui.screens.settings.lowpower.SettingsLowPowerModeScreen
import com.kieronquinn.app.taptap.ui.screens.settings.lowpower.shizukuinfo.SettingsLowPowerModeShizukuInfoScreen
import com.kieronquinn.app.taptap.ui.screens.settings.main.SettingsMainScreen
import com.kieronquinn.app.taptap.ui.screens.settings.modelpicker.SettingsModelPickerScreen
import com.kieronquinn.app.taptap.ui.screens.settings.more.SettingsMoreScreen
import com.kieronquinn.app.taptap.ui.screens.settings.nativemode.SettingsNativeModeRoute
import com.kieronquinn.app.taptap.ui.screens.settings.options.SettingsOptionsScreen
import com.kieronquinn.app.taptap.ui.screens.settings.shared.internet.InternetPermissionDialog
import com.kieronquinn.app.taptap.ui.screens.settings.shared.internet.SettingsSharedInternetPermissionDialogViewModel
import com.kieronquinn.app.taptap.ui.screens.settings.shared.selector.appshortcuts.SettingsSharedAppShortcutsSelectorRoute
import com.kieronquinn.app.taptap.ui.screens.settings.shared.selector.packagename.SettingsSharedPackageSelectorRoute
import com.kieronquinn.app.taptap.ui.screens.settings.shared.selector.quicksetting.SettingsSharedQuickSettingSelectorRoute
import com.kieronquinn.app.taptap.ui.screens.settings.shared.selector.shortcuts.SettingsSharedShortcutsSelectorRoute
import com.kieronquinn.app.taptap.ui.screens.settings.shared.shizuku.SettingsSharedShizukuPermissionFlowRoute
import com.kieronquinn.app.taptap.ui.screens.settings.shared.snapchat.SettingsSharedSnapchatRoute
import com.kieronquinn.app.taptap.ui.screens.settings.update.SettingsUpdateRoute
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinActivityViewModel
import org.koin.compose.viewmodel.koinViewModel

val NonDismissableDialog = DialogSceneStrategy.dialog(DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false))

@Composable
fun restartService(): () -> Unit {
    val context = LocalContext.current
    val shared = koinActivityViewModel<ContainerSharedViewModel>()
    return { shared.restartService(context) }
}

@Composable
fun back(navigation: BaseNavigation): () -> Unit {
    val scope = rememberCoroutineScope()
    return { scope.launch { navigation.navigateBack() } }
}

@Composable
fun InternetPermissionEntry(navigation: BaseNavigation) {
    val viewModel = koinViewModel<SettingsSharedInternetPermissionDialogViewModel>()
    val close = back(navigation)
    LaunchedEffect(viewModel) { viewModel.dismissBus.collect { close() } }
    InternetPermissionDialog(viewModel)
}

@Composable
private fun HelpSheet(title: Int, content: Int, navigation: BaseNavigation) {
    SheetMessage(
        title = stringResource(title),
        text = LocalResources.current.getText(content),
        positive = stringResource(android.R.string.ok),
        onPositive = back(navigation)
    )
}

fun containerEntries(context: Context): (Any) -> NavEntry<Any> = entryProvider {
    entry<ContainerKey.Main> { SettingsMainScreen() }
    entry<ContainerKey.Options> { SettingsOptionsScreen() }
    entry<ContainerKey.More> { SettingsMoreScreen() }
    entry<ContainerKey.Feedback> { SettingsFeedbackScreen() }
    entry<ContainerKey.ActionsDouble> { SettingsActionsRoute(koinViewModel<SettingsActionsDoubleViewModel>(), false) }
    entry<ContainerKey.ActionsTriple> { SettingsActionsRoute(koinViewModel<SettingsActionsTripleViewModel>(), true) }
    entry<ContainerKey.ActionsHelp>(metadata = BottomSheetSceneStrategy.bottomSheet()) {
        HelpSheet(R.string.bs_help_action_title, R.string.bs_help_action, koinInject<ContainerNavigation>())
    }
    entry<ContainerKey.ActionsAddCategory> {
        val viewModel = koinViewModel<SettingsActionsAddCategorySelectorViewModel>()
        val flow = rememberActionAddFlow(viewModel)
        SettingsActionsAddCategorySelectorScreen(viewModel, flow::onActionClicked, flow::onChipClicked)
    }
    entry<ContainerKey.ActionsSelector> { key ->
        val viewModel = koinViewModel<SettingsActionsActionSelectorViewModel>()
        val flow = rememberActionAddFlow(viewModel)
        LaunchedEffect(key) { viewModel.setupWithCategory(key.category, context) }
        SettingsActionsActionSelectorScreen(viewModel, flow::onActionClicked, flow::onChipClicked)
    }
    entry<ContainerKey.WhenGates> { SettingsActionsWhenGatesRoute(it.action, it.isTriple) }
    entry<ContainerKey.Gates> { SettingsGatesRoute() }
    entry<ContainerKey.GatesHelp>(metadata = BottomSheetSceneStrategy.bottomSheet()) {
        HelpSheet(R.string.bs_help_gate_title, R.string.bs_help_gate, koinInject<ContainerNavigation>())
    }
    entry<ContainerKey.GatesAddCategory> { key ->
        val viewModel = koinViewModel<SettingsGatesAddCategorySelectorViewModel>()
        val flow = rememberGateAddFlow(viewModel)
        SettingsGatesAddCategorySelectorScreen(viewModel, key.isRequirement, flow::onGateClicked, flow::onChipClicked)
    }
    entry<ContainerKey.GatesSelector> { key ->
        val viewModel = koinViewModel<SettingsGatesGateSelectorViewModel>()
        val flow = rememberGateAddFlow(viewModel)
        LaunchedEffect(key) { viewModel.setupWithCategory(key.category, context) }
        SettingsGatesGateSelectorScreen(viewModel, key.isRequirement, flow::onGateClicked, flow::onChipClicked)
    }
    entry<ContainerKey.LowPower> { SettingsLowPowerModeScreen(koinViewModel(), restartService()) }
    entry<ContainerKey.ShizukuInfo> { SettingsLowPowerModeShizukuInfoScreen(koinViewModel()) }
    entry<ContainerKey.NativeMode> { SettingsNativeModeRoute() }
    entry<ContainerKey.Advanced> { SettingsAdvancedScreen() }
    entry<ContainerKey.CustomSensitivity>(metadata = BottomSheetSceneStrategy.bottomSheet()) {
        CustomSensitivitySheet(koinViewModel(), restartService())
    }
    entry<ContainerKey.ModelPicker> { SettingsModelPickerScreen(koinViewModel(), restartService()) }
    entry<ContainerKey.Battery> { SettingsBatteryScreen() }
    entry<ContainerKey.BackupRestore> { SettingsBackupRestoreScreen(koinViewModel()) }
    entry<ContainerKey.Backup> { SettingsBackupRestoreBackupRoute(it.uri) }
    entry<ContainerKey.Restore> { SettingsBackupRestoreRestoreRoute(it.uri) }
    entry<ContainerKey.Contributions> { SettingsContributionsScreen() }
    entry<ContainerKey.Update> { SettingsUpdateRoute(it.release) }
    entry<ContainerKey.InternetPermission>(metadata = NonDismissableDialog) {
        InternetPermissionEntry(koinInject<ContainerNavigation>())
    }
    entry<ContainerKey.NoRoot>(metadata = BottomSheetSceneStrategy.bottomSheet()) {
        HelpSheet(R.string.bs_no_root_title, R.string.bs_no_root_content, koinInject<ContainerNavigation>())
    }
    entry<ContainerKey.PackagePicker> { SettingsSharedPackageSelectorRoute(it.argument, it.showAllApps) }
    entry<ContainerKey.ShortcutPicker> { SettingsSharedShortcutsSelectorRoute() }
    entry<ContainerKey.AppShortcutPicker> { SettingsSharedAppShortcutsSelectorRoute() }
    entry<ContainerKey.QuickSettingPicker> { SettingsSharedQuickSettingSelectorRoute() }
    entry<ContainerKey.ShizukuPermission> { SettingsSharedShizukuPermissionFlowRoute(it.argument) }
    entry<ContainerKey.Snapchat> { SettingsSharedSnapchatRoute(it.argument) }
}
