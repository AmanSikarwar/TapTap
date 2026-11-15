package com.kieronquinn.app.taptap.ui.screens.settings.modelpicker

import androidx.compose.runtime.Composable
import com.kieronquinn.app.taptap.ui.base.BackAvailable
import com.kieronquinn.app.taptap.ui.base.ComposeFragment
import com.kieronquinn.app.taptap.ui.base.LockCollapsed
import com.kieronquinn.app.taptap.ui.screens.container.ContainerSharedViewModel
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsModelPickerFragment : ComposeFragment(), LockCollapsed, BackAvailable {

    private val viewModel by viewModel<SettingsModelPickerViewModel>()
    private val sharedViewModel by activityViewModel<ContainerSharedViewModel>()

    @Composable
    override fun Content() = SettingsModelPickerScreen(viewModel) {
        sharedViewModel.restartService(requireContext())
    }

}
