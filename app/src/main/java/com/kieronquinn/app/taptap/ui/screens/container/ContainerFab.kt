package com.kieronquinn.app.taptap.ui.screens.container

import androidx.compose.animation.EnterExitState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import com.kieronquinn.app.taptap.ui.screens.container.ContainerSharedViewModel.FabState
import org.koin.compose.viewmodel.koinActivityViewModel

@Composable
fun ShowFab(state: FabState) {
    val shared = koinActivityViewModel<ContainerSharedViewModel>()
    val exiting = LocalNavAnimatedContentScope.current.transition.targetState == EnterExitState.PostExit
    LaunchedEffect(state, exiting) { shared.setFabState(if (exiting) FabState.Hidden else state) }
    DisposableEffect(shared) { onDispose { shared.setFabState(FabState.Hidden) } }
}

@Composable
fun ShowFab(action: FabState.FabAction?) = ShowFab(action?.let { FabState.Shown(it) } ?: FabState.Hidden)
