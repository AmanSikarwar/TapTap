package com.kieronquinn.app.taptap.ui.compose

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.dp

val LocalContainerPadding = staticCompositionLocalOf<PaddingValues?> { null }

@Composable
fun containerBottomPadding(): PaddingValues {
    val bottom = LocalContainerPadding.current?.calculateBottomPadding()
        ?: WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    return PaddingValues(bottom = bottom + 16.dp)
}
