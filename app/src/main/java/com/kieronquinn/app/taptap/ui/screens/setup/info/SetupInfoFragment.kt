package com.kieronquinn.app.taptap.ui.screens.setup.info

import androidx.compose.runtime.Composable
import com.kieronquinn.app.taptap.ui.base.ProvidesBack
import com.kieronquinn.app.taptap.ui.screens.setup.base.BaseSetupFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class SetupInfoFragment: BaseSetupFragment(), ProvidesBack {

    override val viewModel by viewModel<SetupInfoViewModel>()

    @Composable
    override fun Content() = SetupInfoScreen(viewModel)

    override fun onBackPressed() = viewModel.onBackPressed()

}
