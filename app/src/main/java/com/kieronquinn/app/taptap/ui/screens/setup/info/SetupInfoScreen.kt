package com.kieronquinn.app.taptap.ui.screens.setup.info

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
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
                ExtendedFloatingActionButton(
                    onClick = { viewModel.onNextClicked(context) },
                    icon = { Icon(painterResource(R.drawable.ic_button_next), null) },
                    text = { Text(stringResource(R.string.setup_info_next)) }
                )
            }
        }
    ) {
        Text(stringResource(R.string.setup_foss_info_content).toAnnotatedString(viewModel::onLinkClicked))
        InfoCard(R.drawable.ic_warning, stringResource(R.string.setup_foss_info_warning), viewModel::onSourceClicked)
        InfoCard(R.drawable.ic_about, stringResource(R.string.setup_foss_info_content_lower))
    }
}

@Composable
fun InfoCard(@DrawableRes icon: Int, text: CharSequence, onClick: (() -> Unit)? = null) {
    val colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
    val modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
    val body = @Composable {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(painterResource(icon), null)
            Text(text.toAnnotatedString(), Modifier.padding(start = 16.dp))
        }
    }
    if (onClick != null) {
        Card(onClick = onClick, modifier = modifier, colors = colors, shape = MaterialTheme.shapes.extraLarge) { body() }
    } else {
        Card(modifier = modifier, colors = colors, shape = MaterialTheme.shapes.extraLarge) { body() }
    }
}
