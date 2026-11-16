package com.kieronquinn.app.taptap.ui.compose

import android.widget.TextView
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.res.ResourcesCompat
import com.kieronquinn.app.taptap.R
import io.noties.markwon.Markwon
import org.koin.compose.koinInject

@Composable
fun MarkdownText(markdown: String, modifier: Modifier = Modifier) {
    val markwon = koinInject<Markwon>()
    val color = LocalContentColor.current.toArgb()
    val link = MaterialTheme.colorScheme.primary.toArgb()
    AndroidView(
        factory = { TextView(it).apply { typeface = ResourcesCompat.getFont(it, R.font.google_sans_text) } },
        modifier = modifier,
        update = {
            it.setTextColor(color)
            it.setLinkTextColor(link)
            markwon.setMarkdown(it, markdown)
        }
    )
}
