package com.kieronquinn.app.taptap.ui.screens.settings.backuprestore

import androidx.compose.runtime.Composable
import com.kieronquinn.app.taptap.ui.base.BackAvailable
import com.kieronquinn.app.taptap.ui.base.ComposeFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsBackupRestoreFragment: ComposeFragment(), BackAvailable {

    private val viewModel by viewModel<SettingsBackupRestoreViewModel>()

    @Composable
    override fun Content() = SettingsBackupRestoreScreen(viewModel)

}
