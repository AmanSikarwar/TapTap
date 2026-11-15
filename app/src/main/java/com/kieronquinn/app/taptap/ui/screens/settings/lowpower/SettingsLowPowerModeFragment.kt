package com.kieronquinn.app.taptap.ui.screens.settings.lowpower

import androidx.compose.runtime.Composable
import com.kieronquinn.app.taptap.ui.base.BackAvailable
import com.kieronquinn.app.taptap.ui.base.ComposeFragment
import com.kieronquinn.app.taptap.ui.screens.container.ContainerSharedViewModel
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsLowPowerModeFragment: ComposeFragment(), BackAvailable {

    private val viewModel by viewModel<SettingsLowPowerModeViewModel>()
    private val containerViewModel by activityViewModel<ContainerSharedViewModel>()

    @Composable
    override fun Content() = SettingsLowPowerModeScreen(viewModel) {
        containerViewModel.restartService(requireContext())
    }

}
