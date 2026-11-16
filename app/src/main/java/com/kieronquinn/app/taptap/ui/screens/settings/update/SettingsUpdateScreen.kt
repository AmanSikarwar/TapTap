package com.kieronquinn.app.taptap.ui.screens.settings.update

import androidx.compose.runtime.LaunchedEffect
import com.kieronquinn.app.taptap.ui.screens.container.ContainerSharedViewModel
import com.kieronquinn.app.taptap.ui.screens.container.ContainerSharedViewModel.FabState.FabAction
import com.kieronquinn.app.taptap.ui.screens.container.ShowFab
import org.koin.compose.viewmodel.koinActivityViewModel
import org.koin.compose.viewmodel.koinViewModel
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kieronquinn.app.taptap.BuildConfig
import com.kieronquinn.app.taptap.R
import com.kieronquinn.app.taptap.models.update.Release
import com.kieronquinn.app.taptap.ui.compose.MarkdownText
import com.kieronquinn.app.taptap.ui.compose.StatusIndicator
import com.kieronquinn.app.taptap.ui.compose.StatusScreen
import com.kieronquinn.app.taptap.ui.compose.containerBottomPadding
import com.kieronquinn.app.taptap.ui.screens.settings.update.SettingsUpdateViewModel.State

@Composable
fun SettingsUpdateScreen(viewModel: SettingsUpdateViewModel) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    LifecycleResumeEffect(viewModel) {
        viewModel.onResume()
        onPauseOrDispose { viewModel.onPause() }
    }
    val installButton = stringResource(R.string.settings_update_open_installer)
    when (val s = state) {
        is State.Loading -> StatusScreen(StatusIndicator.Indeterminate, stringResource(R.string.settings_update_loading))
        is State.Info -> UpdateInfo(s.release, viewModel)
        is State.StartDownload -> StatusScreen(StatusIndicator.Indeterminate, stringResource(R.string.update_downloader_downloading_title))
        is State.Downloading -> StatusScreen(StatusIndicator.Progress(s.progress.toFloat()), stringResource(R.string.update_downloader_downloading_title))
        is State.Done, is State.StartInstall -> StatusScreen(
            StatusIndicator.Icon(R.drawable.ic_update_download_done), stringResource(R.string.settings_update_done),
            button = installButton, onButton = viewModel::startInstall
        )
        is State.Failed -> StatusScreen(
            StatusIndicator.Icon(R.drawable.ic_error_circle), stringResource(R.string.update_downloader_downloading_failed),
            button = installButton, onButton = viewModel::startInstall
        )
    }
}

@Composable
private fun UpdateInfo(release: Release, viewModel: SettingsUpdateViewModel) {
    Column(
        Modifier
            .fillMaxSize()
            .nestedScroll(rememberNestedScrollInteropConnection())
            .verticalScroll(rememberScrollState())
            .padding(containerBottomPadding())
            .padding(bottom = dimensionResource(R.dimen.container_fab_margin))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Card(
            Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.extraLarge,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
        ) {
            Column(Modifier.padding(16.dp)) {
                Text(stringResource(R.string.settings_update_heading, release.versionName), style = MaterialTheme.typography.headlineSmall)
                Text(stringResource(R.string.settings_update_subheading, BuildConfig.VERSION_NAME))
            }
        }
        Text(stringResource(R.string.settings_update_changes), style = MaterialTheme.typography.titleMedium)
        MarkdownText(release.body)
        OutlinedButton(onClick = { viewModel.onDownloadBrowserClicked(release.gitHubUrl) }) {
            Text(stringResource(R.string.update_downloader_github))
        }
    }
}

@Composable
fun SettingsUpdateRoute(release: Release, viewModel: SettingsUpdateViewModel = koinViewModel()) {
    val shared = koinActivityViewModel<ContainerSharedViewModel>()
    val showFab by viewModel.showFab.collectAsStateWithLifecycle()
    LaunchedEffect(release) { viewModel.setupWithRelease(release) }
    LaunchedEffect(viewModel) {
        shared.fabClicked.collect { if (it == FabAction.DOWNLOAD) viewModel.startDownload() }
    }
    ShowFab(FabAction.DOWNLOAD.takeIf { showFab })
    SettingsUpdateScreen(viewModel)
}
