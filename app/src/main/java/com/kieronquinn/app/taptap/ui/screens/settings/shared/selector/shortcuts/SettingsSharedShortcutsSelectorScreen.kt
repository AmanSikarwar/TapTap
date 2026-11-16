package com.kieronquinn.app.taptap.ui.screens.settings.shared.selector.shortcuts

import com.kieronquinn.app.taptap.components.navigation.NavigationResults
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.IntentCompat
import com.kieronquinn.app.taptap.utils.extensions.serialize
import android.content.ComponentName
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kieronquinn.app.taptap.R
import com.kieronquinn.app.taptap.ui.compose.ComponentIcon
import com.kieronquinn.app.taptap.ui.compose.SelectorList
import com.kieronquinn.app.taptap.ui.compose.expandableGroups
import com.kieronquinn.app.taptap.ui.screens.settings.shared.selector.shortcuts.SettingsSharedShortcutsSelectorViewModel.Item
import com.kieronquinn.app.taptap.ui.screens.settings.shared.selector.shortcuts.SettingsSharedShortcutsSelectorViewModel.State

@Composable
fun SettingsSharedShortcutsSelectorScreen(viewModel: SettingsSharedShortcutsSelectorViewModel, onShortcutClicked: (ComponentName) -> Unit) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val expanded = remember { mutableStateListOf<String>() }
    val s = state
    val groups = (s as? State.Loaded)?.items.orEmpty().let { items ->
        items.filterIsInstance<Item.App>().map { app ->
            (app.packageName to app.name) to items.filterIsInstance<Item.Shortcut>().filter { it.packageName == app.packageName }
        }
    }
    SelectorList(
        loading = s !is State.Loaded,
        empty = false,
        emptyText = stringResource(R.string.settings_shared_shortcuts_selector_loading)
    ) {
        expandableGroups(groups, expanded, { "${it.packageName}/${it.activity}" }) { shortcut ->
            val component = ComponentName(shortcut.packageName, shortcut.activity)
            ListItem(
                onClick = { onShortcutClicked(component) },
                leadingContent = { ComponentIcon(component) }
            ) { Text(shortcut.name.toString()) }
        }
    }
}

@Composable
fun SettingsSharedShortcutsSelectorRoute(viewModel: SettingsSharedShortcutsSelectorViewModel = koinViewModel()) {
    val context = LocalContext.current
    val results = koinInject<NavigationResults>()
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val serialized = result.data?.let { IntentCompat.getParcelableExtra(it, Intent.EXTRA_SHORTCUT_INTENT, Intent::class.java) }?.serialize()
        if (serialized == null) {
            Toast.makeText(context, R.string.action_launch_shortcut_toast, Toast.LENGTH_LONG).show()
        } else {
            results.send(NavigationResults.Key.SHORTCUT, serialized)
            viewModel.unwind()
        }
    }
    SettingsSharedShortcutsSelectorScreen(viewModel) { component ->
        launcher.launch(Intent().apply {
            `package` = component.packageName
            this.component = component
        })
    }
}
