package com.kieronquinn.app.taptap.ui.screens.settings.lowpower.shizukuinfo

import androidx.compose.runtime.Composable
import com.kieronquinn.app.taptap.ui.base.BackAvailable
import com.kieronquinn.app.taptap.ui.base.ComposeFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsLowPowerModeShizukuInfoFragment: ComposeFragment(), BackAvailable {

    private val viewModel by viewModel<SettingsLowPowerModeShizukuInfoViewModel>()

    @Composable
    override fun Content() = SettingsLowPowerModeShizukuInfoScreen(viewModel)

}
