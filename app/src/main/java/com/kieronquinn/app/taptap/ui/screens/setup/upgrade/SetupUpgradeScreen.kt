package com.kieronquinn.app.taptap.ui.screens.setup.upgrade

import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LoadingIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.kieronquinn.app.taptap.R
import com.kieronquinn.app.taptap.ui.compose.MessageScreen

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SetupUpgradeScreen(viewModel: SetupUpgradeViewModel) {
    val context = LocalContext.current
    LaunchedEffect(viewModel) { viewModel.startUpgrade(context) }
    MessageScreen(title = stringResource(R.string.setup_upgrade_loading), icon = { LoadingIndicator() }) {}
}
