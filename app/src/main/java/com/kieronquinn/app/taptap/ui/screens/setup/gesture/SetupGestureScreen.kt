package com.kieronquinn.app.taptap.ui.screens.setup.gesture

import androidx.compose.material3.BottomSheetDefaults
import com.kieronquinn.app.taptap.ui.navigation.rememberBackgroundNavEntryDecorator
import com.kieronquinn.app.taptap.ui.navigation.slideBack
import com.kieronquinn.app.taptap.ui.navigation.slideForward
import com.kieronquinn.app.taptap.ui.navigation.slidePredictiveBack
import androidx.compose.ui.platform.LocalResources
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.kieronquinn.app.taptap.R
import com.kieronquinn.app.taptap.components.navigation.GestureConfigurationNavigation
import com.kieronquinn.app.taptap.ui.compose.MessageCard
import com.kieronquinn.app.taptap.ui.compose.StatusIndicator
import com.kieronquinn.app.taptap.ui.compose.StatusScreen
import com.kieronquinn.app.taptap.ui.compose.TintedLottie
import com.kieronquinn.app.taptap.ui.navigation.GestureConfigurationKey
import com.kieronquinn.app.taptap.ui.navigation.NavigationEffects
import com.kieronquinn.app.taptap.ui.screens.container.ContainerSharedViewModel
import com.kieronquinn.app.taptap.ui.screens.settings.modelpicker.SettingsModelPickerScreen
import com.kieronquinn.app.taptap.ui.screens.setup.gesture.SetupGestureViewModel.InfoCard
import com.kieronquinn.app.taptap.ui.screens.setup.gesture.SetupGestureViewModel.State
import com.kieronquinn.app.taptap.ui.screens.setup.gesture.configuration.SetupGestureConfigurationScreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinActivityViewModel
import org.koin.compose.viewmodel.koinViewModel

private enum class Ripple { SINGLE, DOUBLE, TRIPLE }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetupGestureScreen(viewModel: SetupGestureViewModel = koinViewModel()) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsStateWithLifecycle()
    if (state is State.Loading) {
        StatusScreen(StatusIndicator.Indeterminate, stringResource(R.string.setup_gesture_loading))
        return
    }
    val scope = rememberCoroutineScope()
    LifecycleResumeEffect(viewModel) {
        val job = scope.launch {
            delay(100L)
            viewModel.startDemoMode(context)
        }
        onPauseOrDispose {
            job.cancel()
            viewModel.stopDemoMode(context)
        }
    }
    val configurationNavigation = koinInject<GestureConfigurationNavigation>()
    NavigationEffects(configurationNavigation)
    val configStack = configurationNavigation.backStack
    LaunchedEffect(configStack.size) { viewModel.setConfigurationBackAvailable(configStack.size > 1) }
    val sheetState = rememberStandardBottomSheetState()
    val scaffoldState = rememberBottomSheetScaffoldState(sheetState)
    LaunchedEffect(sheetState) {
        snapshotFlow { sheetState.targetValue == SheetValue.Expanded }.collect {
            viewModel.onBottomSheetSlideOffsetChange(if (it) 1f else 0f)
        }
    }
    LaunchedEffect(viewModel) {
        launch { viewModel.bottomSheetCloseBus.collect { sheetState.partialExpand() } }
        launch { viewModel.bottomSheetOpenBus.collect { sheetState.expand() } }
    }
    BackHandler { viewModel.onBackPressed() }
    val expanded = sheetState.targetValue == SheetValue.Expanded
    val draggable by viewModel.bottomSheetDraggable.collectAsStateWithLifecycle()
    val peek = dimensionResource(R.dimen.setup_gesture_bottom_sheet_peek_size) +
        WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetPeekHeight = peek,
        sheetSwipeEnabled = draggable,
        topBar = {
            TopAppBar(
                title = {},
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                navigationIcon = {
                    IconButton(onClick = { viewModel.onBackPressed() }) {
                        Icon(painterResource(R.drawable.ic_back), stringResource(R.string.back))
                    }
                }
            )
        },
        sheetContent = {
            Column(Modifier.fillMaxHeight(0.9f)) {
                val top = configStack.last() as GestureConfigurationKey
                Row(
                    Modifier
                        .fillMaxWidth()
                        .clickable(enabled = draggable) {
                            scope.launch { if (expanded) sheetState.partialExpand() else sheetState.expand() }
                        }
                        .padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { viewModel.onConfigurationBackPressed() }) {
                        Icon(
                            painterResource(
                                when {
                                    configStack.size > 1 -> R.drawable.ic_back
                                    expanded -> R.drawable.ic_close
                                    else -> R.drawable.ic_chevron_expand
                                }
                            ),
                            null,
                            Modifier.rotate(if (configStack.size == 1 && !expanded) 180f else 0f)
                        )
                    }
                    Text(stringResource(top.titleRes), style = MaterialTheme.typography.titleLarge)
                }
                NavDisplay(
                    backStack = configStack,
                    onBack = { viewModel.onConfigurationBackPressed() },
                    entryDecorators = listOf(
                        rememberSaveableStateHolderNavEntryDecorator(),
                        rememberViewModelStoreNavEntryDecorator(),
                        rememberBackgroundNavEntryDecorator(BottomSheetDefaults.ContainerColor)
                    ),
                    transitionSpec = slideForward(),
                    popTransitionSpec = slideBack(),
                    predictivePopTransitionSpec = slidePredictiveBack(),
                    entryProvider = entryProvider {
                        entry<GestureConfigurationKey.Configuration> { SetupGestureConfigurationScreen() }
                        entry<GestureConfigurationKey.ModelPicker> {
                            val shared = koinActivityViewModel<ContainerSharedViewModel>()
                            SettingsModelPickerScreen(koinViewModel()) { shared.restartService(context) }
                        }
                    }
                )
            }
        }
    ) { padding ->
        GestureDemo(viewModel, Modifier.padding(padding))
    }
}

