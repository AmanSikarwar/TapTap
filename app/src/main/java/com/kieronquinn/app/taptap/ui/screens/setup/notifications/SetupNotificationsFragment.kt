package com.kieronquinn.app.taptap.ui.screens.setup.notifications

import androidx.compose.runtime.Composable
import com.kieronquinn.app.taptap.ui.base.ProvidesBack
import com.kieronquinn.app.taptap.ui.screens.setup.base.BaseSetupFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class SetupNotificationsFragment: BaseSetupFragment(), ProvidesBack {

    override val viewModel by viewModel<SetupNotificationsViewModel>()

    @Composable
    override fun Content() = SetupNotificationsScreen(viewModel)

    override fun onBackPressed() = viewModel.onBackPressed()

}
