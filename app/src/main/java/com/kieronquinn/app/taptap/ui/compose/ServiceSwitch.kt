package com.kieronquinn.app.taptap.ui.compose

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.kieronquinn.app.taptap.R

@Composable
fun ServiceSwitchCard(checked: Boolean, onToggle: () -> Unit, modifier: Modifier = Modifier) {
    ListItem(
        checked = checked,
        onCheckedChange = { onToggle() },
        modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp).clip(MaterialTheme.shapes.extraLargeIncreased),
        colors = ListItemDefaults.colors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
        trailingContent = { Switch(checked = checked, onCheckedChange = null) }
    ) {
        Text(stringResource(R.string.settings_main_enable_tap_tap), style = MaterialTheme.typography.titleLarge)
    }
}
