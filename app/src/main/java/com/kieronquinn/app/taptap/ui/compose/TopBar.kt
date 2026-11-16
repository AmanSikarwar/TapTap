package com.kieronquinn.app.taptap.ui.compose

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.res.painterResource
import com.kieronquinn.app.taptap.R

class TopBarActionsHolder {
    val actions: MutableState<Pair<Any, @Composable RowScope.() -> Unit>?> = mutableStateOf(null)
}

val LocalTopBarActions = staticCompositionLocalOf { TopBarActionsHolder() }

@Composable
fun TopBarActions(content: @Composable RowScope.() -> Unit) {
    val holder = LocalTopBarActions.current
    val latest by rememberUpdatedState(content)
    val token = remember { Any() }
    DisposableEffect(holder) {
        holder.actions.value = token to { latest() }
        onDispose {
            if (holder.actions.value?.first === token) holder.actions.value = null
        }
    }
}

@Composable
fun OverflowMenu(items: List<Pair<String, () -> Unit>>) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        IconButton(onClick = { expanded = true }) { Icon(painterResource(R.drawable.ic_more), null) }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            items.forEach { (label, onClick) ->
                DropdownMenuItem(text = { Text(label) }, onClick = {
                    expanded = false
                    onClick()
                })
            }
        }
    }
}
