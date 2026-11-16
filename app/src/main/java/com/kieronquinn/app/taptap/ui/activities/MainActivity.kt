package com.kieronquinn.app.taptap.ui.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.kieronquinn.app.taptap.components.navigation.ContainerNavigation
import com.kieronquinn.app.taptap.components.navigation.GestureConfigurationNavigation
import com.kieronquinn.app.taptap.components.navigation.RootNavigation
import com.kieronquinn.app.taptap.ui.navigation.TapTapApp
import com.kieronquinn.app.taptap.ui.screens.root.RootSharedViewModel
import com.kieronquinn.app.taptap.ui.theme.TapTapTheme
import com.kieronquinn.app.taptap.utils.extensions.EXTRA_KEY_IS_FROM_COLUMBUS
import com.kieronquinn.app.taptap.utils.extensions.delayPreDrawUntilFlow
import com.kieronquinn.app.taptap.work.TapTapUpdateCheckWorker
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity: ComponentActivity() {

    companion object {
        const val EXTRA_SUPPRESS_SPLASH_DELAY = "suppress_splash_delay"
    }

    private val rootViewModel by viewModel<RootSharedViewModel>()
    private val rootNavigation by inject<RootNavigation>()
    private val containerNavigation by inject<ContainerNavigation>()
    private val gestureNavigation by inject<GestureConfigurationNavigation>()

    override fun onCreate(savedInstanceState: Bundle?) {
        finishIfFromColumbus()
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        if (savedInstanceState == null) {
            rootNavigation.reset()
            containerNavigation.reset()
            gestureNavigation.reset()
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
            && !intent.getBooleanExtra(EXTRA_SUPPRESS_SPLASH_DELAY, false)) {
            findViewById<View>(android.R.id.content).delayPreDrawUntilFlow(rootViewModel.appReady, lifecycle)
        }
        setContent { TapTapTheme { TapTapApp() } }
        TapTapUpdateCheckWorker.queueCheckWorker(this)
    }

    override fun onNewIntent(intent: Intent) {
        finishIfFromColumbus(intent)
        super.onNewIntent(intent)
    }

    private fun finishIfFromColumbus(intent: Intent = this.intent){
        if(intent.getBooleanExtra(EXTRA_KEY_IS_FROM_COLUMBUS, false)){
            finishAndRemoveTask()
            return
        }
    }

}
