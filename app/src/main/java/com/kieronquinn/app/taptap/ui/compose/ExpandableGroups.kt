package com.kieronquinn.app.taptap.ui.compose

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.kieronquinn.app.taptap.R

fun <C> LazyListScope.expandableGroups(
    groups: List<Pair<Pair<String, CharSequence>, List<C>>>,
    expanded: SnapshotStateList<String>,
    childKey: (C) -> Any,
    child: @Composable (C) -> Unit
) {
    groups.forEachIndexed { index, (app, children) ->
        val (packageName, name) = app
        val isOpen = packageName in expanded
        item(key = packageName) {
            val rotation by animateFloatAsState(if (isOpen) 180f else 0f, label = "chevron")
            SegmentedListItem(
                onClick = { if (isOpen) expanded.remove(packageName) else expanded.add(packageName) },
                shapes = ListItemDefaults.segmentedShapes(index, groups.size),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = ListItemDefaults.SegmentedGap / 2).animateItem(),
                leadingContent = { AppIcon(packageName) },
                trailingContent = { Icon(painterResource(R.drawable.ic_chevron_expand), null, Modifier.rotate(rotation)) }
            ) { Text(name.toString(), style = MaterialTheme.typography.titleMedium) }
        }
        if (isOpen) children.forEach { c ->
            item(key = childKey(c)) {
                Box(Modifier.padding(start = 24.dp).animateItem()) { child(c) }
            }
        }
    }
}
