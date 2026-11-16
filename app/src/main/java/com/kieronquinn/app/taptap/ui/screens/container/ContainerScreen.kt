package com.kieronquinn.app.taptap.ui.screens.container

import com.kieronquinn.app.taptap.ui.navigation.rememberBackgroundNavEntryDecorator
import com.kieronquinn.app.taptap.ui.navigation.slideBack
import com.kieronquinn.app.taptap.ui.navigation.slideForward
import com.kieronquinn.app.taptap.ui.navigation.slidePredictiveBack
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.ui.graphics.Color
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ShortNavigationBar
import androidx.compose.material3.ShortNavigationBarItem
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.scene.DialogSceneStrategy
import androidx.navigation3.ui.NavDisplay
import com.kieronquinn.app.taptap.R
import com.kieronquinn.app.taptap.components.navigation.ContainerNavigation
import com.kieronquinn.app.taptap.ui.compose.LocalContainerPadding
import com.kieronquinn.app.taptap.ui.compose.LocalTopBarActions
import com.kieronquinn.app.taptap.ui.compose.TopBarActionsHolder
import com.kieronquinn.app.taptap.ui.navigation.BottomSheetSceneStrategy
import com.kieronquinn.app.taptap.ui.navigation.ContainerKey
import com.kieronquinn.app.taptap.ui.navigation.NavigationEffects
import com.kieronquinn.app.taptap.ui.navigation.containerEntries
import com.kieronquinn.app.taptap.ui.screens.container.ContainerSharedViewModel.FabState
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinActivityViewModel
import org.koin.compose.viewmodel.koinViewModel

private val tabs = listOf(
    Triple(ContainerKey.Main, R.drawable.ic_gesture, R.string.setting_bottom_nav_gesture),
    Triple(ContainerKey.Options, R.drawable.ic_settings, R.string.setting_bottom_nav_settings),
    Triple(ContainerKey.More, R.drawable.ic_more, R.string.setting_bottom_nav_more)
)

@Composable
fun ContainerScreen(viewModel: ContainerViewModel = koinViewModel()) {
    val context = LocalContext.current
    val navigation = koinInject<ContainerNavigation>()
    val shared = koinActivityViewModel<ContainerSharedViewModel>()
    val scope = rememberCoroutineScope()
    val backStack = navigation.backStack
    val top = backStack.last() as ContainerKey
    val titleKey = backStack.lastOrNull { (it as ContainerKey).titleRes != null } as? ContainerKey
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val snackbarHostState = remember { SnackbarHostState() }
    val topBarActions = remember { TopBarActionsHolder() }
    val fab by shared.fabState.collectAsStateWithLifecycle()
    val showUpdate by viewModel.showUpdateSnackbar.collectAsStateWithLifecycle()
    NavigationEffects(navigation)
    LaunchedEffect(Unit) { viewModel.writeSettingsVersion() }
    LaunchedEffect(shared) { shared.columbusSettingPhoenixBus.collect { viewModel.phoenix() } }
    LaunchedEffect(shared) { shared.snackbarBus.collect { snackbarHostState.showSnackbar(it.toString()) } }
    LaunchedEffect(top) {
        scrollBehavior.state.heightOffset = 0f
        viewModel.setCanShowSnackbar(top in tabs.map { it.first })
    }
    val updateText = stringResource(R.string.snackbar_update)
    val updateAction = stringResource(R.string.snackbar_update_button)
    LaunchedEffect(showUpdate) {
        if (!showUpdate) return@LaunchedEffect
        val result = snackbarHostState.showSnackbar(updateText, updateAction, withDismissAction = true, duration = SnackbarDuration.Indefinite)
        if (result == SnackbarResult.ActionPerformed) viewModel.onUpdateClicked() else viewModel.onUpdateDismissed()
    }
    CompositionLocalProvider(LocalTopBarActions provides topBarActions) {
        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            topBar = {
                LargeFlexibleTopAppBar(
                    title = {
                        Crossfade(titleKey?.titleRes, animationSpec = tween(300)) { res ->
                            Text(res?.let { stringResource(it) }.orEmpty())
                        }
                    },
                    navigationIcon = {
                        AnimatedVisibility(backStack.size > 1, enter = fadeIn(tween(300)), exit = fadeOut(tween(300))) {
                            IconButton(onClick = { scope.launch { navigation.navigateBack() } }) {
                                Icon(painterResource(R.drawable.ic_back), stringResource(R.string.back))
                            }
                        }
                    },
                    actions = { topBarActions.actions.value?.second?.invoke(this) },
                    scrollBehavior = scrollBehavior,
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer,
                        scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainer
                    )
                )
            },
            bottomBar = {
                ShortNavigationBar {
                    tabs.forEach { (key, icon, label) ->
                        ShortNavigationBarItem(
                            selected = backStack.first() == key,
                            onClick = { viewModel.onNavigationItemClicked(key) },
                            icon = { Icon(painterResource(icon), null) },
                            label = { Text(stringResource(label)) }
                        )
                    }
                }
            },
            floatingActionButton = {
                val action = (fab as? FabState.Shown)?.action
                var lastAction by remember { mutableStateOf(action) }
                if (action != null) lastAction = action
                AnimatedVisibility(action != null, enter = scaleIn(), exit = scaleOut()) {
                    val shown = lastAction ?: return@AnimatedVisibility
                    val destructive = shown == ContainerSharedViewModel.FabState.FabAction.DELETE
                    val colors = MaterialTheme.colorScheme
                    val effects = MaterialTheme.motionScheme.defaultEffectsSpec<Color>()
                    val container by animateColorAsState(if (destructive) colors.errorContainer else colors.primaryContainer, effects)
                    val content by animateColorAsState(if (destructive) colors.onErrorContainer else colors.onPrimaryContainer, effects)
                    val fadeInSpec = MaterialTheme.motionScheme.defaultEffectsSpec<Float>()
                    val fadeOutSpec = MaterialTheme.motionScheme.fastEffectsSpec<Float>()
                    val swap: AnimatedContentTransitionScope<*>.() -> ContentTransform = {
                        (fadeIn(fadeInSpec) + slideInVertically { it / 2 }) togetherWith
                            (fadeOut(fadeOutSpec) + slideOutVertically { -it / 2 }) using
                            SizeTransform(clip = false)
                    }
                    ExtendedFloatingActionButton(
                        onClick = { shared.onFabClicked(shown) },
                        containerColor = container,
                        contentColor = content,
                        icon = { AnimatedContent(shown.iconRes, transitionSpec = swap) { Icon(painterResource(it), null) } },
                        text = { AnimatedContent(shown.labelRes, transitionSpec = swap) { Text(stringResource(it)) } }
                    )
                }
            },
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { padding ->
            CompositionLocalProvider(LocalContainerPadding provides padding) {
                NavDisplay(
                    backStack = backStack,
                    modifier = Modifier.fillMaxSize().padding(top = padding.calculateTopPadding()),
                    onBack = { scope.launch { navigation.navigateBack() } },
                    entryDecorators = listOf(
                        rememberSaveableStateHolderNavEntryDecorator(),
                        rememberViewModelStoreNavEntryDecorator(),
                        rememberBackgroundNavEntryDecorator(MaterialTheme.colorScheme.surfaceContainer)
                    ),
                    sceneStrategies = listOf(DialogSceneStrategy(), BottomSheetSceneStrategy()),
                    transitionSpec = slideForward(),
                    popTransitionSpec = slideBack(),
                    predictivePopTransitionSpec = slidePredictiveBack(),
                    entryProvider = containerEntries(context)
                )
            }
        }
    }
}
