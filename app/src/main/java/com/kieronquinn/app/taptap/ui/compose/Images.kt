package com.kieronquinn.app.taptap.ui.compose

import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap

fun Drawable.toImageBitmap(): ImageBitmap = toBitmap().asImageBitmap()

@Composable
fun DrawableImage(@DrawableRes res: Int, modifier: Modifier = Modifier, contentDescription: String? = null) {
    val context = LocalContext.current
    val bitmap = remember(res) { ContextCompat.getDrawable(context, res)!!.toImageBitmap() }
    Image(bitmap, contentDescription, modifier)
}
