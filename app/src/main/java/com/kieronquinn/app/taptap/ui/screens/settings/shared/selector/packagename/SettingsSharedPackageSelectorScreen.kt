package com.kieronquinn.app.taptap.ui.screens.settings.shared.selector.packagename

import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kieronquinn.app.taptap.R
import com.kieronquinn.app.taptap.ui.compose.AppIcon
import com.kieronquinn.app.taptap.ui.compose.SearchField
import com.kieronquinn.app.taptap.ui.compose.SelectorItem
import com.kieronquinn.app.taptap.ui.compose.SelectorList
import com.kieronquinn.app.taptap.ui.screens.settings.shared.selector.packagename.SettingsSharedPackageSelectorViewModel.State

@Composable
fun SettingsSharedPackageSelectorScreen(viewModel: SettingsSharedPackageSelectorViewModel, onAppClicked: (String) -> Unit) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val s = state
    SelectorList(
        loading = s is State.Loading,
        empty = (s as? State.Loaded)?.items?.isEmpty() == true,
        emptyText = stringResource(R.string.settings_shared_package_selector_empty),
        header = { SearchField(viewModel.searchText.value, viewModel::setSearchText) }
    ) {
        if (s is State.Loaded) itemsIndexed(s.items, key = { _, it -> it.packageName }) { index, app ->
            SelectorItem(
                icon = 0,
                title = app.name.toString(),
                description = app.packageName,
                index = index,
                count = s.items.size,
                onClick = { onAppClicked(app.packageName) },
                leading = { AppIcon(app.packageName) }
            )
        }
    }
}
