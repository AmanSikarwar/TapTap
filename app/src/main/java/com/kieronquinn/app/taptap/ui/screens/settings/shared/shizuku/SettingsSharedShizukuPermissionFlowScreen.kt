package com.kieronquinn.app.taptap.ui.screens.settings.shared.shizuku

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kieronquinn.app.taptap.R
import com.kieronquinn.app.taptap.models.shared.SharedArgument
import com.kieronquinn.app.taptap.ui.compose.StatusIndicator
import com.kieronquinn.app.taptap.ui.compose.StatusScreen
import com.kieronquinn.app.taptap.ui.compose.containerBottomPadding
import com.kieronquinn.app.taptap.ui.screens.settings.lowpower.shizukuinfo.ShizukuCards
import com.kieronquinn.app.taptap.ui.screens.settings.shared.shizuku.SettingsSharedShizukuPermissionFlowViewModel.State

@Composable
fun SettingsSharedShizukuPermissionFlowScreen(
    viewModel: SettingsSharedShizukuPermissionFlowViewModel,
    argument: SharedArgument,
    onResult: (Boolean) -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    LaunchedEffect(state) {
        when (state) {
            State.PermissionGranted -> {
                onResult(true)
                viewModel.popBackstack()
            }
            State.NoShizuku -> onResult(false)
            else -> Unit
        }
    }
    if (state != State.NoShizuku) {
        StatusScreen(
            StatusIndicator.Indeterminate,
            stringResource(R.string.settings_shared_shizuku_permission_flow_title),
            stringResource(R.string.settings_shared_shizuku_permission_flow_content)
        )
        return
    }
    val target = if (argument.gate != null) R.string.settings_shared_shizuku_permission_flow_error_content_gate
        else R.string.settings_shared_shizuku_permission_flow_error_content_action
    Column(
        Modifier
            .fillMaxSize()
            .nestedScroll(rememberNestedScrollInteropConnection())
            .verticalScroll(rememberScrollState())
            .padding(containerBottomPadding())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(stringResource(R.string.settings_shared_shizuku_permission_flow_error_title), style = MaterialTheme.typography.headlineSmall)
        Text(stringResource(R.string.settings_shared_shizuku_permission_flow_error_content, stringResource(target)))
        ShizukuCards({ viewModel.onShizukuClicked(context) }, { viewModel.onSuiClicked(context) })
    }
}
