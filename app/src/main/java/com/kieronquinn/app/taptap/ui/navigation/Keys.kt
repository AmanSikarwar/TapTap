package com.kieronquinn.app.taptap.ui.navigation

import android.net.Uri
import androidx.annotation.StringRes
import com.kieronquinn.app.taptap.R
import com.kieronquinn.app.taptap.models.action.TapTapActionCategory
import com.kieronquinn.app.taptap.models.action.TapTapUIAction
import com.kieronquinn.app.taptap.models.gate.TapTapGateCategory
import com.kieronquinn.app.taptap.models.shared.SharedArgument
import com.kieronquinn.app.taptap.models.update.Release

sealed interface RootKey {
    data object Decision: RootKey
    data object SetupLanding: RootKey
    data object SetupInfo: RootKey
    data object SetupInfoWarning: RootKey
    data object SetupNotifications: RootKey
    data object SetupGesture: RootKey
    data object SetupComplete: RootKey
    data object SetupUpgrade: RootKey
    data object NoGyroscope: RootKey
    data object DisableColumbus: RootKey
    data object InternetPermission: RootKey
    data object Container: RootKey
}

sealed class ContainerKey(@param:StringRes val titleRes: Int? = null) {
    data object Main: ContainerKey(R.string.app_name)
    data object Options: ContainerKey(R.string.setting_bottom_nav_settings)
    data object More: ContainerKey(R.string.setting_bottom_nav_more)
    data object Feedback: ContainerKey(R.string.setting_feedback)
    data object ActionsDouble: ContainerKey(R.string.setting_actions_double)
    data object ActionsTriple: ContainerKey(R.string.setting_actions_triple)
    data object ActionsHelp: ContainerKey()
    data object ActionsAddCategory: ContainerKey(R.string.fab_add_action)
    data class ActionsSelector(val category: TapTapActionCategory): ContainerKey(category.labelRes)
    data class WhenGates(val action: TapTapUIAction, val isTriple: Boolean): ContainerKey(R.string.settings_when_gates_title)
    data object Gates: ContainerKey(R.string.setting_gates)
    data object GatesHelp: ContainerKey()
    data class GatesAddCategory(val isRequirement: Boolean): ContainerKey(if (isRequirement) R.string.fab_add_requirement else R.string.fab_add_gate)
    data class GatesSelector(val category: TapTapGateCategory, val isRequirement: Boolean): ContainerKey(category.labelRes)
    data object LowPower: ContainerKey(R.string.setting_low_power_mode)
    data object ShizukuInfo: ContainerKey(R.string.settings_shared_shizuku_permission_flow_toolbar_title)
    data object NativeMode: ContainerKey(R.string.settings_native_mode)
    data object Advanced: ContainerKey(R.string.setting_advanced)
    data object CustomSensitivity: ContainerKey()
    data object ModelPicker: ContainerKey(R.string.setting_gesture_model)
    data object Battery: ContainerKey(R.string.battery_and_optimisation)
    data object BackupRestore: ContainerKey(R.string.settings_backup_restore)
    data class Backup(val uri: Uri): ContainerKey(R.string.settings_backup_restore_backup)
    data class Restore(val uri: Uri): ContainerKey(R.string.settings_backup_restore_restore)
    data object Contributions: ContainerKey(R.string.about_contributors)
    data class Update(val release: Release): ContainerKey(R.string.snackbar_update)
    data object InternetPermission: ContainerKey()
    data object NoRoot: ContainerKey()
    data class PackagePicker(val argument: SharedArgument, @param:StringRes val title: Int, val showAllApps: Boolean): ContainerKey(title)
    data object ShortcutPicker: ContainerKey(R.string.action_launch_shortcut)
    data object AppShortcutPicker: ContainerKey(R.string.action_launch_app_shortcut)
    data object QuickSettingPicker: ContainerKey(R.string.action_click_quick_setting)
    data class ShizukuPermission(val argument: SharedArgument): ContainerKey(R.string.settings_shared_shizuku_permission_flow_toolbar_title)
    data class Snapchat(val argument: SharedArgument): ContainerKey(R.string.action_snapchat)
}

sealed class GestureConfigurationKey(@param:StringRes val titleRes: Int) {
    data object Configuration: GestureConfigurationKey(R.string.setup_gesture_toolbar)
    data object ModelPicker: GestureConfigurationKey(R.string.setting_gesture_model)
}
