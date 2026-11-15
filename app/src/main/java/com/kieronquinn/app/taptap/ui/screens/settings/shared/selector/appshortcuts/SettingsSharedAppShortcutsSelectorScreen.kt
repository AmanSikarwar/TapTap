package com.kieronquinn.app.taptap.ui.screens.settings.shared.selector.appshortcuts

import android.graphics.drawable.BitmapDrawable
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kieronquinn.app.taptap.R
import com.kieronquinn.app.taptap.models.columbus.AppShortcutData
import com.kieronquinn.app.taptap.ui.compose.AsyncIcon
import com.kieronquinn.app.taptap.ui.compose.SelectorList
import com.kieronquinn.app.taptap.ui.compose.expandableGroups
import com.kieronquinn.app.taptap.ui.screens.settings.shared.selector.appshortcuts.SettingsSharedAppShortcutsSelectorViewModel.Item
import com.kieronquinn.app.taptap.ui.screens.settings.shared.selector.appshortcuts.SettingsSharedAppShortcutsSelectorViewModel.State

@Composable
fun SettingsSharedAppShortcutsSelectorScreen(viewModel: SettingsSharedAppShortcutsSelectorViewModel, onClicked: (AppShortcutData) -> Unit) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val expanded = remember { mutableStateListOf<String>() }
    val s = state
    val groups = (s as? State.Loaded)?.items.orEmpty().let { items ->
        items.filterIsInstance<Item.App>().map { app ->
            (app.packageName to app.name) to items.filterIsInstance<Item.AppShortcut>().filter { it.packageName == app.packageName }
        }
    }
    SelectorList(
        loading = s !is State.Loaded && s !is State.Error,
        empty = s is State.Error,
        emptyText = (s as? State.Error)?.reason?.let { stringResource(it.contentRes) }.orEmpty()
    ) {
        expandableGroups(groups, expanded, { "${it.packageName}/${it.shortcutId}" }) { shortcut ->
            ListItem(
                onClick = { onClicked(AppShortcutData(shortcut.packageName, shortcut.shortcutId, shortcut.name.toString())) },
                leadingContent = {
                    AsyncIcon(shortcut.icon) {
                        shortcut.icon.cacheIcon?.let { BitmapDrawable(resources, it.absolutePath) }
                            ?: shortcut.icon.icon?.loadDrawable(this)
                    }
                }
            ) { Text(shortcut.name.toString()) }
        }
    }
}
