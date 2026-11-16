package com.kieronquinn.app.taptap.ui.screens.settings.actions.whengates

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kieronquinn.app.taptap.R
import com.kieronquinn.app.taptap.components.navigation.NavigationResults
import com.kieronquinn.app.taptap.models.gate.TapTapUIGate
import com.kieronquinn.app.taptap.ui.screens.container.ContainerSharedViewModel
import com.kieronquinn.app.taptap.ui.screens.container.ContainerSharedViewModel.FabState.FabAction
import com.kieronquinn.app.taptap.ui.screens.container.ShowFab
import kotlinx.coroutines.flow.mapNotNull
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinActivityViewModel
import org.koin.compose.viewmodel.koinViewModel
import com.kieronquinn.app.taptap.models.action.TapTapUIAction
import com.kieronquinn.app.taptap.models.gate.TapTapUIWhenGate
import com.kieronquinn.app.taptap.ui.compose.EditableCard
import com.kieronquinn.app.taptap.ui.compose.containerBottomPadding
import com.kieronquinn.app.taptap.ui.compose.toAnnotatedString
import com.kieronquinn.app.taptap.ui.screens.settings.actions.EmptyState
import com.kieronquinn.app.taptap.ui.screens.settings.actions.whengates.SettingsActionsWhenGatesViewModel.State
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.debounce

sealed interface WhenGatesEvent {
    data object Add: WhenGatesEvent
    data object Delete: WhenGatesEvent
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class, FlowPreview::class)
@Composable
fun SettingsActionsWhenGatesScreen(
    viewModel: SettingsActionsWhenGatesViewModel,
    action: TapTapUIAction,
    events: Flow<WhenGatesEvent>,
    onSizeChanged: (Int, Int) -> Unit,
    onRestartService: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var selected by remember { mutableStateOf<TapTapUIWhenGate?>(null) }
    val listState = rememberLazyListState()
    val context = LocalContext.current
    BackHandler(selected != null) {
        selected = null
        viewModel.onItemSelectionStateChange(false)
    }
    LaunchedEffect(viewModel) { viewModel.setupWithAction(action, context) }
    LifecycleResumeEffect(viewModel) {
        viewModel.onResume()
        onPauseOrDispose { viewModel.onPause() }
    }
    LaunchedEffect(state) {
        (state as? State.Loaded)?.let {
            selected = null
            onSizeChanged(it.action.id, it.gates.size)
        }
    }
    LaunchedEffect(viewModel) { viewModel.reloadServiceBus.debounce(1000L).collect { onRestartService() } }
    LaunchedEffect(viewModel) {
        viewModel.scrollToBottomBus.collect {
            val count = (viewModel.state.value as? State.Loaded)?.gates?.size ?: 0
            if (count > 0) listState.animateScrollToItem(count - 1)
        }
    }
    LaunchedEffect(events) {
        events.collect {
            when (it) {
                WhenGatesEvent.Add -> viewModel.onAddRequirementFabClicked()
                WhenGatesEvent.Delete -> {
                    selected?.let { gate -> viewModel.removeWhenGate(action.id, gate.id) }
                    selected = null
                    viewModel.onItemSelectionStateChange(false)
                }
            }
        }
    }
    Column(Modifier.fillMaxSize()) {
        Card(
            Modifier.fillMaxWidth().padding(16.dp),
            shape = MaterialTheme.shapes.extraLarge,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
        ) {
            Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(painterResource(action.tapAction.iconRes), null)
                Column(Modifier.padding(start = 16.dp)) {
                    Text(stringResource(action.tapAction.nameRes), style = MaterialTheme.typography.titleMedium)
                    Text(action.description.toAnnotatedString(), style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
        when (val s = state) {
            is State.Loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    LoadingIndicator()
                    Text(stringResource(R.string.settings_actions_loading_when_gates))
                }
            }
            is State.Loaded -> if (s.gates.isEmpty()) {
                EmptyState(R.drawable.ic_action_chip_when_normal, R.string.settings_actions_when_gates_empty)
            } else LazyColumn(
                Modifier.fillMaxSize().nestedScroll(rememberNestedScrollInteropConnection()),
                state = listState,
                contentPadding = PaddingValues(
                    bottom = containerBottomPadding().calculateBottomPadding() + dimensionResource(R.dimen.container_fab_margin)
                )
            ) {
                items(s.gates, key = { it.id }) { gate ->
                    val name = stringResource(gate.gate.gate.nameRes)
                    EditableCard(
                        icon = gate.gate.gate.iconRes,
                        title = if (gate.inverted) stringResource(R.string.action_when_gate_inverted, name) else name,
                        description = gate.gate.description,
                        selected = gate == selected,
                        onLongClick = {
                            selected = if (gate == selected) null else gate
                            viewModel.onItemSelectionStateChange(selected != null)
                        },
                        modifier = Modifier.animateItem()
                    )
                }
            }
        }
    }
}

@Composable
fun SettingsActionsWhenGatesRoute(action: TapTapUIAction, isTriple: Boolean) {
    val viewModel: SettingsActionsWhenGatesViewModel = if (isTriple) {
        koinViewModel<SettingsActionsWhenGatesViewModelTriple>()
    } else {
        koinViewModel<SettingsActionsWhenGatesViewModelDouble>()
    }
    val context = LocalContext.current
    val shared = koinActivityViewModel<ContainerSharedViewModel>()
    val results = koinInject<NavigationResults>()
    val fab by viewModel.fabState.collectAsStateWithLifecycle()
    ShowFab(
        when (fab) {
            SettingsActionsWhenGatesViewModel.FabState.HIDDEN -> null
            SettingsActionsWhenGatesViewModel.FabState.ADD -> FabAction.ADD_REQUIREMENT
            SettingsActionsWhenGatesViewModel.FabState.DELETE -> FabAction.DELETE
        }
    )
    LaunchedEffect(viewModel) {
        results.receive<TapTapUIGate>(NavigationResults.Key.GATE).collect {
            viewModel.handleGateResult(action.id, TapTapUIWhenGate(-1, it, false), context)
        }
    }
    val events = remember(viewModel) {
        shared.fabClicked.mapNotNull {
            when (it) {
                FabAction.ADD_REQUIREMENT -> WhenGatesEvent.Add
                FabAction.DELETE -> WhenGatesEvent.Delete
                else -> null
            }
        }
    }
    SettingsActionsWhenGatesScreen(
        viewModel, action, events,
        { id, size -> results.send(NavigationResults.Key.WHEN_GATES_SIZE, id to size) },
        { shared.restartService(context) }
    )
}
