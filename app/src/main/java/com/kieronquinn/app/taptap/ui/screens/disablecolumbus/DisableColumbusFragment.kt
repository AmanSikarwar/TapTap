package com.kieronquinn.app.taptap.ui.screens.disablecolumbus

import androidx.compose.runtime.Composable
import com.kieronquinn.app.taptap.ui.base.ComposeFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class DisableColumbusFragment: ComposeFragment() {

    private val viewModel by viewModel<DisableColumbusViewModel>()

    @Composable
    override fun Content() = DisableColumbusScreen(viewModel)

}
