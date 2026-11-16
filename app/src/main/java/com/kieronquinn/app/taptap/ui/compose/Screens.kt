package com.kieronquinn.app.taptap.ui.compose

import androidx.annotation.DrawableRes
import androidx.compose.animation.graphics.ExperimentalAnimationGraphicsApi
import androidx.compose.animation.graphics.res.animatedVectorResource
import androidx.compose.animation.graphics.res.rememberAnimatedVectorPainter
import androidx.compose.animation.graphics.vector.AnimatedImageVector
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun MessageScreen(
    title: String,
    icon: @Composable () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.surface) {
        Column(
            Modifier
                .verticalScroll(rememberScrollState())
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
        ) {
            icon()
            Text(title, style = MaterialTheme.typography.headlineMedium, textAlign = TextAlign.Center)
            content()
        }
    }
}

@OptIn(ExperimentalAnimationGraphicsApi::class)
@Composable
fun AnimatedVector(@DrawableRes res: Int, modifier: Modifier = Modifier.size(128.dp)) {
    val image = AnimatedImageVector.animatedVectorResource(res)
    var atEnd by remember { mutableStateOf(false) }
    LaunchedEffect(res) { atEnd = true }
    Image(rememberAnimatedVectorPainter(image, atEnd), null, modifier)
}

@Composable
fun SheetMessage(
    title: String,
    text: CharSequence,
    positive: String,
    onPositive: () -> Unit,
    negative: String? = null,
    onNegative: () -> Unit = {}
) {
    Column(Modifier.padding(horizontal = 24.dp).padding(bottom = 16.dp)) {
        Text(title, style = MaterialTheme.typography.headlineSmall)
        Text(text.toAnnotatedString(), Modifier.padding(vertical = 16.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)) {
            negative?.let { OutlinedButton(onClick = onNegative) { Text(it) } }
            Button(onClick = onPositive) { Text(positive) }
        }
    }
}
