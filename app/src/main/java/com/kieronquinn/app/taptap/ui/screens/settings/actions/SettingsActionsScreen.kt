package com.kieronquinn.app.taptap.ui.screens.settings.actions

import androidx.compose.ui.platform.LocalResources
import androidx.activity.compose.BackHandler
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.AssistChip
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import com.kieronquinn.app.taptap.components.navigation.NavigationResults
import com.kieronquinn.app.taptap.ui.compose.ModeSwitch
import com.kieronquinn.app.taptap.ui.screens.container.ContainerSharedViewModel
import com.kieronquinn.app.taptap.ui.screens.container.ContainerSharedViewModel.FabState.FabAction
import com.kieronquinn.app.taptap.ui.screens.container.ShowFab
import com.kieronquinn.app.taptap.ui.screens.settings.actions.tripletap.SettingsActionsTripleViewModel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.merge
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinActivityViewModel
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kieronquinn.app.taptap.R
import com.kieronquinn.app.taptap.models.action.ActionRequirement
import com.kieronquinn.app.taptap.models.action.TapTapUIAction
import com.kieronquinn.app.taptap.ui.compose.EditableCard
import com.kieronquinn.app.taptap.ui.compose.HelpBanner
import com.kieronquinn.app.taptap.ui.compose.containerBottomPadding
import com.kieronquinn.app.taptap.ui.compose.rememberReorderState
import com.kieronquinn.app.taptap.ui.compose.reorderHandle
import com.kieronquinn.app.taptap.ui.compose.reorderable
import com.kieronquinn.app.taptap.ui.screens.settings.actions.SettingsActionsGenericViewModel.SettingsActionsItem
import com.kieronquinn.app.taptap.ui.screens.settings.actions.SettingsActionsGenericViewModel.State
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.debounce

