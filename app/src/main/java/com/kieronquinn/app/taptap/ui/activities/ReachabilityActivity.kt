package com.kieronquinn.app.taptap.ui.activities

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.kieronquinn.app.taptap.ui.screens.reachability.ReachabilityScreen
import com.kieronquinn.app.taptap.ui.screens.reachability.ReachabilityViewModel
import com.kieronquinn.app.taptap.ui.theme.TapTapTheme
import org.koin.androidx.viewmodel.ext.android.viewModel

class ReachabilityActivity: ComponentActivity() {

    companion object {
        const val KEY_PACKAGE_NAME = "package_name"
    }

    private val viewModel by viewModel<ReachabilityViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { TapTapTheme { ReachabilityScreen(viewModel) } }
    }

    override fun onMultiWindowModeChanged(isInMultiWindowMode: Boolean, newConfig: Configuration) {
        super.onMultiWindowModeChanged(isInMultiWindowMode, newConfig)
        if (isInMultiWindowMode) return
        (viewModel.getCurrentApp() ?: intent.getStringExtra(KEY_PACKAGE_NAME))?.let {
            packageManager.getLaunchIntentForPackage(it)?.run(::startActivity)
        }
        finishAndRemoveTask()
    }

}
