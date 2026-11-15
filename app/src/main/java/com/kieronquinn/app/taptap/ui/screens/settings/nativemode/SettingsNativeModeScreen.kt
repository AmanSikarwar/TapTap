package com.kieronquinn.app.taptap.ui.screens.settings.nativemode

import android.app.Notification
import android.app.PendingIntent
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.kieronquinn.app.taptap.ui.activities.MainActivity
import com.kieronquinn.app.taptap.utils.notifications.TapTapNotificationChannel
import com.kieronquinn.app.taptap.utils.notifications.TapTapNotificationId
import com.kieronquinn.app.taptap.utils.notifications.TapTapNotificationIntentId
import kotlinx.coroutines.delay
import android.content.Context
import androidx.compose.runtime.DisposableEffect
import com.kieronquinn.app.taptap.ui.screens.container.ContainerSharedViewModel
import org.koin.compose.viewmodel.koinActivityViewModel
import org.koin.compose.viewmodel.koinViewModel
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kieronquinn.app.taptap.R
import com.kieronquinn.app.taptap.ui.compose.MessageCard
import com.kieronquinn.app.taptap.ui.compose.ModeScreen
import com.kieronquinn.app.taptap.ui.screens.settings.lowpower.ShizukuError

@Composable
fun SettingsNativeModeScreen(viewModel: SettingsNativeModeViewModel, onSetupNotification: suspend () -> Unit) {
    val context = LocalContext.current
    val checked by viewModel.nativeModeEnabled.collectAsStateWithLifecycle()
    val enabled by viewModel.switchEnabled.collectAsState(false)
    val shizukuInstalled by viewModel.shizukuInstalled.collectAsState(true)
    LifecycleResumeEffect(viewModel) {
        viewModel.checkState(context)
        onPauseOrDispose {}
    }
    LaunchedEffect(viewModel) {
        viewModel.toastBus.collect { Toast.makeText(context, it, Toast.LENGTH_LONG).show() }
    }
    LaunchedEffect(viewModel) {
        viewModel.setupNotificationBus.collect { onSetupNotification() }
    }
    ModeScreen(
        switchLabel = stringResource(R.string.settings_native_mode_enable),
        checked = checked,
        enabled = enabled,
        onToggle = { viewModel.onSwitchClicked(context) }
    ) {
        ShizukuError(shizukuInstalled, { viewModel.onShizukuClicked(context) }, viewModel::onSuiClicked)
        MessageCard(R.drawable.ic_about, stringResource(R.string.setting_native_mode_content))
    }
}

@Composable
fun SettingsNativeModeRoute(viewModel: SettingsNativeModeViewModel = koinViewModel()) {
    val context = LocalContext.current
    val shared = koinActivityViewModel<ContainerSharedViewModel>()
    DisposableEffect(shared) {
        shared.setSuppressColumbusRestart(true)
        onDispose { shared.setSuppressColumbusRestart(false) }
    }
    SettingsNativeModeScreen(viewModel) { context.showSetupNotification() }
}

private suspend fun Context.showSetupNotification() = with(TapTapNotificationChannel.NativeSetup) {
    showNotification(
        this@showSetupNotification,
        TapTapNotificationId.NATIVE_SETUP
    ){
        val clickIntent = Intent(this@showSetupNotification, MainActivity::class.java)
        val content = this@showSetupNotification.getString(R.string.notification_native_setup_content)
        it.setOngoing(false)
        it.setSmallIcon(TapTapNotificationChannel.NOTIFICATION_ICON)
        it.setContentTitle(this@showSetupNotification.getString(R.string.notification_native_setup_title))
        it.setContentText(content)
        it.setAutoCancel(true)
        it.setCategory(Notification.CATEGORY_REMINDER)
        it.setStyle(NotificationCompat.BigTextStyle(it).bigText(content))
        it.setContentIntent(PendingIntent.getActivity(this@showSetupNotification, TapTapNotificationIntentId.NATIVE_SETUP_CLICK.ordinal, clickIntent, PendingIntent.FLAG_IMMUTABLE))
        it.priority = NotificationCompat.PRIORITY_MAX
    }
    //Clear notification after 5 seconds
    delay(5000L)
    cancelNotifications(this@showSetupNotification, TapTapNotificationId.NATIVE_SETUP)
}
