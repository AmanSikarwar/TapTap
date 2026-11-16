package com.kieronquinn.app.taptap.ui.compose

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedListItem
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.kieronquinn.app.taptap.R
import kotlinx.coroutines.delay

@Composable
fun SearchField(initial: String, onSearch: (String) -> Unit, modifier: Modifier = Modifier) {
    var text by rememberSaveable { mutableStateOf(initial) }
    LaunchedEffect(text) {
        delay(250L)
        onSearch(text)
    }
    TextField(
        value = text,
        onValueChange = { text = it },
        modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        placeholder = { Text(stringResource(R.string.search)) },
        leadingIcon = { Icon(painterResource(R.drawable.ic_search), null) },
        trailingIcon = {
            if (text.isNotEmpty()) IconButton(onClick = { text = "" }) {
                Icon(painterResource(R.drawable.ic_search_clear), stringResource(R.string.close))
            }
        },
        singleLine = true,
        shape = CircleShape,
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        )
    )
}

@Composable
fun SelectorItem(
    icon: Int,
    title: String,
    description: CharSequence?,
    index: Int,
    count: Int,
    enabled: Boolean = true,
    onClick: () -> Unit,
    leading: (@Composable () -> Unit)? = null,
    chips: (@Composable () -> Unit)? = null
) {
    SegmentedListItem(
        onClick = onClick,
        enabled = enabled,
        shapes = ListItemDefaults.segmentedShapes(index, count),
        colors = segmentedItemColors(),
        modifier = Modifier.padding(horizontal = 16.dp, vertical = ListItemDefaults.SegmentedGap / 2),
        leadingContent = leading ?: { Icon(painterResource(icon), null) },
        supportingContent = {
            Column {
                description?.takeIf { it.isNotEmpty() }?.let { Text(it.toAnnotatedString()) }
                chips?.let { Box(Modifier.padding(top = 4.dp)) { it() } }
            }
        }
    ) { Text(title) }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SelectorList(
    loading: Boolean,
    empty: Boolean,
    emptyText: String,
    modifier: Modifier = Modifier,
    header: (@Composable () -> Unit)? = null,
    content: LazyListScope.() -> Unit
) {
    Column(modifier.fillMaxSize()) {
        header?.invoke()
        when {
            loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { LoadingIndicator() }
            empty -> Box(Modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
                Text(emptyText, style = MaterialTheme.typography.bodyLarge)
            }
            else -> LazyColumn(
                Modifier.fillMaxSize().nestedScroll(rememberNestedScrollInteropConnection()),
                contentPadding = PaddingValues(bottom = containerBottomPadding().calculateBottomPadding()),
                content = content
            )
        }
    }
}
