package com.kieronquinn.app.taptap.ui.screens.settings.gates

import android.os.Bundle
import android.view.View
import androidx.compose.runtime.Composable
import androidx.core.os.BundleCompat
import androidx.fragment.app.setFragmentResultListener
import com.kieronquinn.app.taptap.models.gate.TapTapUIGate
import com.kieronquinn.app.taptap.ui.base.BackAvailable
import com.kieronquinn.app.taptap.ui.base.ComposeFragment
import com.kieronquinn.app.taptap.ui.base.LockCollapsed
import com.kieronquinn.app.taptap.ui.screens.container.ContainerSharedViewModel
import com.kieronquinn.app.taptap.ui.screens.container.ContainerSharedViewModel.FabState
import com.kieronquinn.app.taptap.ui.screens.settings.gates.selector.SettingsGatesAddGenericFragment
import com.kieronquinn.app.taptap.utils.extensions.whenResumed
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.receiveAsFlow
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsGatesFragment : ComposeFragment(), LockCollapsed, BackAvailable {

    private val viewModel by viewModel<SettingsGatesViewModel>()
    private val sharedViewModel by activityViewModel<ContainerSharedViewModel>()
    private val results = Channel<GatesEvent>(Channel.BUFFERED)

    private val events by lazy {
        merge(results.receiveAsFlow(), sharedViewModel.fabClicked.mapNotNull {
            when (it) {
                FabState.FabAction.ADD_GATE -> GatesEvent.Add
                FabState.FabAction.DELETE -> GatesEvent.Delete
                else -> null
            }
        })
    }

    @Composable
    override fun Content() = SettingsGatesScreen(viewModel, events) { sharedViewModel.restartService(requireContext()) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setFragmentResultListener(SettingsGatesAddGenericFragment.FRAGMENT_RESULT_KEY_GATE) { _, bundle ->
            val gate = BundleCompat.getParcelable(bundle, SettingsGatesAddGenericFragment.FRAGMENT_RESULT_KEY_GATE, TapTapUIGate::class.java)
                ?: return@setFragmentResultListener
            results.trySend(GatesEvent.Added(gate))
        }
        whenResumed {
            viewModel.fabState.collect {
                sharedViewModel.setFabState(when (it) {
                    SettingsGatesViewModel.FabState.HIDDEN -> FabState.Hidden
                    SettingsGatesViewModel.FabState.ADD -> FabState.Shown(FabState.FabAction.ADD_GATE)
                    SettingsGatesViewModel.FabState.DELETE -> FabState.Shown(FabState.FabAction.DELETE)
                })
            }
        }
    }

    override fun onPause() {
        super.onPause()
        sharedViewModel.setFabState(FabState.Hidden)
    }

}
