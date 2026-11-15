package com.kieronquinn.app.taptap.ui.screens.setup.info.warning

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.kieronquinn.app.taptap.R
import com.kieronquinn.app.taptap.ui.base.ComposeBottomSheetFragment
import com.kieronquinn.app.taptap.ui.compose.SheetMessage

class SetupInfoWarningBottomSheetFragment: ComposeBottomSheetFragment() {

    @Composable
    override fun ColumnScope.Content() = SheetMessage(
        title = stringResource(R.string.setup_foss_info_bs_title),
        text = stringResource(R.string.setup_foss_info_bs_content),
        positive = stringResource(android.R.string.ok),
        onPositive = ::dismiss
    )

}
