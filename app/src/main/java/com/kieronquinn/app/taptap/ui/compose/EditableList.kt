package com.kieronquinn.app.taptap.ui.compose

import androidx.annotation.DrawableRes
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.spring
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.ui.unit.IntOffset
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.kieronquinn.app.taptap.R

@Stable
class ReorderState(
    private val listState: LazyListState,
    private val onMove: (Int, Int) -> Unit,
    private val onDrop: (Int, Int) -> Unit,
    private val canMoveTo: (Int) -> Boolean
) {
    var draggingIndex by mutableStateOf<Int?>(null)
        private set
    var offset by mutableFloatStateOf(0f)
        private set
    private var startIndex = -1

    fun start(index: Int) {
        draggingIndex = index
        startIndex = index
        offset = 0f
    }

    fun drag(dy: Float) {
        val index = draggingIndex ?: return
        offset += dy
        val visible = listState.layoutInfo.visibleItemsInfo
        val targetIndex = if (offset > 0) index + 1 else index - 1
        val target = visible.firstOrNull { it.index == targetIndex } ?: return
        if (kotlin.math.abs(offset) < target.size / 2f || !canMoveTo(targetIndex)) return
        onMove(index, targetIndex)
        offset += if (offset > 0) -target.size.toFloat() else target.size.toFloat()
        draggingIndex = targetIndex
    }

    fun end() {
        val index = draggingIndex
        if (index != null && startIndex != -1 && index != startIndex) onDrop(startIndex, index)
        draggingIndex = null
        startIndex = -1
        offset = 0f
    }
}

@Composable
fun rememberReorderState(
    listState: LazyListState,
    onMove: (Int, Int) -> Unit,
    onDrop: (Int, Int) -> Unit,
    canMoveTo: (Int) -> Boolean = { true }
): ReorderState {
    val move by rememberUpdatedState(onMove)
    val drop by rememberUpdatedState(onDrop)
    val can by rememberUpdatedState(canMoveTo)
    return remember(listState) { ReorderState(listState, { a, b -> move(a, b) }, { a, b -> drop(a, b) }, { can(it) }) }
}

fun Modifier.reorderHandle(state: ReorderState, index: () -> Int) = pointerInput(state) {
    detectDragGesturesAfterLongPress(
        onDragStart = { state.start(index()) },
        onDrag = { change, amount ->
            change.consume()
            state.drag(amount.y)
        },
        onDragEnd = state::end,
        onDragCancel = state::end
    )
}

@Composable
fun LazyItemScope.reorderable(state: ReorderState, index: Int) = Modifier
    .animateItem(placementSpec = if (state.draggingIndex == index) null else spring(visibilityThreshold = IntOffset.VisibilityThreshold))
    .zIndex(if (state.draggingIndex == index) 1f else 0f)
    .graphicsLayer {
        if (state.draggingIndex == index) {
            translationY = state.offset
            scaleX = 1.02f
            scaleY = 1.02f
        }
    }

@OptIn(ExperimentalFoundationApi::class, ExperimentalLayoutApi::class)
@Composable
fun EditableCard(
    @DrawableRes icon: Int,
    title: String,
    description: CharSequence?,
    selected: Boolean,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier,
    reorderHandle: Modifier? = null,
    chips: (@Composable () -> Unit)? = null,
    trailing: (@Composable () -> Unit)? = null
) {
    val colors = MaterialTheme.colorScheme
    val container by animateColorAsState(if (selected) colors.secondaryContainer else colors.surfaceBright)
    val badge by animateColorAsState(if (selected) colors.primary else colors.secondaryContainer)
    val badgeContent by animateColorAsState(if (selected) colors.onPrimary else colors.onSecondaryContainer)
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = container),
        border = if (selected) BorderStroke(2.dp, colors.primary) else null
    ) {
        Row(Modifier.height(IntrinsicSize.Min)) {
            Row(
                Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .combinedClickable(onClick = onLongClick, onLongClick = onLongClick)
                    .padding(start = 16.dp, top = 16.dp, bottom = 16.dp)
            ) {
                Box(Modifier.size(40.dp).clip(CircleShape).background(badge), contentAlignment = Alignment.Center) {
                    Crossfade(selected) {
                        Icon(
                            painterResource(if (it) R.drawable.ic_check else icon),
                            null,
                            Modifier.size(24.dp),
                            tint = badgeContent
                        )
                    }
                }
                Column(Modifier.weight(1f).padding(start = 16.dp, end = 4.dp)) {
                    Text(title, style = MaterialTheme.typography.titleMedium)
                    description?.takeIf { it.isNotEmpty() }?.let {
                        Text(
                            it.toAnnotatedString(),
                            style = MaterialTheme.typography.bodyMedium,
                            color = colors.onSurfaceVariant,
                            maxLines = if (selected) Int.MAX_VALUE else 3,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.animateContentSize()
                        )
                    }
                    chips?.let {
                        FlowRow(
                            Modifier.padding(top = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) { it() }
                    }
                }
            }
            Row(Modifier.align(Alignment.CenterVertically).padding(end = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                trailing?.invoke()
                reorderHandle?.let {
                    Icon(
                        painterResource(R.drawable.ic_drag_handle),
                        stringResource(R.string.reorder),
                        it.padding(horizontal = 8.dp, vertical = 12.dp).size(24.dp),
                        tint = colors.onSurfaceVariant
                    )
                }
            }
        }
    }
}
