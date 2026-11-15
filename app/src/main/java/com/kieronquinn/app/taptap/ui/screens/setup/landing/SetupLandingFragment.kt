package com.kieronquinn.app.taptap.ui.screens.setup.landing

import androidx.compose.runtime.Composable
import com.kieronquinn.app.taptap.ui.screens.setup.base.BaseSetupFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class SetupLandingFragment: BaseSetupFragment() {

    override val viewModel by viewModel<SetupLandingViewModel>()

    @Composable
    override fun Content() = SetupLandingScreen(viewModel)

}
