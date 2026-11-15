package com.kieronquinn.app.taptap.ui.screens.nogyro

import androidx.compose.runtime.Composable
import com.kieronquinn.app.taptap.ui.base.ComposeFragment

class NoGyroscopeFragment: ComposeFragment() {

    @Composable
    override fun Content() = NoGyroscopeScreen(onClose = { requireActivity().finish() })

}
