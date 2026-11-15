package com.kieronquinn.app.taptap.ui.screens.settings.actions.selector

import androidx.compose.runtime.Composable
import com.kieronquinn.app.taptap.ui.base.BackAvailable
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsActionsAddCategorySelectorFragment : SettingsActionsAddGenericFragment(), BackAvailable {

    override val viewModel by viewModel<SettingsActionsAddCategorySelectorViewModel>()

    @Composable
    override fun Content() = SettingsActionsAddCategorySelectorScreen(viewModel, { onActionClicked(it) }, { showSnackbarForChip(it) })

}
