package com.kieronquinn.app.taptap.ui.screens.setup.info

import com.kieronquinn.app.taptap.ui.compose.MessageCard
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.kieronquinn.app.taptap.R
import com.kieronquinn.app.taptap.ui.compose.SetupBottomBar
import com.kieronquinn.app.taptap.ui.compose.SetupScaffold
import com.kieronquinn.app.taptap.ui.compose.toAnnotatedString

@Composable
fun SetupInfoScreen(viewModel: SetupInfoViewModel) {
    val context = LocalContext.current
    SetupScaffold(
        onBack = { viewModel.onBackPressed() },
        title = stringResource(R.string.setup_foss_info_title),
        bottomBar = {
            SetupBottomBar {
                Button(
                    onClick = { viewModel.onNextClicked(context) },
                    modifier = Modifier.fillMaxWidth().height(ButtonDefaults.MediumContainerHeight),
                    contentPadding = ButtonDefaults.MediumContentPadding
                ) { Text(stringResource(R.string.setup_info_next), style = ButtonDefaults.textStyleFor(ButtonDefaults.MediumContainerHeight)) }
            }
        }
    ) {
        Text(stringResource(R.string.setup_foss_info_content).toAnnotatedString(viewModel::onLinkClicked))
        MessageCard(R.drawable.ic_warning, stringResource(R.string.setup_foss_info_warning), Modifier.padding(top = 16.dp), onClick = viewModel::onSourceClicked)
        MessageCard(R.drawable.ic_about, stringResource(R.string.setup_foss_info_content_lower), Modifier.padding(top = 16.dp))
    }
}
