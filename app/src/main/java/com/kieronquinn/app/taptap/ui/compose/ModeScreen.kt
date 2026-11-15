package com.kieronquinn.app.taptap.ui.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.unit.dp

@Composable
fun ModeScreen(
    switchLabel: String,
    checked: Boolean,
    enabled: Boolean,
    onToggle: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        Modifier
            .fillMaxSize()
            .nestedScroll(rememberNestedScrollInteropConnection())
            .verticalScroll(rememberScrollState())
            .padding(containerBottomPadding())
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ModeSwitch(switchLabel, checked, enabled, onToggle, Modifier.padding(top = 8.dp))
        content()
    }
}

@Composable
fun ModeSwitch(label: String, checked: Boolean, enabled: Boolean, onToggle: () -> Unit, modifier: Modifier = Modifier) {
    ListItem(
        checked = checked,
        onCheckedChange = { onToggle() },
        enabled = enabled,
        modifier = modifier.clip(MaterialTheme.shapes.extraLargeIncreased),
        colors = ListItemDefaults.colors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
        trailingContent = { Switch(checked = checked, onCheckedChange = null, enabled = enabled) }
    ) { Text(label, style = MaterialTheme.typography.titleLarge) }
}
