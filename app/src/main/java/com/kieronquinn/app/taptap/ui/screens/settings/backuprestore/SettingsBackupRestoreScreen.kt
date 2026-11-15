package com.kieronquinn.app.taptap.ui.screens.settings.backuprestore

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import com.kieronquinn.app.taptap.R
import com.kieronquinn.app.taptap.ui.compose.containerBottomPadding
import com.kieronquinn.app.taptap.ui.compose.settingsItems
import com.kieronquinn.app.taptap.ui.screens.settings.generic.GenericSettingsViewModel.SettingsItem

@Composable
fun SettingsBackupRestoreScreen(viewModel: SettingsBackupRestoreViewModel) {
    val backup = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("*/*")) {
        it?.let(viewModel::onBackupFileClicked)
    }
    val restore = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) {
        it?.let(viewModel::onRestoreFileClicked)
    }
    val items = listOf(
        SettingsItem.Text(
            icon = R.drawable.ic_backup_restore_backup,
            titleRes = R.string.settings_backup_restore_backup,
            contentRes = R.string.settings_backup_restore_backup_desc,
            onClick = { viewModel.onBackupClicked(backup) }
        ),
        SettingsItem.Text(
            icon = R.drawable.ic_backup_restore_restore,
            titleRes = R.string.settings_backup_restore_restore,
            contentRes = R.string.settings_backup_restore_restore_desc,
            onClick = { viewModel.onRestoreClicked(restore) }
        )
    )
    LazyColumn(
        Modifier.fillMaxSize().nestedScroll(rememberNestedScrollInteropConnection()),
        contentPadding = containerBottomPadding()
    ) { settingsItems(items) }
}
