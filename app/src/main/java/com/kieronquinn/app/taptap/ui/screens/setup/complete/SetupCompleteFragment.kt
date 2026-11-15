package com.kieronquinn.app.taptap.ui.screens.setup.complete

import androidx.compose.runtime.Composable
import com.kieronquinn.app.taptap.ui.base.ProvidesBack
import com.kieronquinn.app.taptap.ui.screens.setup.base.BaseSetupFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class SetupCompleteFragment: BaseSetupFragment(), ProvidesBack {

    override val viewModel by viewModel<SetupCompleteViewModel>()

    @Composable
    override fun Content() = SetupCompleteScreen(viewModel)

    override fun onBackPressed() = viewModel.onBackPressed()

}
