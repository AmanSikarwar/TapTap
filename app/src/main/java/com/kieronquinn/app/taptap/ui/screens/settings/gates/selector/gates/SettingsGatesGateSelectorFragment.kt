package com.kieronquinn.app.taptap.ui.screens.settings.gates.selector.gates

import android.os.Bundle
import android.view.View
import androidx.compose.runtime.Composable
import androidx.navigation.fragment.navArgs
import com.kieronquinn.app.taptap.ui.base.BackAvailable
import com.kieronquinn.app.taptap.ui.base.ProvidesTitle
import com.kieronquinn.app.taptap.ui.screens.settings.gates.selector.SettingsGatesAddGenericFragment
import com.kieronquinn.app.taptap.ui.screens.settings.gates.selector.SettingsGatesGateSelectorScreen
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsGatesGateSelectorFragment : SettingsGatesAddGenericFragment(), ProvidesTitle, BackAvailable {

    override val viewModel by viewModel<SettingsGatesGateSelectorViewModel>()
    private val args by navArgs<SettingsGatesGateSelectorFragmentArgs>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.setupWithCategory(args.category, requireContext())
    }

    @Composable
    override fun Content() = SettingsGatesGateSelectorScreen(viewModel, args.isRequirement, { onGateClicked(it) }, { showSnackbarForChip(it) })

    override fun getTitle(): CharSequence? = context?.getString(args.category.labelRes)

}
