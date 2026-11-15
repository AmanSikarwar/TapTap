package com.kieronquinn.app.taptap.ui.screens.settings.backuprestore.backup

import android.os.Bundle
import android.view.View
import androidx.compose.runtime.Composable
import androidx.navigation.fragment.navArgs
import com.kieronquinn.app.taptap.ui.base.BackAvailable
import com.kieronquinn.app.taptap.ui.base.ComposeFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsBackupRestoreBackupFragment : ComposeFragment(), BackAvailable {

    private val args by navArgs<SettingsBackupRestoreBackupFragmentArgs>()
    private val viewModel by viewModel<SettingsBackupRestoreBackupViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.setBackupUri(args.backupUri)
    }

    @Composable
    override fun Content() = SettingsBackupRestoreBackupScreen(viewModel)

}
