package com.kieronquinn.app.taptap.ui.compose

import android.text.Html
import android.text.Spanned
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.fromHtml
import androidx.compose.material3.MaterialTheme

@Composable
fun resolveText(res: Int?, text: CharSequence?): CharSequence? {
    val context = LocalContext.current
    return text ?: res?.let { context.getText(it) }
}

@Composable
fun CharSequence.toAnnotatedString(linkClicked: ((String) -> Unit)? = null): AnnotatedString {
    val html = when {
        linkClicked != null -> toString()
        this is Spanned -> Html.toHtml(this, Html.TO_HTML_PARAGRAPH_LINES_CONSECUTIVE).trim()
        else -> return AnnotatedString(toString())
    }
    val annotated = AnnotatedString.fromHtml(
        html,
        linkStyles = TextLinkStyles(SpanStyle(color = MaterialTheme.colorScheme.primary)),
        linkInteractionListener = linkClicked?.let { onClick ->
            { link -> (link as? LinkAnnotation.Url)?.let { onClick(it.url) } }
        }
    )
    return annotated.subSequence(0, annotated.trimEnd().length)
}
