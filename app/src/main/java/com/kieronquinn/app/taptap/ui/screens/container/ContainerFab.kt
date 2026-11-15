package com.kieronquinn.app.taptap.ui.screens.container

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import com.kieronquinn.app.taptap.ui.screens.container.ContainerSharedViewModel.FabState
import org.koin.compose.viewmodel.koinActivityViewModel

@Composable
fun ShowFab(state: FabState) {
    val shared = koinActivityViewModel<ContainerSharedViewModel>()
    LaunchedEffect(state) { shared.setFabState(state) }
    DisposableEffect(shared) { onDispose { shared.setFabState(FabState.Hidden) } }
}

@Composable
fun ShowFab(action: FabState.FabAction?) = ShowFab(action?.let { FabState.Shown(it) } ?: FabState.Hidden)
