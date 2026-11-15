package com.kieronquinn.app.taptap.ui.screens.settings.gates.selector

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
import com.kieronquinn.app.taptap.models.gate.GateRequirement
import com.kieronquinn.app.taptap.models.gate.GateSupportedRequirement
import com.kieronquinn.app.taptap.models.gate.TapTapGateDirectory
import com.kieronquinn.app.taptap.ui.compose.SearchField
import com.kieronquinn.app.taptap.ui.compose.SelectorItem
import com.kieronquinn.app.taptap.ui.compose.SelectorList
import com.kieronquinn.app.taptap.ui.screens.settings.gates.selector.gates.SettingsGatesGateSelectorViewModel

@Composable
fun SettingsGatesAddCategorySelectorScreen(
    viewModel: SettingsGatesAddCategorySelectorViewModel,
    isRequirement: Boolean,
    onGateClicked: (TapTapGateDirectory) -> Unit,
    onChipClicked: (GateRequirement.UserDisplayedGateRequirement) -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val s = state
    SelectorList(
        loading = s is SettingsGatesAddCategorySelectorViewModel.State.Loading,
        empty = (s as? SettingsGatesAddCategorySelectorViewModel.State.CategoryPicker)?.categories?.isEmpty() == true
            || (s as? SettingsGatesAddCategorySelectorViewModel.State.ItemPicker)?.items?.isEmpty() == true,
        emptyText = stringResource(R.string.settings_gates_add_category_selector_empty),
        header = { SearchField(viewModel.searchText.value.toString(), viewModel::setSearchText) }
    ) {
        when (s) {
            is SettingsGatesAddCategorySelectorViewModel.State.CategoryPicker -> itemsIndexed(s.categories) { index, category ->
                SelectorItem(
                    icon = category.icon,
                    title = stringResource(category.labelRes),
                    description = LocalContext.current.getText(category.descRes),
                    index = index,
                    count = s.categories.size,
                    onClick = { viewModel.onCategoryClicked(category, isRequirement) }
                )
            }
            is SettingsGatesAddCategorySelectorViewModel.State.ItemPicker -> itemsIndexed(s.items) { index, gate ->
                GateSelectorItem(gate, index, s.items.size, isRequirement, viewModel::getGateSupportedRequirement, onGateClicked, onChipClicked)
            }
            else -> Unit
        }
    }
}

@Composable
fun SettingsGatesGateSelectorScreen(
    viewModel: SettingsGatesGateSelectorViewModel,
    isRequirement: Boolean,
    onGateClicked: (TapTapGateDirectory) -> Unit,
    onChipClicked: (GateRequirement.UserDisplayedGateRequirement) -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val s = state
    SelectorList(
        loading = s is SettingsGatesGateSelectorViewModel.State.Loading,
        empty = (s as? SettingsGatesGateSelectorViewModel.State.Loaded)?.gates?.isEmpty() == true,
        emptyText = stringResource(R.string.settings_gates_add_category_selector_empty),
        header = { SearchField(viewModel.searchText.value.toString(), viewModel::setSearchText) }
    ) {
        if (s is SettingsGatesGateSelectorViewModel.State.Loaded) itemsIndexed(s.gates) { index, gate ->
            GateSelectorItem(gate, index, s.gates.size, isRequirement, viewModel::getGateSupportedRequirement, onGateClicked, onChipClicked)
        }
    }
}

@Composable
private fun GateSelectorItem(
    gate: TapTapGateDirectory,
    index: Int,
    count: Int,
    isRequirement: Boolean,
    getSupportedRequirement: (Context, TapTapGateDirectory) -> GateSupportedRequirement?,
    onGateClicked: (TapTapGateDirectory) -> Unit,
    onChipClicked: (GateRequirement.UserDisplayedGateRequirement) -> Unit
) {
    val context = LocalContext.current
    val unsupported = getSupportedRequirement(context, gate)
    val requirement = if (unsupported == null) {
        gate.gateRequirement?.firstOrNull { it is GateRequirement.UserDisplayedGateRequirement } as? GateRequirement.UserDisplayedGateRequirement
    } else null
    SelectorItem(
        icon = gate.iconRes,
        title = stringResource(gate.nameRes),
        description = when {
            unsupported != null -> stringResource(R.string.gate_selector_unavailable, stringResource(unsupported.description))
            isRequirement -> context.getText(gate.whenDescriptionRes)
            else -> context.getText(gate.descriptionRes)
        },
        index = index,
        count = count,
        enabled = unsupported == null,
        onClick = { onGateClicked(gate) },
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
