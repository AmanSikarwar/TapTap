package com.kieronquinn.app.taptap.ui.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedListItem
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.kieronquinn.app.taptap.BuildConfig
import com.kieronquinn.app.taptap.R
import com.kieronquinn.app.taptap.components.settings.invert
import com.kieronquinn.app.taptap.ui.screens.settings.generic.GenericSettingsViewModel.SettingsItem
import kotlinx.coroutines.launch

@Composable
fun SettingsList(
    items: List<SettingsItem>,
    modifier: Modifier = Modifier,
    refreshKey: Any? = null,
    contentPadding: PaddingValues = PaddingValues(),
    header: (LazyListScope.() -> Unit)? = null
) {
    val visible = remember(items, refreshKey) { items.filter { it.isVisible() } }
    LazyColumn(
        modifier = modifier.nestedScroll(rememberNestedScrollInteropConnection()),
        contentPadding = contentPadding
    ) {
        header?.invoke(this)
        settingsItems(visible)
    }
}

fun LazyListScope.settingsItems(items: List<SettingsItem>) {
    val groups = mutableListOf<MutableList<SettingsItem>>()
    items.forEach { item ->
        if (item.isStandalone() || groups.lastOrNull()?.last()?.isStandalone() != false) {
            groups.add(mutableListOf(item))
        } else {
            groups.last().add(item)
        }
    }
    groups.forEach { group ->
        group.forEachIndexed { index, item ->
            item(key = System.identityHashCode(item)) {
                val segmentModifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(top = if (index == 0) 8.dp else ListItemDefaults.SegmentedGap)
                when (item) {
                    is SettingsItem.Header -> SettingsHeader(item)
                    is SettingsItem.Info -> SettingsInfo(item, Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
                    is SettingsItem.About -> SettingsAbout(item, Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
                    else -> SettingsRow(item, index, group.size, segmentModifier)
                }
            }
        }
    }
}

@Composable
private fun SettingsRow(item: SettingsItem, index: Int, count: Int, modifier: Modifier) {
    val scope = rememberCoroutineScope()
    val enabled = item.isEnabled()
    val shapes = ListItemDefaults.segmentedShapes(index, count)
    when (item) {
        is SettingsItem.Text -> {
            val title = resolveText(item.titleRes, item.title)
            val content = resolveText(item.contentRes, item.content?.invoke())
            val leading = @Composable { Icon(painterResource(item.icon), null) }
            val supporting = content?.takeIf { it.isNotEmpty() }?.let {
                @Composable { Text(it.toAnnotatedString(item.linkClicked)) }
            }
            if (item.onClick != null) {
                SegmentedListItem(
                    onClick = item.onClick, shapes = shapes, modifier = modifier, enabled = enabled,
                    leadingContent = leading, supportingContent = supporting
                ) { Text(title?.toString().orEmpty()) }
            } else {
                SegmentedListItem(
                    shapes = shapes, modifier = modifier, enabled = enabled,
                    leadingContent = leading, supportingContent = supporting
                ) { Text(title?.toString().orEmpty()) }
            }
        }
        is SettingsItem.Switch -> {
            val checked by item.setting.asFlow().collectAsState(item.setting.getSync())
            val toggle: () -> Unit = { scope.launch { item.setting.invert() } }
            SegmentedListItem(
                onClick = toggle, shapes = shapes, modifier = modifier, enabled = enabled,
                leadingContent = { Icon(painterResource(item.icon), null) },
                supportingContent = resolveText(item.contentRes, item.content?.invoke())
                    ?.takeIf { it.isNotEmpty() }?.let { { Text(it.toAnnotatedString()) } },
                trailingContent = { Switch(checked = checked, onCheckedChange = { toggle() }, enabled = enabled) }
            ) { Text(resolveText(item.titleRes, item.title)?.toString().orEmpty()) }
        }
        is SettingsItem.Slider -> {
            val stored by item.setting.asFlow().collectAsState(item.setting.getSync())
            var dragging by remember { mutableFloatStateOf(Float.NaN) }
            val value = if (dragging.isNaN()) stored.toFloat() else dragging
            val steps = item.stepSize?.let { ((item.maximumValue - item.minimumValue) / it).toInt() - 1 } ?: 0
            SegmentedListItem(
                shapes = shapes, modifier = modifier, enabled = enabled,
                leadingContent = { Icon(painterResource(item.icon), null) },
                supportingContent = {
                    Column {
                        resolveText(item.contentRes, item.content?.invoke())?.takeIf { it.isNotEmpty() }?.let {
                            Text(it.toAnnotatedString())
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Slider(
                                value = value,
                                onValueChange = {
                                    dragging = it
                                    scope.launch { item.setting.set(it) }
                                },
                                onValueChangeFinished = { dragging = Float.NaN },
                                valueRange = item.minimumValue..item.maximumValue,
                                steps = steps.coerceAtLeast(0),
                                enabled = enabled,
                                modifier = Modifier.weight(1f)
                            )
                            item.labelFormatter?.let {
                                Text(it(value), Modifier.padding(start = 12.dp), style = MaterialTheme.typography.labelLarge)
                            }
                        }
                    }
                }
            ) { Text(resolveText(item.titleRes, item.title)?.toString().orEmpty()) }
        }
        else -> Unit
    }
}

@Composable
private fun SettingsHeader(item: SettingsItem.Header) {
    Text(
        stringResource(item.titleRes),
        modifier = Modifier.padding(start = 32.dp, end = 32.dp, top = 24.dp, bottom = 4.dp),
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.primary
    )
}

@Composable
fun SettingsInfo(item: SettingsItem.Info, modifier: Modifier = Modifier) {
    val content = resolveText(item.contentRes, item.content?.invoke())
    val colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
    val body = @Composable {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(painterResource(item.icon ?: R.drawable.ic_about), null)
            Text(
                (content ?: "").toAnnotatedString(item.linkClicked),
                modifier = Modifier.weight(1f).padding(horizontal = 16.dp),
                style = MaterialTheme.typography.bodyMedium
            )
            item.onDismissClicked?.let {
                IconButton(onClick = it) { Icon(painterResource(R.drawable.ic_dismiss), stringResource(android.R.string.cancel)) }
            }
        }
    }
    if (item.onClick != null) {
        Card(onClick = item.onClick, modifier = modifier.fillMaxWidth(), colors = colors, shape = MaterialTheme.shapes.extraLarge) { body() }
    } else {
        Card(modifier = modifier.fillMaxWidth(), colors = colors, shape = MaterialTheme.shapes.extraLarge) { body() }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SettingsAbout(item: SettingsItem.About, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                DrawableImage(R.mipmap.ic_launcher, Modifier.size(48.dp))
                Column(Modifier.padding(start = 16.dp)) {
                    Text(stringResource(R.string.app_name), style = MaterialTheme.typography.titleLarge)
                    Text(stringResource(R.string.about_version, BuildConfig.VERSION_NAME), style = MaterialTheme.typography.bodyMedium)
                }
            }
            Spacer(Modifier.height(12.dp))
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(
                    Triple(R.string.about_contributors, R.drawable.ic_community, item.onContributorsClicked),
                    Triple(R.string.donate, R.drawable.ic_donate, item.onDonateClicked),
                    Triple(R.string.github, R.drawable.ic_github, item.onGitHubClicked),
                    Triple(R.string.twitter, R.drawable.ic_twitter, item.onTwitterClicked),
                    Triple(R.string.xda_thread, R.drawable.ic_xda, item.onXdaClicked),
                    Triple(R.string.libraries, R.drawable.ic_libraries, item.onLibrariesClicked)
                ).forEach { (label, icon, onClick) ->
                    AssistChip(
                        onClick = onClick,
                        label = { Text(stringResource(label)) },
                        leadingIcon = { Icon(painterResource(icon), null, Modifier.size(18.dp)) }
                    )
                }
            }
        }
    }
}

private fun SettingsItem.isStandalone() =
    this is SettingsItem.Header || this is SettingsItem.Info || this is SettingsItem.About
