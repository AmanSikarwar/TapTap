package com.kieronquinn.app.taptap.ui.compose

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.kieronquinn.app.taptap.R
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MessageCard(
    @DrawableRes icon: Int,
    text: CharSequence,
    modifier: Modifier = Modifier,
    title: String? = null,
    error: Boolean = false,
    containerColor: Color = if (error) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.secondaryContainer,
    onClick: (() -> Unit)? = null,
    linkClicked: ((String) -> Unit)? = null,
    onDismiss: (() -> Unit)? = null,
    tintIcon: Boolean = true,
    actions: (@Composable () -> Unit)? = null
) {
    val colors = CardDefaults.cardColors(containerColor = containerColor)
    val body = @Composable {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (tintIcon) Icon(painterResource(icon), null)
                else Image(painterResource(icon), null, Modifier.size(40.dp))
                Column(Modifier.weight(1f).padding(start = 16.dp)) {
                    title?.let { Text(it, style = MaterialTheme.typography.titleMedium) }
                    Text(text.toAnnotatedString(linkClicked), style = MaterialTheme.typography.bodyMedium)
                }
                onDismiss?.let {
                    IconButton(onClick = it) { Icon(painterResource(R.drawable.ic_dismiss), stringResource(R.string.close)) }
                }
            }
            actions?.let {
                FlowRow(
                    Modifier.fillMaxWidth().padding(top = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
                ) { it() }
            }
        }
    }
    val cardModifier = modifier.fillMaxWidth()
    if (onClick != null) {
        Card(onClick = onClick, modifier = cardModifier, colors = colors, shape = MaterialTheme.shapes.extraLarge) { body() }
    } else {
        Card(modifier = cardModifier, colors = colors, shape = MaterialTheme.shapes.extraLarge) { body() }
    }
}