@Composable
private fun GestureDemo(viewModel: SetupGestureViewModel, modifier: Modifier) {
    val infoCard by viewModel.infoCard.collectAsStateWithLifecycle()
    val lottieSize = dimensionResource(R.dimen.setup_gesture_lottie_size)
    val ripples = remember(viewModel) {
        merge(
            viewModel.tapEvents.map { Ripple.SINGLE },
            viewModel.doubleTapEvents.map { Ripple.DOUBLE },
            viewModel.tripleTapEvents.map { Ripple.TRIPLE }
        )
    }
    Column(
        modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterVertically)
    ) {
        Box(contentAlignment = Alignment.Center) {
            TapRipples(ripples, lottieSize, Modifier.fillMaxWidth().size(lottieSize * 2))
            val lottie = when (Build.MODEL) {
                "Pixel 6 Pro", "Pixel 6" -> R.raw.gesture_columbus_circle_p6
                else -> R.raw.gesture_columbus_circle_p5
            }
            TintedLottie(lottie, MaterialTheme.colorScheme.primary, ".blue400", modifier = Modifier.size(lottieSize))
        }
        MessageCard(
            icon = infoCard.icon,
            text = LocalResources.current.getText(infoCard.contentRes),
            containerColor = if (infoCard == InfoCard.SUCCESS) MaterialTheme.colorScheme.primaryContainer
                else MaterialTheme.colorScheme.surfaceContainerHigh,
            onClick = if (infoCard == InfoCard.SUCCESS) viewModel::onNextClicked else null
        )
    }
}

@Composable
private fun TapRipples(events: Flow<Ripple>, minSize: Dp, modifier: Modifier) {
    val active = remember { mutableStateListOf<Pair<Ripple, Animatable<Float, *>>>() }
    LaunchedEffect(events) {
        events.collect { type ->
            val progress = Animatable(0f)
            val entry = type to progress
            active.add(entry)
            launch {
                progress.animateTo(1f, tween(1000, easing = LinearEasing))
                active.remove(entry)
            }
        }
    }
    val single = MaterialTheme.colorScheme.secondaryContainer
    val primary = MaterialTheme.colorScheme.primary
    val widths = with(LocalDensity.current) {
        mapOf(
            Ripple.SINGLE to dimensionResource(R.dimen.ripple_view_single_width).toPx(),
            Ripple.DOUBLE to dimensionResource(R.dimen.ripple_view_double_width).toPx(),
            Ripple.TRIPLE to dimensionResource(R.dimen.ripple_view_triple_width).toPx()
        )
    }
    val minRadius = with(LocalDensity.current) { (minSize / 2).toPx() }
    Canvas(modifier) {
        active.forEach { (type, progress) ->
            val value = progress.value
            drawCircle(
                color = (if (type == Ripple.SINGLE) single else primary).copy(alpha = 1f - value),
                radius = lerp(minRadius, size.minDimension / 2f, value),
                style = Stroke(widths.getValue(type))
            )
        }
    }
}
