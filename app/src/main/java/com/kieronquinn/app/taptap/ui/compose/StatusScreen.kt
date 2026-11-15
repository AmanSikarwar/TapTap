package com.kieronquinn.app.taptap.ui.compose

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearWavyProgressIndicator
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

sealed interface StatusIndicator {
    data object Indeterminate: StatusIndicator
    data class Progress(val progress: Float): StatusIndicator
    data class Icon(@DrawableRes val icon: Int): StatusIndicator
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun StatusScreen(
    indicator: StatusIndicator,
    title: String,
    description: String? = null,
    button: String? = null,
    onButton: () -> Unit = {}
) {
    Column(
        Modifier.fillMaxSize().padding(containerBottomPadding()).padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
    ) {
        when (indicator) {
            StatusIndicator.Indeterminate -> LoadingIndicator(Modifier.size(64.dp))
            is StatusIndicator.Progress -> LinearWavyProgressIndicator(progress = { indicator.progress })
            is StatusIndicator.Icon -> Icon(
                painterResource(indicator.icon), null, Modifier.size(64.dp), tint = MaterialTheme.colorScheme.primary
            )
        }
        Text(title, style = MaterialTheme.typography.headlineSmall, textAlign = TextAlign.Center)
        description?.let { Text(it, textAlign = TextAlign.Center) }
        button?.let { Button(onClick = onButton) { Text(it) } }
    }
}
