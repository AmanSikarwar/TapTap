package com.kieronquinn.app.taptap.ui.activities

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.documentfile.provider.DocumentFile
import com.kieronquinn.app.taptap.BuildConfig
import com.kieronquinn.app.taptap.R
import com.kieronquinn.app.taptap.ui.compose.toAnnotatedString
import com.kieronquinn.app.taptap.ui.theme.TapTapTheme
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 *  This activity is intentionally barebones - no fragments and minimal library use.
 *  It should be able to handle an incoming crash from the extras, show it and export it.
 */
class CrashReportingActivity : ComponentActivity() {

    companion object {
        const val KEY_EXCEPTION = "exception"
        private const val CRASH_REPORT_FILE_TEMPLATE = "tap_tap_crash_report_%s.txt"
    }

    private val saveReport = registerForActivityResult(ActivityResultContracts.CreateDocument("text/plain")){
        if(it == null) return@registerForActivityResult
        saveCrashReport(it)
    }

    private val crashReport by lazy {
        createCrashReport(intent.getStringExtra(KEY_EXCEPTION) ?: run {
            finish()
            ""
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TapTapTheme {
                CrashReportingScreen(
                    report = crashReport,
                    onClose = ::finish,
                    onSave = { saveReport.launch(getFilename()) },
                    onLink = ::openLink
                )
            }
        }
    }

    private fun openLink(url: String) {
        startActivity(Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(url)
        })
    }

    private fun createCrashReport(exception: String): String {
        val header = getString(R.string.crash_reporting_header)
        val appVersion = getString(
            R.string.crash_reporting_app_version,
            BuildConfig.VERSION_NAME,
            BuildConfig.VERSION_CODE
        )
        val device = getString(R.string.crash_reporting_device, Build.MODEL)
        val api = getString(R.string.crash_reporting_api, Build.VERSION.SDK_INT)
        val fingerprint = getString(R.string.crash_reporting_fingerprint, Build.FINGERPRINT)
        val exceptionHeader = getString(R.string.crash_reporting_exception_header)
        return StringBuilder().apply {
            appendLine(header)
            appendLine()
            appendLine(appVersion)
            appendLine(device)
            appendLine(api)
            appendLine(fingerprint)
            appendLine()
            appendLine(exceptionHeader)
            appendLine()
            appendLine(exception)
        }.toString()
    }

    private fun saveCrashReport(uri: Uri) {
        try {
            val file = DocumentFile.fromSingleUri(this, uri) ?: throw Exception()
            val outputStream = contentResolver.openOutputStream(file.uri) ?: throw Exception()
            outputStream.bufferedWriter().use {
                it.write(crashReport)
                it.flush()
            }
            Toast.makeText(this, getString(R.string.crash_reporting_toast_success), Toast.LENGTH_LONG).show()
        }catch (e: Exception){
            Toast.makeText(this, getString(R.string.crash_reporting_toast_failed), Toast.LENGTH_LONG).show()
        }
    }

    private fun getFilename(): String {
        val time = LocalDateTime.now()
        val dateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
        return String.format(CRASH_REPORT_FILE_TEMPLATE, dateTimeFormatter.format(time))
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CrashReportingScreen(report: String, onClose: () -> Unit, onSave: () -> Unit, onLink: (String) -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.crash_reporting_title)) },
                navigationIcon = {
                    IconButton(onClick = onClose) { Icon(painterResource(R.drawable.ic_back), stringResource(R.string.back)) }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onSave,
                icon = { Icon(painterResource(R.drawable.ic_save), null) },
                text = { Text(stringResource(R.string.crash_reporting_save)) }
            )
        }
    ) { padding ->
        Column(Modifier.padding(padding).verticalScroll(rememberScrollState()).padding(16.dp)) {
            Text(stringResource(R.string.crash_reporting_content_intro).toAnnotatedString(onLink))
            SelectionContainer {
                Text(report, Modifier.padding(top = 16.dp), fontFamily = FontFamily.Monospace, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
