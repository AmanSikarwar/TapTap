package com.kieronquinn.app.taptap.ui.compose

import android.content.ComponentName
import android.content.Context
import android.graphics.drawable.Drawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun AsyncIcon(key: Any, size: Dp = 40.dp, modifier: Modifier = Modifier, load: Context.() -> Drawable?) {
    val context = LocalContext.current
    val sizePx = with(LocalDensity.current) { size.roundToPx() }
    val bitmap = produceState<ImageBitmap?>(null, key) {
        value = withContext(Dispatchers.IO) {
            runCatching { context.load()?.toImageBitmap(sizePx) }.getOrNull()
        }
    }.value
    if (bitmap != null) Image(bitmap, null, modifier.size(size)) else Box(modifier.size(size))
}

@Composable
fun AppIcon(packageName: String, size: Dp = 40.dp) = AsyncIcon(packageName, size) {
    packageManager.getApplicationIcon(packageName)
}

@Composable
fun ComponentIcon(component: ComponentName, size: Dp = 40.dp) = AsyncIcon(component, size) {
    runCatching { packageManager.getActivityIcon(component) }.getOrNull()
        ?: runCatching { packageManager.getServiceInfo(component, 0).loadIcon(packageManager) }.getOrNull()
        ?: packageManager.getApplicationIcon(component.packageName)
}
