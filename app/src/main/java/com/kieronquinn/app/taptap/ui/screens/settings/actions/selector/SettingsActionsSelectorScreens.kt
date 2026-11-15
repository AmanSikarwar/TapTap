package com.kieronquinn.app.taptap.ui.screens.settings.actions.selector

import android.content.Context
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kieronquinn.app.taptap.R
import com.kieronquinn.app.taptap.models.action.ActionRequirement
import com.kieronquinn.app.taptap.models.action.ActionSupportedRequirement
import com.kieronquinn.app.taptap.models.action.TapTapActionDirectory
import com.kieronquinn.app.taptap.ui.compose.SearchField
import com.kieronquinn.app.taptap.ui.compose.SelectorItem
import com.kieronquinn.app.taptap.ui.compose.SelectorList
import com.kieronquinn.app.taptap.ui.screens.settings.actions.selector.actions.SettingsActionsActionSelectorViewModel

@Composable
fun SettingsActionsAddCategorySelectorScreen(
    viewModel: SettingsActionsAddCategorySelectorViewModel,
    onActionClicked: (TapTapActionDirectory) -> Unit,
    onChipClicked: (ActionRequirement.UserDisplayedActionRequirement) -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val s = state
    SelectorList(
        loading = s is SettingsActionsAddCategorySelectorViewModel.State.Loading,
        empty = (s as? SettingsActionsAddCategorySelectorViewModel.State.CategoryPicker)?.categories?.isEmpty() == true
            || (s as? SettingsActionsAddCategorySelectorViewModel.State.ItemPicker)?.items?.isEmpty() == true,
        emptyText = stringResource(R.string.settings_actions_add_category_selector_empty),
        header = { SearchField(viewModel.searchText.value.toString(), viewModel::setSearchText) }
    ) {
        when (s) {
            is SettingsActionsAddCategorySelectorViewModel.State.CategoryPicker -> itemsIndexed(s.categories) { index, category ->
                SelectorItem(
                    icon = category.icon,
                    title = stringResource(category.labelRes),
                    description = LocalContext.current.getText(category.descRes),
                    index = index,
                    count = s.categories.size,
                    onClick = { viewModel.onCategoryClicked(category) }
                )
            }
            is SettingsActionsAddCategorySelectorViewModel.State.ItemPicker -> itemsIndexed(s.items) { index, action ->
                ActionSelectorItem(action, index, s.items.size, viewModel::getActionSupportedRequirement, onActionClicked, onChipClicked)
            }
            else -> Unit
        }
    }
}

@Composable
fun SettingsActionsActionSelectorScreen(
    viewModel: SettingsActionsActionSelectorViewModel,
    onActionClicked: (TapTapActionDirectory) -> Unit,
    onChipClicked: (ActionRequirement.UserDisplayedActionRequirement) -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val s = state
    SelectorList(
        loading = s is SettingsActionsActionSelectorViewModel.State.Loading,
        empty = (s as? SettingsActionsActionSelectorViewModel.State.Loaded)?.actions?.isEmpty() == true,
        emptyText = stringResource(R.string.settings_actions_add_category_selector_empty),
        header = { SearchField(viewModel.searchText.value.toString(), viewModel::setSearchText) }
    ) {
        if (s is SettingsActionsActionSelectorViewModel.State.Loaded) itemsIndexed(s.actions) { index, action ->
            ActionSelectorItem(action, index, s.actions.size, viewModel::getActionSupportedRequirement, onActionClicked, onChipClicked)
        }
    }
}

@Composable
private fun ActionSelectorItem(
    action: TapTapActionDirectory,
    index: Int,
    count: Int,
    getSupportedRequirement: (Context, TapTapActionDirectory) -> ActionSupportedRequirement?,
    onActionClicked: (TapTapActionDirectory) -> Unit,
    onChipClicked: (ActionRequirement.UserDisplayedActionRequirement) -> Unit
) {
    val context = LocalContext.current
    val unsupported = getSupportedRequirement(context, action)
    val requirement = if (unsupported == null) {
        action.actionRequirement?.firstOrNull { it is ActionRequirement.UserDisplayedActionRequirement } as? ActionRequirement.UserDisplayedActionRequirement
    } else null
    SelectorItem(
        icon = action.iconRes,
        title = stringResource(action.nameRes),
        description = if (unsupported == null) context.getText(action.descriptionRes)
            else stringResource(R.string.action_selector_unavailable, stringResource(unsupported.description)),
        index = index,
        count = count,
        enabled = unsupported == null,
        onClick = { onActionClicked(action) },
        chips = requirement?.let {
            {
                AssistChip(
                    onClick = { onChipClicked(it) },
                    label = { Text(stringResource(it.label)) },
                    leadingIcon = { Icon(painterResource(it.icon), null, Modifier.size(18.dp)) }
                )
            }
        }
    )
}
