package com.kieronquinn.app.taptap.ui.screens.settings.backuprestore.backup

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kieronquinn.app.taptap.R
import com.kieronquinn.app.taptap.repositories.backuprestore.BackupRepository
import com.kieronquinn.app.taptap.ui.compose.StatusIndicator
import com.kieronquinn.app.taptap.ui.compose.StatusScreen
import com.kieronquinn.app.taptap.ui.screens.settings.backuprestore.backup.SettingsBackupRestoreBackupViewModel.State

@Composable
fun SettingsBackupRestoreBackupScreen(viewModel: SettingsBackupRestoreBackupViewModel) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    when (val s = state) {
        is State.BackingUp -> StatusScreen(
            StatusIndicator.Indeterminate,
            stringResource(R.string.settings_backuprestore_backup_title),
            stringResource(R.string.settings_backuprestore_backup_desc)
        )
        is State.Finished -> when (val result = s.result) {
            is BackupRepository.BackupResult.Success -> StatusScreen(
                StatusIndicator.Icon(R.drawable.ic_check_circle),
                stringResource(R.string.settings_backuprestore_backup_done_title),
                stringResource(R.string.settings_backuprestore_backup_done_desc, result.file.name.orEmpty()),
                stringResource(R.string.close),
                viewModel::onCloseClicked
            )
            is BackupRepository.BackupResult.Error -> StatusScreen(
                StatusIndicator.Icon(R.drawable.ic_error_circle),
                stringResource(R.string.settings_backuprestore_backup_error_title),
                stringResource(R.string.settings_backuprestore_backup_error_desc, stringResource(result.errorType.errorRes)),
                stringResource(R.string.close),
                viewModel::onCloseClicked
            )
        }
    }
}
