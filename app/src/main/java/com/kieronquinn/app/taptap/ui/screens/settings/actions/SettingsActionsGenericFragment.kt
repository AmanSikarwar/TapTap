package com.kieronquinn.app.taptap.ui.screens.settings.actions

import android.os.Bundle
import android.view.View
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.core.os.BundleCompat
import androidx.fragment.app.setFragmentResultListener
import com.kieronquinn.app.taptap.models.action.TapTapUIAction
import com.kieronquinn.app.taptap.repositories.room.actions.Action
import com.kieronquinn.app.taptap.ui.base.BackAvailable
import com.kieronquinn.app.taptap.ui.base.ComposeFragment
import com.kieronquinn.app.taptap.ui.base.LockCollapsed
import com.kieronquinn.app.taptap.ui.screens.container.ContainerSharedViewModel
import com.kieronquinn.app.taptap.ui.screens.container.ContainerSharedViewModel.FabState
import com.kieronquinn.app.taptap.ui.screens.settings.actions.selector.SettingsActionsAddGenericFragment.Companion.FRAGMENT_RESULT_KEY_ACTION
import com.kieronquinn.app.taptap.ui.screens.settings.actions.whengates.SettingsActionsWhenGatesFragment
import com.kieronquinn.app.taptap.utils.extensions.whenResumed
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.mapNotNull
import org.koin.androidx.viewmodel.ext.android.activityViewModel

abstract class SettingsActionsGenericFragment<A: Action>: ComposeFragment(), LockCollapsed, BackAvailable {

    abstract val viewModel: SettingsActionsGenericViewModel<A>
    @get:DrawableRes abstract val emptyIcon: Int
    @get:StringRes abstract val emptyText: Int
    open val header: (@Composable () -> Unit)? = null

    private val sharedViewModel by activityViewModel<ContainerSharedViewModel>()
    private val results = Channel<ActionsEvent>(Channel.BUFFERED)

    private val events by lazy {
        merge(results.receiveAsFlow(), sharedViewModel.fabClicked.mapNotNull {
            when (it) {
                FabState.FabAction.ADD_ACTION -> ActionsEvent.Add
                FabState.FabAction.DELETE -> ActionsEvent.Delete
                else -> null
            }
        })
    }

    @Composable
    override fun Content() = SettingsActionsScreen(
        viewModel, events, { sharedViewModel.restartService(requireContext()) }, emptyIcon, emptyText, header
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setFragmentResultListener(FRAGMENT_RESULT_KEY_ACTION) { _, bundle ->
            val action = BundleCompat.getParcelable(bundle, FRAGMENT_RESULT_KEY_ACTION, TapTapUIAction::class.java)
                ?: return@setFragmentResultListener
            results.trySend(ActionsEvent.Added(action))
        }
        setFragmentResultListener(SettingsActionsWhenGatesFragment.FRAGMENT_RESULT_KEY_WHEN_GATES_SIZE) { _, bundle ->
            @Suppress("UNCHECKED_CAST", "DEPRECATION")
            val pair = bundle.getSerializable(SettingsActionsWhenGatesFragment.FRAGMENT_RESULT_KEY_WHEN_GATES_SIZE) as? Pair<Int, Int>
                ?: return@setFragmentResultListener
            results.trySend(ActionsEvent.WhenGatesSize(pair.first, pair.second))
        }
        whenResumed {
            viewModel.fabState.collect {
                sharedViewModel.setFabState(when (it) {
                    SettingsActionsGenericViewModel.FabState.HIDDEN -> FabState.Hidden
                    SettingsActionsGenericViewModel.FabState.ADD -> FabState.Shown(FabState.FabAction.ADD_ACTION)
                    SettingsActionsGenericViewModel.FabState.DELETE -> FabState.Shown(FabState.FabAction.DELETE)
                })
            }
        }
    }

    override fun onPause() {
        super.onPause()
        sharedViewModel.setFabState(FabState.Hidden)
    }

}
