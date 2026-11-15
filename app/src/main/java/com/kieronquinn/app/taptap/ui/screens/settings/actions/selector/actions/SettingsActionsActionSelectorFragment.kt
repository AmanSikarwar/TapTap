package com.kieronquinn.app.taptap.ui.screens.settings.actions.selector.actions

import android.os.Bundle
import android.view.View
import androidx.compose.runtime.Composable
import androidx.navigation.fragment.navArgs
import com.kieronquinn.app.taptap.ui.base.BackAvailable
import com.kieronquinn.app.taptap.ui.base.ProvidesTitle
import com.kieronquinn.app.taptap.ui.screens.settings.actions.selector.SettingsActionsActionSelectorScreen
import com.kieronquinn.app.taptap.ui.screens.settings.actions.selector.SettingsActionsAddGenericFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsActionsActionSelectorFragment : SettingsActionsAddGenericFragment(), ProvidesTitle, BackAvailable {

    override val viewModel by viewModel<SettingsActionsActionSelectorViewModel>()
    private val args by navArgs<SettingsActionsActionSelectorFragmentArgs>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.setupWithCategory(args.category, requireContext())
    }

    @Composable
    override fun Content() = SettingsActionsActionSelectorScreen(viewModel, { onActionClicked(it) }, { showSnackbarForChip(it) })

    override fun getTitle(): CharSequence? = context?.getString(args.category.labelRes)

}
