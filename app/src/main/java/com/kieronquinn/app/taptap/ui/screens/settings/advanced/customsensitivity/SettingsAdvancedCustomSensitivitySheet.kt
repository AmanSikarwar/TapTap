package com.kieronquinn.app.taptap.ui.screens.settings.advanced.customsensitivity

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.kieronquinn.app.taptap.R
import com.kieronquinn.app.taptap.ui.screens.container.ContainerSharedViewModel

@Composable
fun CustomSensitivitySheet(viewModel: SettingsAdvancedCustomSensitivityViewModel, onRestartService: () -> Unit) {
    var text by remember { mutableStateOf(viewModel.customSensitivity.value) }
    val valid by viewModel.isSensitivityValid.collectAsState(true)
    LaunchedEffect(viewModel) {
        viewModel.restartService?.collect { onRestartService() }
    }
    Column(Modifier.padding(horizontal = 24.dp).padding(bottom = 16.dp)) {
        Text(stringResource(R.string.bs_advanced_custom_sensitivity), style = MaterialTheme.typography.headlineSmall)
        OutlinedTextField(
            value = text,
            onValueChange = {
                text = it
                viewModel.setValue(it)
            },
            label = { Text(stringResource(R.string.bs_advanced_custom_sensitivity_hint)) },
            isError = !valid,
            supportingText = if (valid) null else ({ Text(stringResource(R.string.bs_advanced_custom_sensitivity_invalid)) }),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)
        )
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            TextButton(onClick = viewModel::onNeutralClicked) { Text(stringResource(R.string.bs_advanced_custom_sensitivity_reset)) }
            Row(Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)) {
                TextButton(onClick = viewModel::onNegativeClicked) { Text(stringResource(android.R.string.cancel)) }
                Button(onClick = viewModel::onPositiveClicked, enabled = valid) { Text(stringResource(android.R.string.ok)) }
            }
        }
    }
}
