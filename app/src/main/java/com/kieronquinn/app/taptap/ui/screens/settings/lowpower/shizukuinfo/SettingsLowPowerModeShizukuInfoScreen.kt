package com.kieronquinn.app.taptap.ui.screens.settings.lowpower.shizukuinfo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.kieronquinn.app.taptap.R
import com.kieronquinn.app.taptap.ui.compose.MessageCard
import com.kieronquinn.app.taptap.ui.compose.containerBottomPadding

@Composable
fun SettingsLowPowerModeShizukuInfoScreen(viewModel: SettingsLowPowerModeShizukuInfoViewModel) {
    val context = LocalContext.current
    Column(
        Modifier
            .fillMaxSize()
            .nestedScroll(rememberNestedScrollInteropConnection())
            .verticalScroll(rememberScrollState())
            .padding(containerBottomPadding())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ShizukuCards({ viewModel.onShizukuClicked(context) }, { viewModel.onSuiClicked(context) })
    }
}

@Composable
fun ShizukuCards(onShizuku: () -> Unit, onSui: () -> Unit) {
    MessageCard(
        R.drawable.ic_shizuku,
        stringResource(R.string.settings_shared_shizuku_permission_flow_error_card_shizuku_content),
        title = stringResource(R.string.settings_shared_shizuku_permission_flow_error_card_shizuku_title),
        tintIcon = false
    ) {
        Button(onClick = onShizuku) { Text(stringResource(R.string.settings_shared_shizuku_permission_flow_error_card_shizuku_button)) }
    }
    MessageCard(
        R.drawable.ic_sui,
        stringResource(R.string.settings_shared_shizuku_permission_flow_error_card_sui_content),
        title = stringResource(R.string.settings_shared_shizuku_permission_flow_error_card_sui_title),
        tintIcon = false
    ) {
        Button(onClick = onSui) { Text(stringResource(R.string.settings_shared_shizuku_permission_flow_error_card_sui_button)) }
    }
}
