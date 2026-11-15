package com.kieronquinn.app.taptap.ui.navigation

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import com.jakewharton.processphoenix.ProcessPhoenix
import com.kieronquinn.app.taptap.components.navigation.BaseNavigation
import com.kieronquinn.app.taptap.components.navigation.NavigationEvent
import com.kieronquinn.app.taptap.ui.activities.MainActivity
import com.kieronquinn.app.taptap.ui.activities.MainActivity.Companion.EXTRA_SUPPRESS_SPLASH_DELAY

@Composable
fun NavigationEffects(navigation: BaseNavigation) {
    val context = LocalContext.current
    LaunchedEffect(navigation) {
        navigation.navigationBus.collect {
            when (it) {
                is NavigationEvent.Intent -> try {
                    context.startActivity(it.intent)
                } catch (e: ActivityNotFoundException) {
                    it.onActivityNotFound?.invoke()
                }
                NavigationEvent.Finish -> (context as? Activity)?.finish()
                NavigationEvent.RestartActivity -> (context as? Activity)?.let { activity ->
                    activity.finish()
                    activity.startActivity(activity.intent)
                }
                NavigationEvent.Phoenix -> ProcessPhoenix.triggerRebirth(context, Intent(context, MainActivity::class.java).apply {
                    putExtra(EXTRA_SUPPRESS_SPLASH_DELAY, true)
                })
            }
        }
    }
}
