package com.kieronquinn.app.taptap.ui.screens.disablecolumbus

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.kieronquinn.app.taptap.R
import com.kieronquinn.app.taptap.ui.compose.DrawableImage
import com.kieronquinn.app.taptap.ui.compose.MessageScreen
import com.kieronquinn.app.taptap.ui.compose.toAnnotatedString

@Composable
fun DisableColumbusScreen(viewModel: DisableColumbusViewModel) {
    LaunchedEffect(viewModel) {
        viewModel.phoenixBus.collect { viewModel.phoenix() }
    }
    MessageScreen(
        title = stringResource(R.string.modal_disable_columbus_title),
        icon = { DrawableImage(R.mipmap.ic_launcher, 96.dp) }
    ) {
        Text(
            stringResource(R.string.modal_disable_columbus_content).toAnnotatedString(viewModel::onLinkClicked),
            textAlign = TextAlign.Center
        )
        Card(
            shape = MaterialTheme.shapes.extraLarge,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
        ) {
            Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(painterResource(R.drawable.ic_about), null)
                Text(stringResource(R.string.modal_disable_columbus_card), Modifier.padding(start = 16.dp))
            }
        }
        Button(onClick = viewModel::onOpenSettingsClicked) {
            Text(stringResource(R.string.modal_disable_columbus_button))
        }
    }
}
