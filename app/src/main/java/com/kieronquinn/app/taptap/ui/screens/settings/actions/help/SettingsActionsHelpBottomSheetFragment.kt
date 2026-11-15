package com.kieronquinn.app.taptap.ui.screens.settings.actions.help

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.kieronquinn.app.taptap.R
import com.kieronquinn.app.taptap.ui.base.ComposeBottomSheetFragment
import com.kieronquinn.app.taptap.ui.compose.SheetMessage

class SettingsActionsHelpBottomSheetFragment: ComposeBottomSheetFragment() {

    @Composable
    override fun ColumnScope.Content() = SheetMessage(
        title = stringResource(R.string.bs_help_action_title),
        text = LocalContext.current.getText(R.string.bs_help_action),
        positive = stringResource(android.R.string.ok),
        onPositive = ::dismiss
    )

}
