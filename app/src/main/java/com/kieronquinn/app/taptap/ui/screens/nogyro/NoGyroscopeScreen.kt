package com.kieronquinn.app.taptap.ui.screens.nogyro

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.kieronquinn.app.taptap.R
import com.kieronquinn.app.taptap.ui.compose.AnimatedVector
import com.kieronquinn.app.taptap.ui.compose.MessageScreen

@Composable
fun NoGyroscopeScreen(onClose: () -> Unit) {
    MessageScreen(
        title = stringResource(R.string.modal_no_gyroscope_title),
        icon = { AnimatedVector(R.drawable.avd_gyroscope) }
    ) {
        Text(stringResource(R.string.modal_no_gyroscope_content), textAlign = TextAlign.Center)
        Button(onClick = onClose) { Text(stringResource(R.string.close)) }
    }
}
