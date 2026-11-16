package com.kieronquinn.app.taptap.ui.screens.settings.gates

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kieronquinn.app.taptap.R
import androidx.compose.ui.platform.LocalContext
import com.kieronquinn.app.taptap.components.navigation.NavigationResults
import com.kieronquinn.app.taptap.ui.screens.container.ContainerSharedViewModel
import com.kieronquinn.app.taptap.ui.screens.container.ContainerSharedViewModel.FabState.FabAction
import com.kieronquinn.app.taptap.ui.screens.container.ShowFab
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.merge
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinActivityViewModel
import org.koin.compose.viewmodel.koinViewModel
import com.kieronquinn.app.taptap.models.gate.TapTapUIGate
import com.kieronquinn.app.taptap.ui.compose.EditableCard
import com.kieronquinn.app.taptap.ui.compose.MessageCard
import com.kieronquinn.app.taptap.ui.compose.containerBottomPadding
import com.kieronquinn.app.taptap.ui.compose.rememberReorderState
import com.kieronquinn.app.taptap.ui.compose.reorderHandle
import com.kieronquinn.app.taptap.ui.compose.reorderable
import com.kieronquinn.app.taptap.ui.screens.settings.actions.EmptyState
import com.kieronquinn.app.taptap.ui.screens.settings.gates.SettingsGatesViewModel.SettingsGatesItem
import com.kieronquinn.app.taptap.ui.screens.settings.gates.SettingsGatesViewModel.State
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.debounce

sealed interface GatesEvent {
    data class Added(val gate: TapTapUIGate): GatesEvent
    data object Add: GatesEvent
    data object Delete: GatesEvent
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class, FlowPreview::class)
@Composable
fun SettingsGatesScreen(viewModel: SettingsGatesViewModel, events: Flow<GatesEvent>, onRestartService: () -> Unit) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val items = remember { mutableStateListOf<SettingsGatesItem>() }
    var selected by remember { mutableStateOf<SettingsGatesItem.Gate?>(null) }
    val listState = rememberLazyListState()
    val reorder = rememberReorderState(
        listState,
        onMove = { from, to -> items.add(to, items.removeAt(from)) },
        onDrop = viewModel::moveGate,
        canMoveTo = { items.getOrNull(it) is SettingsGatesItem.Gate }
    )
    fun select(item: SettingsGatesItem.Gate?) {
        selected = item
        viewModel.onItemSelectionStateChange(item != null)
    }
    BackHandler(selected != null) { select(null) }
    LaunchedEffect(viewModel) { viewModel.reloadGates() }
    LaunchedEffect(state) {
        (state as? State.Loaded)?.let {
            items.clear()
            items.addAll(it.items)
            selected = null
        }
    }
    LifecycleResumeEffect(viewModel) {
        viewModel.onResume()
        onPauseOrDispose { viewModel.onPause() }
    }
    LaunchedEffect(viewModel) {
        viewModel.scrollToBottomBus.collect { if (items.isNotEmpty()) listState.animateScrollToItem(items.lastIndex) }
    }
    LaunchedEffect(viewModel) { viewModel.reloadServiceBus.debounce(1000L).collect { onRestartService() } }
    LaunchedEffect(events) {
        events.collect { event ->
            when (event) {
                is GatesEvent.Added -> {
                    items.add(SettingsGatesItem.Gate(event.gate))
                    viewModel.onGateResult(event.gate)
                }
                GatesEvent.Add -> viewModel.onAddGateFabClicked()
                GatesEvent.Delete -> {
                    selected?.let {
                        items.remove(it)
                        viewModel.removeGate(it.gate.id)
                    }
                    select(null)
                }
            }
        }
    }
    when {
        state is State.Loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                LoadingIndicator()
                Text(stringResource(R.string.settings_gates_loading_gates))
            }
        }
        items.none { it is SettingsGatesItem.Gate } -> EmptyState(R.drawable.ic_gates, R.string.settings_gates_empty)
        else -> LazyColumn(
            Modifier.fillMaxSize().nestedScroll(rememberNestedScrollInteropConnection()),
            state = listState,
            contentPadding = PaddingValues(
                bottom = containerBottomPadding().calculateBottomPadding() + dimensionResource(R.dimen.container_fab_margin)
            )
        ) {
            itemsIndexed(items, key = { _, item -> System.identityHashCode(item) }) { index, item ->
                when (item) {
                    is SettingsGatesItem.Header -> MessageCard(
                        R.drawable.ic_about,
                        stringResource(item.contentRes),
                        Modifier.padding(16.dp),
                        onClick = item.onClick,
                        onDismiss = item.onCloseClick
                    )
                    is SettingsGatesItem.Gate -> {
                        var enabled by remember(item) { mutableStateOf(item.gate.enabled) }
                        EditableCard(
                            icon = item.gate.gate.iconRes,
                            title = stringResource(item.gate.gate.nameRes),
                            description = item.gate.description,
                            selected = item == selected,
                            onLongClick = { select(if (item == selected) null else item) },
                            modifier = Modifier.animateItem().reorderable(reorder, index),
                            reorderHandle = Modifier.reorderHandle(reorder) { items.indexOf(item) },
                            trailing = {
                                Switch(checked = enabled, onCheckedChange = {
                                    enabled = it
                                    item.gate.enabled = it
                                    viewModel.onItemStateChanged(item.gate.id, it)
                                })
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsGatesRoute(viewModel: SettingsGatesViewModel = koinViewModel()) {
    val context = LocalContext.current
    val shared = koinActivityViewModel<ContainerSharedViewModel>()
    val results = koinInject<NavigationResults>()
    val fab by viewModel.fabState.collectAsStateWithLifecycle()
    ShowFab(
        when (fab) {
            SettingsGatesViewModel.FabState.HIDDEN -> null
            SettingsGatesViewModel.FabState.ADD -> FabAction.ADD_GATE
            SettingsGatesViewModel.FabState.DELETE -> FabAction.DELETE
        }
    )
    val events = remember(viewModel) {
        merge(
            results.receive<TapTapUIGate>(NavigationResults.Key.GATE).map { GatesEvent.Added(it) },
            shared.fabClicked.mapNotNull {
                when (it) {
                    FabAction.ADD_GATE -> GatesEvent.Add
                    FabAction.DELETE -> GatesEvent.Delete
                    else -> null
                }
            }
        )
    }
    SettingsGatesScreen(viewModel, events) { shared.restartService(context) }
}
