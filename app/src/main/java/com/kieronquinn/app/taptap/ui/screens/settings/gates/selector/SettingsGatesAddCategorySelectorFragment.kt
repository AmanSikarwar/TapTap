package com.kieronquinn.app.taptap.ui.screens.settings.gates.selector

import androidx.compose.runtime.Composable
import androidx.navigation.fragment.navArgs
import com.kieronquinn.app.taptap.R
import com.kieronquinn.app.taptap.ui.base.BackAvailable
import com.kieronquinn.app.taptap.ui.base.ProvidesTitle
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsGatesAddCategorySelectorFragment : SettingsGatesAddGenericFragment(), BackAvailable, ProvidesTitle {

    override val viewModel by viewModel<SettingsGatesAddCategorySelectorViewModel>()
    private val args by navArgs<SettingsGatesAddCategorySelectorFragmentArgs>()

    @Composable
    override fun Content() = SettingsGatesAddCategorySelectorScreen(viewModel, args.isRequirement, { onGateClicked(it) }, { showSnackbarForChip(it) })

    override fun getTitle(): CharSequence =
        getString(if (args.isRequirement) R.string.fab_add_requirement else R.string.fab_add_gate)

}
