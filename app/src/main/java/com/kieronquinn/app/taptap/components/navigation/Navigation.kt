package com.kieronquinn.app.taptap.components.navigation

import android.content.Intent
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.kieronquinn.app.taptap.ui.navigation.ContainerKey
import com.kieronquinn.app.taptap.ui.navigation.GestureConfigurationKey
import com.kieronquinn.app.taptap.ui.navigation.RootKey
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.withContext
import kotlin.reflect.KClass

interface BaseNavigation {

    val backStack: SnapshotStateList<Any>
    val navigationBus: Flow<NavigationEvent>

    suspend fun navigate(key: Any)
    suspend fun navigate(intent: Intent)
    suspend fun replace(key: Any)
    suspend fun navigateUpTo(key: KClass<*>, popInclusive: Boolean = false)
    suspend fun navigateBack()
    suspend fun restartActivity()
    suspend fun phoenix()
    fun reset()

}

sealed class NavigationEvent {
    data class Intent(val intent: android.content.Intent, val onActivityNotFound: (() -> Unit)? = null): NavigationEvent()
    object Finish: NavigationEvent()
    object RestartActivity: NavigationEvent()
    object Phoenix: NavigationEvent()
}

open class NavigationImpl(private val start: Any): BaseNavigation {

    override val backStack = mutableStateListOf(start)

    private val _navigationBus = Channel<NavigationEvent>(Channel.BUFFERED)
    override val navigationBus = _navigationBus.receiveAsFlow()

    private suspend fun edit(block: SnapshotStateList<Any>.() -> Unit) = withContext(Dispatchers.Main.immediate) {
        backStack.block()
    }

    override suspend fun navigate(key: Any) = edit {
        if (lastOrNull() != key) add(key)
    }

    override suspend fun navigate(intent: Intent) {
        _navigationBus.send(NavigationEvent.Intent(intent))
    }

    override suspend fun replace(key: Any) = edit {
        clear()
        add(key)
    }

    override suspend fun navigateUpTo(key: KClass<*>, popInclusive: Boolean) = edit {
        val index = indexOfLast { key.isInstance(it) }
        if (index == -1) return@edit
        val keep = if (popInclusive) index else index + 1
        while (size > keep.coerceAtLeast(1)) removeAt(lastIndex)
    }

    override suspend fun navigateBack() {
        val popped = withContext(Dispatchers.Main.immediate) {
            if (backStack.size > 1) {
                backStack.removeAt(backStack.lastIndex)
                true
            } else false
        }
        if (!popped) _navigationBus.send(NavigationEvent.Finish)
    }

    override suspend fun restartActivity() {
        _navigationBus.send(NavigationEvent.RestartActivity)
    }

    override suspend fun phoenix() {
        _navigationBus.send(NavigationEvent.Phoenix)
    }

    override fun reset() {
        backStack.clear()
        backStack.add(start)
    }

}

//Root level navigation for full screen changes
interface RootNavigation: BaseNavigation
class RootNavigationImpl: NavigationImpl(RootKey.Decision), RootNavigation

//Container level navigation for inner changes
interface ContainerNavigation: BaseNavigation
class ContainerNavigationImpl: NavigationImpl(ContainerKey.Main), ContainerNavigation

//Gesture Configuration bottom sheet navigation
interface GestureConfigurationNavigation: BaseNavigation
class GestureConfigurationNavigationImpl: NavigationImpl(GestureConfigurationKey.Configuration), GestureConfigurationNavigation
