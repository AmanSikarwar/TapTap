package com.kieronquinn.app.taptap.ui.screens.settings.shared.root

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.kieronquinn.app.taptap.R
import com.kieronquinn.app.taptap.ui.base.ComposeBottomSheetFragment
import com.kieronquinn.app.taptap.ui.compose.SheetMessage

class SettingsSharedNoRootBottomSheetFragment: ComposeBottomSheetFragment() {

    @Composable
    override fun ColumnScope.Content() = SheetMessage(
        title = stringResource(R.string.bs_no_root_title),
        text = LocalContext.current.getText(R.string.bs_no_root_content),
        positive = stringResource(android.R.string.ok),
        onPositive = ::dismiss
    )

}
