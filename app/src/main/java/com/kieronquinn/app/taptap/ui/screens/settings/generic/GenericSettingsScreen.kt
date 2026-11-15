package com.kieronquinn.app.taptap.ui.screens.settings.generic

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.LifecycleResumeEffect
import com.kieronquinn.app.taptap.ui.compose.SettingsList
import com.kieronquinn.app.taptap.ui.compose.containerBottomPadding
import com.kieronquinn.app.taptap.ui.screens.container.ContainerSharedViewModel
import com.kieronquinn.app.taptap.ui.screens.settings.generic.GenericSettingsViewModel.SettingsItem
import org.koin.compose.viewmodel.koinActivityViewModel

@Composable
fun GenericSettingsScreen(
    viewModel: GenericSettingsViewModel,
    items: List<SettingsItem>,
    refreshKey: Any? = null,
    contentPadding: PaddingValues = containerBottomPadding(),
    onResume: () -> Unit = {},
    header: (@Composable () -> Unit)? = null
) {
    val context = LocalContext.current
    val containerViewModel = koinActivityViewModel<ContainerSharedViewModel>()
    var resumes by remember { mutableIntStateOf(0) }
    LifecycleResumeEffect(viewModel) {
        resumes++
        onResume()
        onPauseOrDispose {}
    }
    LaunchedEffect(viewModel) {
        viewModel.restartService?.collect { containerViewModel.restartService(context) }
    }
    SettingsList(
        items = items,
        modifier = Modifier.fillMaxSize(),
        refreshKey = resumes to refreshKey,
        contentPadding = contentPadding,
        header = header?.let { h -> { item { h() } } }
    )
}
