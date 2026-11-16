package com.kieronquinn.app.taptap.ui.screens.settings.shared.selector.quicksetting

import com.kieronquinn.app.taptap.components.navigation.NavigationResults
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kieronquinn.app.taptap.R
import com.kieronquinn.app.taptap.repositories.quicksettings.QuickSettingsRepository.QuickSetting
import com.kieronquinn.app.taptap.ui.compose.ComponentIcon
import com.kieronquinn.app.taptap.ui.compose.MessageCard
import com.kieronquinn.app.taptap.ui.compose.SelectorItem
import com.kieronquinn.app.taptap.ui.compose.SelectorList
import com.kieronquinn.app.taptap.ui.screens.settings.shared.selector.quicksetting.SettingsSharedQuickSettingSelectorViewModel.State

@Composable
fun SettingsSharedQuickSettingSelectorScreen(viewModel: SettingsSharedQuickSettingSelectorViewModel, onClicked: (QuickSetting) -> Unit) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val s = state
    SelectorList(
        loading = s is State.Loading,
        empty = (s as? State.Loaded)?.tiles?.isEmpty() == true,
        emptyText = stringResource(R.string.settings_shared_quick_settings_empty)
    ) {
        if (s is State.Loaded) {
            item {
                MessageCard(R.drawable.ic_about, stringResource(R.string.item_settings_shared_quick_settings_header_content), Modifier.padding(16.dp))
            }
            itemsIndexed(s.tiles) { index, tile ->
                SelectorItem(
                    icon = 0,
                    title = (tile.label ?: tile.packageLabel).toString(),
                    description = tile.component.shortClassName.substringAfterLast('.'),
                    index = index,
                    count = s.tiles.size,
                    onClick = { onClicked(tile) },
                    leading = { ComponentIcon(tile.component) }
                )
            }
        }
    }
}

@Composable
fun SettingsSharedQuickSettingSelectorRoute(viewModel: SettingsSharedQuickSettingSelectorViewModel = koinViewModel()) {
    val results = koinInject<NavigationResults>()
    SettingsSharedQuickSettingSelectorScreen(viewModel) {
        results.send(NavigationResults.Key.QUICK_SETTING, it)
        viewModel.onQuickSettingClicked()
    }
}
