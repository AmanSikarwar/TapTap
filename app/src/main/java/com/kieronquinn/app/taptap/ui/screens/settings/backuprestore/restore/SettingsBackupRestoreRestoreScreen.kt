package com.kieronquinn.app.taptap.ui.screens.settings.backuprestore.restore

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
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
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
