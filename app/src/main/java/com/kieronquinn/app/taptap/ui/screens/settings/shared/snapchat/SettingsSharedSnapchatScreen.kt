package com.kieronquinn.app.taptap.ui.screens.settings.shared.snapchat

import com.kieronquinn.app.taptap.components.navigation.NavigationResults
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import com.kieronquinn.app.taptap.models.shared.SharedArgument
import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kieronquinn.app.taptap.R
import com.kieronquinn.app.taptap.repositories.snapchat.SnapchatRepository.QuickTapToSnapState
import com.kieronquinn.app.taptap.ui.compose.MessageCard
import com.kieronquinn.app.taptap.ui.compose.StatusIndicator
import com.kieronquinn.app.taptap.ui.compose.StatusScreen
import com.kieronquinn.app.taptap.ui.screens.settings.shared.snapchat.SettingsSharedSnapchatViewModel.State

@Composable
fun SettingsSharedSnapchatScreen(viewModel: SettingsSharedSnapchatViewModel, onResult: (Boolean) -> Unit) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    LifecycleResumeEffect(viewModel) {
        viewModel.onResume()
        onPauseOrDispose {}
    }
    val loaded = (state as? State.Loaded)?.state
    LaunchedEffect(loaded) {
        loaded ?: return@LaunchedEffect
        onResult(loaded == QuickTapToSnapState.AVAILABLE)
        when (loaded) {
            QuickTapToSnapState.AVAILABLE -> viewModel.popBackstack(true)
            QuickTapToSnapState.ERROR -> {
                Toast.makeText(context, R.string.settings_shared_snapchat_setup_error, Toast.LENGTH_LONG).show()
                viewModel.popBackstack(true)
            }
            else -> Unit
        }
    }
    val card: Triple<Int, Int, Pair<Int, () -> Unit>>? = when (loaded) {
        QuickTapToSnapState.NEEDS_SETUP_ROOT -> Triple(
            R.string.settings_shared_snapchat_setup_root_title, R.string.settings_shared_snapchat_setup_root_content,
            R.string.settings_shared_snapchat_setup_root_button to { viewModel.onSnapchatClicked(context) }
        )
        QuickTapToSnapState.NEEDS_SETUP -> Triple(
            R.string.settings_shared_snapchat_setup_no_root_title, R.string.settings_shared_snapchat_setup_no_root_content,
            R.string.settings_shared_snapchat_setup_no_root_button to viewModel::onInstructionsClicked
        )
        QuickTapToSnapState.UNAVAILABLE -> Triple(
            R.string.settings_shared_snapchat_setup_incompatible_title, R.string.settings_shared_snapchat_setup_incompatible_content,
            R.string.settings_shared_snapchat_setup_incompatible_button to { viewModel.popBackstack(false) }
        )
        else -> null
    }
    if (card == null) {
        StatusScreen(
            StatusIndicator.Indeterminate,
            stringResource(R.string.settings_shared_snapchat_loading_title),
            stringResource(R.string.settings_shared_snapchat_loading_content)
        )
    } else {
        MessageCard(
            R.drawable.ic_about,
            LocalContext.current.getText(card.second),
            Modifier.fillMaxSize().wrapContentHeight().padding(16.dp),
            title = stringResource(card.first)
        ) {
            Button(onClick = card.third.second) { Text(stringResource(card.third.first)) }
        }
    }
}

@Composable
fun SettingsSharedSnapchatRoute(argument: SharedArgument, viewModel: SettingsSharedSnapchatViewModel = koinViewModel()) {
    val results = koinInject<NavigationResults>()
    SettingsSharedSnapchatScreen(viewModel) { results.send(NavigationResults.Key.SNAPCHAT, argument to it) }
}
