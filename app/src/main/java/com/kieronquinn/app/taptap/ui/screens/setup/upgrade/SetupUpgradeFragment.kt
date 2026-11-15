package com.kieronquinn.app.taptap.ui.screens.setup.upgrade

import androidx.compose.runtime.Composable
import com.kieronquinn.app.taptap.ui.base.ComposeFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class SetupUpgradeFragment: ComposeFragment() {

    private val viewModel by viewModel<SetupUpgradeViewModel>()

    @Composable
    override fun Content() = SetupUpgradeScreen(viewModel)

}
