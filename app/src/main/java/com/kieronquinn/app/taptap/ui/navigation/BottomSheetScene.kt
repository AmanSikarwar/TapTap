package com.kieronquinn.app.taptap.ui.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.scene.OverlayScene
import androidx.navigation3.scene.Scene
import androidx.navigation3.scene.SceneStrategy
import androidx.navigation3.scene.SceneStrategyScope

private const val BOTTOM_SHEET = "bottom_sheet"

private class BottomSheetScene<T : Any>(
    override val key: Any,
    override val previousEntries: List<NavEntry<T>>,
    override val overlaidEntries: List<NavEntry<T>>,
    private val entry: NavEntry<T>,
    private val onBack: () -> Unit
) : OverlayScene<T> {

    override val entries = listOf(entry)

    @OptIn(ExperimentalMaterial3Api::class)
    override val content: @Composable () -> Unit = {
        ModalBottomSheet(onDismissRequest = onBack) {
            Column(Modifier.navigationBarsPadding()) { entry.Content() }
        }
    }

    override fun equals(other: Any?) = other is BottomSheetScene<*> && key == other.key &&
        entry == other.entry && previousEntries == other.previousEntries

    override fun hashCode() = (key.hashCode() * 31 + entry.hashCode()) * 31 + previousEntries.hashCode()

}

class BottomSheetSceneStrategy<T : Any> : SceneStrategy<T> {

    override fun SceneStrategyScope<T>.calculateScene(entries: List<NavEntry<T>>): Scene<T>? {
        val last = entries.lastOrNull()?.takeIf { it.metadata.containsKey(BOTTOM_SHEET) } ?: return null
        return BottomSheetScene(last.contentKey, entries.dropLast(1), entries.dropLast(1), last, onBack)
    }

    companion object {
        fun bottomSheet(): Map<String, Any> = mapOf(BOTTOM_SHEET to true)
    }

}
