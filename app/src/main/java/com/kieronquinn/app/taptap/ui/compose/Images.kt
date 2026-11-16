package com.kieronquinn.app.taptap.ui.compose

import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URL

fun Drawable.toImageBitmap(sizePx: Int? = null): ImageBitmap =
    (if (sizePx != null) toBitmap(sizePx, sizePx) else toBitmap()).asImageBitmap()

@Composable
fun DrawableImage(@DrawableRes res: Int, size: Dp, modifier: Modifier = Modifier, contentDescription: String? = null) {
    val context = LocalContext.current
    val sizePx = with(LocalDensity.current) { size.roundToPx() }
    val bitmap = remember(res, sizePx) { ContextCompat.getDrawable(context, res)!!.toImageBitmap(sizePx) }
    Image(bitmap, contentDescription, modifier.size(size))
}

@Composable
fun rememberUrlImage(url: String?): ImageBitmap? = produceState<ImageBitmap?>(null, url) {
    value = url?.let {
        withContext(Dispatchers.IO) {
            runCatching { URL(it).openStream().use { BitmapFactory.decodeStream(it)?.asImageBitmap() } }.getOrNull()
        }
    }
}.value
