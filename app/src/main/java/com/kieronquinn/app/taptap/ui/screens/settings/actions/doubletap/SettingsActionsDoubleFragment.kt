package com.kieronquinn.app.taptap.ui.screens.settings.actions.doubletap

import com.kieronquinn.app.taptap.R
import com.kieronquinn.app.taptap.repositories.room.actions.DoubleTapAction
import com.kieronquinn.app.taptap.ui.screens.settings.actions.SettingsActionsGenericFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsActionsDoubleFragment: SettingsActionsGenericFragment<DoubleTapAction>() {

    override val viewModel by viewModel<SettingsActionsDoubleViewModel>()
    override val emptyIcon = R.drawable.ic_actions
    override val emptyText = R.string.settings_actions_double_empty

}
