package com.kieronquinn.app.taptap.ui.screens.settings.nativemode

import android.app.Notification
import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import androidx.compose.runtime.Composable
import com.kieronquinn.app.taptap.ui.base.ComposeFragment
import android.view.View
import androidx.core.app.NotificationCompat
import com.kieronquinn.app.taptap.R
import com.kieronquinn.app.taptap.ui.activities.MainActivity
import com.kieronquinn.app.taptap.ui.base.BackAvailable
import com.kieronquinn.app.taptap.ui.screens.container.ContainerSharedViewModel
import com.kieronquinn.app.taptap.utils.notifications.TapTapNotificationChannel
import com.kieronquinn.app.taptap.utils.notifications.TapTapNotificationId
import com.kieronquinn.app.taptap.utils.notifications.TapTapNotificationIntentId
import kotlinx.coroutines.delay
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsNativeModeFragment: ComposeFragment(), BackAvailable {

    private val viewModel by viewModel<SettingsNativeModeViewModel>()
    private val containerViewModel by activityViewModel<ContainerSharedViewModel>()

    @Composable
    override fun Content() = SettingsNativeModeScreen(viewModel, ::showSetupNotification)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        containerViewModel.setSuppressColumbusRestart(true)
    }

    override fun onDestroyView() {
        containerViewModel.setSuppressColumbusRestart(false)
        super.onDestroyView()
    }

    private suspend fun showSetupNotification() = with(TapTapNotificationChannel.NativeSetup) {
        showNotification(
            requireContext(),
            TapTapNotificationId.NATIVE_SETUP
        ){
            val clickIntent = Intent(requireContext(), MainActivity::class.java)
            val content = getString(R.string.notification_native_setup_content)
            it.setOngoing(false)
            it.setSmallIcon(TapTapNotificationChannel.NOTIFICATION_ICON)
            it.setContentTitle(getString(R.string.notification_native_setup_title))
            it.setContentText(content)
            it.setAutoCancel(true)
            it.setCategory(Notification.CATEGORY_REMINDER)
            it.setStyle(NotificationCompat.BigTextStyle(it).bigText(content))
            it.setContentIntent(PendingIntent.getActivity(context, TapTapNotificationIntentId.NATIVE_SETUP_CLICK.ordinal, clickIntent, PendingIntent.FLAG_IMMUTABLE))
            it.priority = NotificationCompat.PRIORITY_MAX
        }
        //Clear notification after 5 seconds
        delay(5000L)
        cancelNotifications(requireContext(), TapTapNotificationId.NATIVE_SETUP)
    }

}