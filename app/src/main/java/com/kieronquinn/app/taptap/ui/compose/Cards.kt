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
import androidx.compose.ui.text.style.TextOverflow

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
            Row {
                if (tintIcon) Icon(painterResource(icon), null, Modifier.padding(top = 2.dp))
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

@Composable
fun HelpBanner(text: CharSequence, modifier: Modifier = Modifier, onClick: (() -> Unit)? = null, onDismiss: (() -> Unit)? = null) {
    val body = text.toString().substringBeforeLast('\n').takeIf { onClick != null } ?: text
    val hint = text.toString().substringAfterLast('\n', "").takeIf { onClick != null && it.isNotBlank() }
    Card(
        onClick = { onClick?.invoke() },
        enabled = onClick != null,
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            disabledContentColor = MaterialTheme.colorScheme.onSecondaryContainer
        )
    ) {
        Row(Modifier.padding(start = 16.dp, top = 12.dp, bottom = 12.dp, end = 4.dp)) {
            Icon(painterResource(R.drawable.ic_about), null, Modifier.padding(top = 2.dp).size(20.dp))
            Column(Modifier.weight(1f).padding(horizontal = 12.dp)) {
                Text(
                    body.toAnnotatedString(),
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = if (onClick != null) 2 else Int.MAX_VALUE,
                    overflow = TextOverflow.Ellipsis
                )
                hint?.let {
                    Text(it, Modifier.padding(top = 4.dp), style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                }
            }
            onDismiss?.let {
                IconButton(onClick = it, Modifier.size(32.dp)) {
                    Icon(painterResource(R.drawable.ic_dismiss), stringResource(R.string.close), Modifier.size(18.dp))
                }
            }
        }
    }
}
