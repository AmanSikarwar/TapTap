package com.kieronquinn.app.taptap.ui.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.kieronquinn.app.taptap.R

@Composable
fun SetupScaffold(
    onBack: (() -> Unit)?,
    title: String? = null,
    bottomBar: @Composable () -> Unit = {},
    content: @Composable ColumnScope.() -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {},
                scrollBehavior = scrollBehavior,
                navigationIcon = {
                    if (onBack != null) IconButton(onClick = onBack) {
                        Icon(painterResource(R.drawable.ic_back), stringResource(R.string.back))
                    }
                }
            )
        },
        bottomBar = bottomBar
    ) { padding ->
        Column(
            Modifier.padding(padding).verticalScroll(rememberScrollState()).padding(horizontal = 24.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            title?.let {
                Text(it, style = MaterialTheme.typography.headlineMedium, textAlign = TextAlign.Center, modifier = Modifier.padding(bottom = 16.dp))
            }
            content()
        }
    }
}

@Composable
fun SetupBottomBar(content: @Composable () -> Unit) {
    Column(Modifier.fillMaxWidth().navigationBarsPadding().padding(16.dp), horizontalAlignment = Alignment.End) {
        content()
    }
}
