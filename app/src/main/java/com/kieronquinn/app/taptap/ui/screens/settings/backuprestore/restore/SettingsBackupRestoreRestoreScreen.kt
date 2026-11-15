package com.kieronquinn.app.taptap.ui.screens.settings.backuprestore.restore

import android.Manifest
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import com.kieronquinn.app.taptap.components.navigation.NavigationResults
import com.kieronquinn.app.taptap.models.action.ActionRequirement
import com.kieronquinn.app.taptap.models.gate.GateRequirement
import com.kieronquinn.app.taptap.models.shared.SharedArgument
import com.kieronquinn.app.taptap.ui.screens.container.ContainerSharedViewModel
import com.kieronquinn.app.taptap.ui.screens.container.ContainerSharedViewModel.FabState
import com.kieronquinn.app.taptap.ui.screens.container.ShowFab
import com.kieronquinn.app.taptap.ui.screens.settings.backuprestore.restore.SettingsBackupRestoreRestoreViewModel.ResolvedRequirement
import com.kieronquinn.app.taptap.ui.screens.settings.shared.RequirementRequester
import com.kieronquinn.app.taptap.ui.screens.settings.shared.rememberRequirementRequester
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinActivityViewModel
import org.koin.compose.viewmodel.koinViewModel
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kieronquinn.app.taptap.R
import com.kieronquinn.app.taptap.ui.compose.MessageCard
import com.kieronquinn.app.taptap.ui.compose.StatusIndicator
import com.kieronquinn.app.taptap.ui.compose.StatusScreen
import com.kieronquinn.app.taptap.ui.compose.containerBottomPadding
import com.kieronquinn.app.taptap.ui.compose.toAnnotatedString
import com.kieronquinn.app.taptap.ui.screens.settings.backuprestore.restore.SettingsBackupRestoreRestoreViewModel.Item
import com.kieronquinn.app.taptap.ui.screens.settings.backuprestore.restore.SettingsBackupRestoreRestoreViewModel.State

@Composable
fun SettingsBackupRestoreRestoreScreen(viewModel: SettingsBackupRestoreRestoreViewModel, onSetupClicked: (Item.Requirement) -> Unit) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    when (val s = state) {
        is State.Loading, is State.Restoring -> StatusScreen(
            StatusIndicator.Indeterminate,
            stringResource(R.string.settings_backuprestore_restore_title),
            stringResource(R.string.settings_backuprestore_restore_desc)
        )
        is State.Finished -> StatusScreen(
            StatusIndicator.Icon(if (s.success) R.drawable.ic_check_circle else R.drawable.ic_error_circle),
            stringResource(if (s.success) R.string.settings_backuprestore_restore_done_title else R.string.settings_backuprestore_restore_error_title),
            if (s.success) stringResource(R.string.settings_backuprestore_restore_done_desc, s.filename.orEmpty())
            else stringResource(R.string.settings_backuprestore_restore_error_desc),
            stringResource(R.string.close),
            viewModel::onCloseClicked
        )
        is State.ActionRequired -> LazyColumn(
            Modifier.fillMaxSize().nestedScroll(rememberNestedScrollInteropConnection()),
            contentPadding = PaddingValues(
                bottom = containerBottomPadding().calculateBottomPadding() + dimensionResource(R.dimen.container_fab_margin)
            )
        ) {
            items(s.items) { item ->
                when (item) {
                    is Item.Infobox -> MessageCard(R.drawable.ic_about, LocalContext.current.getText(item.content), Modifier.padding(16.dp))
                    is Item.Header -> Text(
                        LocalContext.current.getText(item.title).toAnnotatedString(),
                        Modifier.padding(start = 32.dp, end = 32.dp, top = 16.dp, bottom = 4.dp),
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    is Item.Requirement -> RequirementCard(item, { onSetupClicked(item) }, { viewModel.onRequirementSkipped(item.uuid) })
                }
            }
        }
    }
}

