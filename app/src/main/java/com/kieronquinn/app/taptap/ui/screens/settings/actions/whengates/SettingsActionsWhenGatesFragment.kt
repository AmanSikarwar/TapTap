package com.kieronquinn.app.taptap.ui.screens.settings.actions.whengates

import android.os.Bundle
import android.view.View
import androidx.compose.runtime.Composable
import androidx.core.os.BundleCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.navArgs
import com.kieronquinn.app.taptap.models.gate.TapTapUIGate
import com.kieronquinn.app.taptap.models.gate.TapTapUIWhenGate
import com.kieronquinn.app.taptap.ui.base.BackAvailable
import com.kieronquinn.app.taptap.ui.base.ComposeFragment
import com.kieronquinn.app.taptap.ui.base.LockCollapsed
import com.kieronquinn.app.taptap.ui.screens.container.ContainerSharedViewModel
import com.kieronquinn.app.taptap.ui.screens.container.ContainerSharedViewModel.FabState
import com.kieronquinn.app.taptap.ui.screens.settings.gates.selector.SettingsGatesAddGenericFragment
import com.kieronquinn.app.taptap.utils.extensions.whenResumed
import kotlinx.coroutines.flow.mapNotNull
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsActionsWhenGatesFragment : ComposeFragment(), LockCollapsed, BackAvailable {

    companion object {
        const val FRAGMENT_RESULT_KEY_WHEN_GATES_SIZE = "fragment_result_when_gates_size"
    }

    private val viewModelDouble by viewModel<SettingsActionsWhenGatesViewModelDouble>()
    private val viewModelTriple by viewModel<SettingsActionsWhenGatesViewModelTriple>()
    private val args by navArgs<SettingsActionsWhenGatesFragmentArgs>()
    private val sharedViewModel by activityViewModel<ContainerSharedViewModel>()

    private val viewModel: SettingsActionsWhenGatesViewModel by lazy {
        if (args.isTriple) viewModelTriple else viewModelDouble
    }

    private val events by lazy {
        sharedViewModel.fabClicked.mapNotNull {
            when (it) {
                FabState.FabAction.ADD_REQUIREMENT -> WhenGatesEvent.Add
                FabState.FabAction.DELETE -> WhenGatesEvent.Delete
                else -> null
            }
        }
    }

    @Composable
    override fun Content() = SettingsActionsWhenGatesScreen(
        viewModel,
        args.action,
        events,
        { id, size -> setFragmentResult(FRAGMENT_RESULT_KEY_WHEN_GATES_SIZE, bundleOf(FRAGMENT_RESULT_KEY_WHEN_GATES_SIZE to Pair(id, size))) },
        { sharedViewModel.restartService(requireContext()) }
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setFragmentResultListener(SettingsGatesAddGenericFragment.FRAGMENT_RESULT_KEY_GATE) { _, bundle ->
            val gate = BundleCompat.getParcelable(bundle, SettingsGatesAddGenericFragment.FRAGMENT_RESULT_KEY_GATE, TapTapUIGate::class.java)
                ?: return@setFragmentResultListener
            viewModel.handleGateResult(args.action.id, TapTapUIWhenGate(-1, gate, false), requireContext())
        }
        whenResumed {
            viewModel.fabState.collect {
                sharedViewModel.setFabState(when (it) {
                    SettingsActionsWhenGatesViewModel.FabState.HIDDEN -> FabState.Hidden
                    SettingsActionsWhenGatesViewModel.FabState.ADD -> FabState.Shown(FabState.FabAction.ADD_REQUIREMENT)
                    SettingsActionsWhenGatesViewModel.FabState.DELETE -> FabState.Shown(FabState.FabAction.DELETE)
                })
            }
        }
    }

    override fun onPause() {
        super.onPause()
        sharedViewModel.setFabState(FabState.Hidden)
    }

}
