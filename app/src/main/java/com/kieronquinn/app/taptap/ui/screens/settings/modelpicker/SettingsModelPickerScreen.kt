package com.kieronquinn.app.taptap.ui.screens.settings.modelpicker

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.RadioButton
import androidx.compose.material3.SegmentedListItem
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kieronquinn.app.taptap.R
import com.kieronquinn.app.taptap.models.phonespecs.DeviceSpecs
import com.kieronquinn.app.taptap.ui.compose.MessageCard
import com.kieronquinn.app.taptap.ui.compose.containerBottomPadding
import com.kieronquinn.app.taptap.ui.compose.rememberUrlImage
import com.kieronquinn.app.taptap.ui.screens.settings.modelpicker.SettingsModelPickerViewModel.Item
import com.kieronquinn.app.taptap.ui.screens.settings.modelpicker.SettingsModelPickerViewModel.State

private val tabs = listOf(
    R.string.settings_model_picker_tab_newest,
    R.string.settings_model_picker_tab_legacy,
    R.string.settings_model_picker_tab_oem
)

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SettingsModelPickerScreen(viewModel: SettingsModelPickerViewModel, onRestartService: () -> Unit) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(viewModel) {
        viewModel.restartService?.collect { onRestartService() }
    }
    when (val s = state) {
        is State.Loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                LoadingIndicator()
                Text(stringResource(R.string.settings_model_picker_loading))
            }
        }
        is State.Loaded -> Column(Modifier.fillMaxSize()) {
            s.deviceSpecs?.let { DeviceCard(it) }
            PrimaryTabRow(selectedTabIndex = s.selectedTab ?: 0) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = index == (s.selectedTab ?: 0),
                        onClick = { viewModel.onTabSelected(index) },
                        text = { Text(stringResource(title)) }
                    )
                }
            }
            val models = s.items.filterIsInstance<Item.Model>()
            LazyColumn(
                Modifier.nestedScroll(rememberNestedScrollInteropConnection()),
                contentPadding = containerBottomPadding()
            ) {
                items(s.items, key = { if (it is Item.Model) it.model.ordinal else -1 }) { item ->
                    when (item) {
                        is Item.Header -> MessageCard(
                            R.drawable.ic_about,
                            LocalContext.current.getText(item.contentRes),
                            Modifier.padding(16.dp)
                        )
                        is Item.Model -> {
                            val index = models.indexOf(item)
                            val description = stringResource(item.model.descriptionRes)
                            SegmentedListItem(
                                onClick = { viewModel.onModelSelected(item.model) },
                                shapes = ListItemDefaults.segmentedShapes(index, models.size),
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = ListItemDefaults.SegmentedGap / 2),
                                supportingContent = {
                                    Text(
                                        if (item.best) stringResource(R.string.settings_model_picker_recommended, description)
                                        else description
                                    )
                                },
                                trailingContent = { RadioButton(selected = item.selected, onClick = null) }
                            ) { Text(stringResource(item.model.nameRes)) }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DeviceCard(specs: DeviceSpecs) {
    val image = rememberUrlImage(specs.deviceImageUrl)
    Card(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            image?.let { Image(it, null, Modifier.size(72.dp).padding(end = 16.dp)) }
            Column {
                Text(specs.deviceName, style = MaterialTheme.typography.titleMedium)
                Text(stringResource(R.string.settings_model_picker_device_size, specs.heightMm.toString(), specs.heightIn.toString()))
            }
        }
    }
}