@Composable
private fun RequirementCard(item: Item.Requirement, onSetup: () -> Unit, onSkip: () -> Unit) {
    val context = LocalContext.current
    Card(
        Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp).alpha(if (item.isSupported) 1f else 0.5f),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceBright)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(painterResource(item.icon), null)
                Column(Modifier.padding(start = 16.dp)) {
                    Text(context.getText(item.title).toString(), style = MaterialTheme.typography.titleMedium)
                    Text(
                        context.getText(
                            when {
                                item.isSupported -> item.desc
                                item.isGate -> R.string.gate_selector_unavailable
                                else -> R.string.action_selector_unavailable
                            }
                        ).toAnnotatedString(),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    SuggestionChip(
                        onClick = {},
                        label = { Text(stringResource(item.chipText)) },
                        icon = { Icon(painterResource(item.chipIcon), null, Modifier.size(18.dp)) }
                    )
                }
            }
            if (item.isSupported) Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)) {
                TextButton(onClick = onSkip) { Text(stringResource(R.string.settings_backup_restore_restore_item_skip)) }
                Button(onClick = onSetup) { Text(stringResource(R.string.settings_backup_restore_restore_item_setup)) }
            }
        }
    }
}

@Composable
fun SettingsBackupRestoreRestoreRoute(uri: Uri, viewModel: SettingsBackupRestoreRestoreViewModel = koinViewModel()) {
    val requester = rememberRequirementRequester()
    val results = koinInject<NavigationResults>()
    val scope = rememberCoroutineScope()
    val shared = koinActivityViewModel<ContainerSharedViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(uri) { viewModel.setUri(uri) }
    LaunchedEffect(viewModel) {
        launch {
            results.receive<Pair<SharedArgument, Boolean>>(NavigationResults.Key.SHIZUKU).collect { (_, granted) ->
                if (granted) viewModel.onRequirementResolved(ResolvedRequirement.Action(ActionRequirement.Shizuku))
            }
        }
        launch {
            results.receive<Pair<SharedArgument, Boolean>>(NavigationResults.Key.SNAPCHAT).collect { (_, available) ->
                if (available) viewModel.onRequirementResolved(ResolvedRequirement.Action(ActionRequirement.Snapchat))
            }
        }
        launch {
            shared.fabClicked.collect { if (it == FabState.FabAction.RESTORE_BACKUP) viewModel.onFabClicked() }
        }
    }
    ShowFab(FabState.FabAction.RESTORE_BACKUP.takeIf { (state as? State.ActionRequired)?.isContinueEnabled == true })
    BackHandler(enabled = state is State.Finished) { viewModel.onCloseClicked() }
    SettingsBackupRestoreRestoreScreen(viewModel) { item ->
        scope.launch {
            //Take the first requirement as the list is dynamic
            when (val requirement = item.requirements.firstOrNull() ?: return@launch) {
                is ResolvedRequirement.Action -> requester.resolve(requirement.requirement, viewModel)
                is ResolvedRequirement.Gate -> requester.resolve(requirement.requirement, viewModel)
            }
        }
    }
}

private suspend fun RequirementRequester.resolve(requirement: ActionRequirement, viewModel: SettingsBackupRestoreRestoreViewModel) {
    val result = when (requirement) {
        is ActionRequirement.Accessibility -> requestAccessibilityService()
        is ActionRequirement.AnswerPhoneCallsPermission -> requestPermission(Manifest.permission.ANSWER_PHONE_CALLS, Manifest.permission.READ_PHONE_STATE)
        is ActionRequirement.GestureAccessibility -> requestGestureService()
        is ActionRequirement.TaskerPermission -> requestTaskerPermission()
        is ActionRequirement.AccessNotificationPolicyPermission -> requestNotificationPolicy()
        is ActionRequirement.CameraPermission -> requestPermission(Manifest.permission.CAMERA)
        is ActionRequirement.DrawOverOtherAppsPermission -> requestDisplayOverOtherApps()
        is ActionRequirement.WriteSystemSettingsPermission -> requestWriteSystemSettings()
        is ActionRequirement.Shizuku -> false.also { viewModel.launchShizukuFlow(false) }
        is ActionRequirement.Root -> viewModel.checkRoot().also { if (!it) viewModel.showNoRoot() }
        is ActionRequirement.Snapchat -> false.also { viewModel.launchSnapchatFlow() }
        else -> false
    }
    if (result) viewModel.onRequirementResolved(ResolvedRequirement.Action(requirement))
}

private suspend fun RequirementRequester.resolve(requirement: GateRequirement, viewModel: SettingsBackupRestoreRestoreViewModel) {
    val result = when (requirement) {
        is GateRequirement.Accessibility -> requestAccessibilityService()
        is GateRequirement.ReadPhoneStatePermission -> requestPermission(Manifest.permission.READ_PHONE_STATE)
        is GateRequirement.Shizuku -> false.also { viewModel.launchShizukuFlow(true) }
        else -> false
    }
    if (result) viewModel.onRequirementResolved(ResolvedRequirement.Gate(requirement))
}