sealed interface ActionsEvent {
    data class Added(val action: TapTapUIAction): ActionsEvent
    data class WhenGatesSize(val actionId: Int, val size: Int): ActionsEvent
    data object Delete: ActionsEvent
    data object Add: ActionsEvent
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class, FlowPreview::class)
@Composable
fun SettingsActionsScreen(
    viewModel: SettingsActionsGenericViewModel<*>,
    events: Flow<ActionsEvent>,
    onRestartService: () -> Unit,
    @DrawableRes emptyIcon: Int,
    @StringRes emptyText: Int,
    header: (@Composable () -> Unit)? = null
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val items = remember { mutableStateListOf<SettingsActionsItem>() }
    var selected by remember { mutableStateOf<SettingsActionsItem.Action?>(null) }
    val listState = rememberLazyListState()
    val reorder = rememberReorderState(
        listState,
        onMove = { from, to -> items.add(to, items.removeAt(from)) },
        onDrop = viewModel::moveAction,
        canMoveTo = { items.getOrNull(it) is SettingsActionsItem.Action }
    )
    fun select(item: SettingsActionsItem.Action?) {
        selected = item
        viewModel.onItemSelectionStateChange(item != null)
    }
    BackHandler(selected != null) { select(null) }
    LaunchedEffect(viewModel) { viewModel.reloadActions() }
    var synced by remember { mutableStateOf<State?>(null) }
    LaunchedEffect(state) {
        (state as? State.Loaded)?.let {
            items.clear()
            items.addAll(it.items)
            selected = null
            synced = it
        }
    }
    LifecycleResumeEffect(viewModel) {
        viewModel.onResume()
        onPauseOrDispose { viewModel.onPause() }
    }
    LaunchedEffect(viewModel) {
        viewModel.scrollToBottomBus.collect {
            if (items.isNotEmpty()) listState.animateScrollToItem(items.lastIndex)
        }
    }
    LaunchedEffect(viewModel) { viewModel.reloadServiceBus.debounce(1000L).collect { onRestartService() } }
    LaunchedEffect(viewModel) { viewModel.switchChanged?.debounce(1000L)?.collect { onRestartService() } }
    LaunchedEffect(events) {
        events.collect { event ->
            when (event) {
                is ActionsEvent.Added -> {
                    items.add(SettingsActionsItem.Action(event.action))
                    viewModel.onActionResult(event.action)
                }
                is ActionsEvent.WhenGatesSize -> {
                    val index = items.indexOfFirst { it is SettingsActionsItem.Action && it.action.id == event.actionId }
                    if (index != -1) {
                        val item = items[index] as SettingsActionsItem.Action
                        items[index] = item.copy(action = item.action.copy(whenGatesSize = event.size))
                    }
                }
                ActionsEvent.Delete -> {
                    selected?.let {
                        items.remove(it)
                        viewModel.removeAction(it.action.id)
                    }
                    select(null)
                }
                ActionsEvent.Add -> viewModel.onAddActionFabClicked()
            }
        }
    }
    Column(Modifier.fillMaxSize()) {
        header?.invoke()
        when {
            state is State.Loading || synced !== state -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    LoadingIndicator()
                    Text(stringResource(R.string.settings_actions_loading_actions))
                }
            }
            items.none { it is SettingsActionsItem.Action } -> EmptyState(emptyIcon, emptyText)
            else -> LazyColumn(
                Modifier.fillMaxSize().nestedScroll(rememberNestedScrollInteropConnection()),
                state = listState,
                contentPadding = containerBottomPadding().let {
                    PaddingValues(bottom = it.calculateBottomPadding() + dimensionResource(R.dimen.container_fab_margin))
                }
            ) {
                itemsIndexed(items, key = { _, item -> System.identityHashCode(item) }) { index, item ->
                    when (item) {
                        is SettingsActionsItem.Header -> HelpBanner(
                            LocalResources.current.getText(item.contentRes),
                            Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            onClick = item.onClick,
                            onDismiss = item.onCloseClick
                        )
                        is SettingsActionsItem.Action -> ActionCard(
                            item = item,
                            selected = item == selected,
                            onLongClick = { select(if (item == selected) null else item) },
                            onWhenClick = { viewModel.onWhenGateChipClicked(item.action) },
                            modifier = reorderable(reorder, index),
                            handle = Modifier.reorderHandle(reorder) { items.indexOf(item) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ActionCard(
    item: SettingsActionsItem.Action,
    selected: Boolean,
    onLongClick: () -> Unit,
    onWhenClick: () -> Unit,
    modifier: Modifier,
    handle: Modifier
) {
    val action = item.action
    val requirement = action.tapAction.actionRequirement
        ?.firstOrNull { it is ActionRequirement.UserDisplayedActionRequirement } as? ActionRequirement.UserDisplayedActionRequirement
    EditableCard(
        icon = action.tapAction.iconRes,
        title = stringResource(action.tapAction.nameRes),
        description = action.description,
        selected = selected,
        onLongClick = onLongClick,
        modifier = modifier,
        reorderHandle = handle,
        chips = {
            requirement?.let {
                Row(
                    Modifier.padding(top = 8.dp).height(32.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(painterResource(it.icon), null, Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(stringResource(it.label), style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            AssistChip(
                onClick = onWhenClick,
                label = {
                    Text(
                        if (action.whenGatesSize > 0) pluralStringResource(R.plurals.item_action_when_not_empty, action.whenGatesSize, action.whenGatesSize)
                        else stringResource(R.string.item_action_when_empty)
                    )
                },
                leadingIcon = {
                    Icon(
                        painterResource(if (action.whenGatesSize > 0) R.drawable.ic_action_chip_when_normal else R.drawable.ic_action_chip_when_empty),
                        null, Modifier.size(18.dp)
                    )
                }
            )
        }
    )
}

@Composable
fun EmptyState(@DrawableRes icon: Int, @StringRes text: Int) {
    Column(
        Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
    ) {
        Icon(painterResource(icon), null, Modifier.size(64.dp), tint = MaterialTheme.colorScheme.primary)
        Text(stringResource(text), textAlign = TextAlign.Center, style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
fun SettingsActionsRoute(viewModel: SettingsActionsGenericViewModel<*>, isTriple: Boolean) {
    val context = LocalContext.current
    val shared = koinActivityViewModel<ContainerSharedViewModel>()
    val results = koinInject<NavigationResults>()
    val fab by viewModel.fabState.collectAsStateWithLifecycle()
    ShowFab(
        when (fab) {
            SettingsActionsGenericViewModel.FabState.HIDDEN -> null
            SettingsActionsGenericViewModel.FabState.ADD -> FabAction.ADD_ACTION
            SettingsActionsGenericViewModel.FabState.DELETE -> FabAction.DELETE
        }
    )
    val events = remember(viewModel) {
        merge(
            results.receive<TapTapUIAction>(NavigationResults.Key.ACTION).map { ActionsEvent.Added(it) },
            results.receive<Pair<Int, Int>>(NavigationResults.Key.WHEN_GATES_SIZE).map { ActionsEvent.WhenGatesSize(it.first, it.second) },
            shared.fabClicked.mapNotNull {
                when (it) {
                    FabAction.ADD_ACTION -> ActionsEvent.Add
                    FabAction.DELETE -> ActionsEvent.Delete
                    else -> null
                }
            }
        )
    }
    val header: (@Composable () -> Unit)? = (viewModel as? SettingsActionsTripleViewModel)?.let { triple ->
        {
            val enabled by triple.tripleTapEnabled.asFlow().collectAsState(triple.tripleTapEnabled.getSync())
            ModeSwitch(
                stringResource(R.string.switch_triple), enabled, true, triple::onTripleTapSwitchClicked,
                Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }
    }
    SettingsActionsScreen(
        viewModel, events, { shared.restartService(context) },
        if (isTriple) R.drawable.ic_actions_triple else R.drawable.ic_actions,
        if (isTriple) R.string.settings_actions_triple_empty else R.string.settings_actions_double_empty,
        header
    )
}
