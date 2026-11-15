package com.kieronquinn.app.taptap.ui.screens.settings.update

import android.os.Bundle
import android.view.View
import androidx.compose.runtime.Composable
import androidx.navigation.fragment.navArgs
import com.kieronquinn.app.taptap.ui.base.BackAvailable
import com.kieronquinn.app.taptap.ui.base.ComposeFragment
import com.kieronquinn.app.taptap.ui.screens.container.ContainerSharedViewModel
import com.kieronquinn.app.taptap.ui.screens.container.ContainerSharedViewModel.FabState
import com.kieronquinn.app.taptap.utils.extensions.whenResumed
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsUpdateFragment: ComposeFragment(), BackAvailable {

    private val viewModel by viewModel<SettingsUpdateViewModel>()
    private val sharedViewModel by activityViewModel<ContainerSharedViewModel>()
    private val args by navArgs<SettingsUpdateFragmentArgs>()

    @Composable
    override fun Content() = SettingsUpdateScreen(viewModel)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.setupWithRelease(args.release)
        whenResumed {
            viewModel.showFab.collect {
                sharedViewModel.setFabState(if (it) FabState.Shown(FabState.FabAction.DOWNLOAD) else FabState.Hidden)
            }
        }
        whenResumed {
            sharedViewModel.fabClicked.collect {
                if (it == FabState.FabAction.DOWNLOAD) viewModel.startDownload()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        sharedViewModel.setFabState(FabState.Hidden)
    }

}
