package com.kieronquinn.app.taptap.ui.screens.settings.actions.tripletap

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kieronquinn.app.taptap.R
import com.kieronquinn.app.taptap.repositories.room.actions.TripleTapAction
import com.kieronquinn.app.taptap.ui.compose.ModeSwitch
import com.kieronquinn.app.taptap.ui.screens.settings.actions.SettingsActionsGenericFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsActionsTripleFragment: SettingsActionsGenericFragment<TripleTapAction>() {

    override val viewModel by viewModel<SettingsActionsTripleViewModel>()
    override val emptyIcon = R.drawable.ic_actions_triple
    override val emptyText = R.string.settings_actions_triple_empty

    override val header: @Composable () -> Unit = {
        val enabled by viewModel.tripleTapEnabled.asFlow().collectAsState(viewModel.tripleTapEnabled.getSync())
        ModeSwitch(stringResource(R.string.switch_triple), enabled, true, viewModel::onTripleTapSwitchClicked, Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
    }

}
