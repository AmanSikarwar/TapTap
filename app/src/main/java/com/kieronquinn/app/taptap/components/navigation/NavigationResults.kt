package com.kieronquinn.app.taptap.components.navigation

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import java.util.concurrent.ConcurrentHashMap

class NavigationResults {

    enum class Key {
        ACTION, WHEN_GATES_SIZE, GATE, PACKAGE, SHORTCUT, APP_SHORTCUT, QUICK_SETTING, SHIZUKU, SNAPCHAT
    }

    private val channels = ConcurrentHashMap<Key, Channel<Any>>()

    private fun channel(key: Key) = channels.getOrPut(key) { Channel(Channel.BUFFERED) }

    fun send(key: Key, value: Any) {
        channel(key).trySend(value)
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> receive(key: Key): Flow<T> = channel(key).receiveAsFlow() as Flow<T>

}
