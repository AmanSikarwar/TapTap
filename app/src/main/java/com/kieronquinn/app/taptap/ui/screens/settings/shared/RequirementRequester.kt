package com.kieronquinn.app.taptap.ui.screens.settings.shared

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.kieronquinn.app.taptap.BuildConfig
import com.kieronquinn.app.taptap.R
import com.kieronquinn.app.taptap.components.accessibility.TapTapAccessibilityRouter
import com.kieronquinn.app.taptap.service.accessibility.TapTapAccessibilityService
import com.kieronquinn.app.taptap.service.accessibility.TapTapGestureAccessibilityService
import com.kieronquinn.app.taptap.ui.activities.MainActivity
import com.kieronquinn.app.taptap.utils.extensions.doesHaveNotificationPolicyAccess
import com.kieronquinn.app.taptap.utils.extensions.doesHaveTaskerPermission
import com.kieronquinn.app.taptap.utils.extensions.getAccessibilityIntent
import com.kieronquinn.app.taptap.utils.extensions.getAppInfoIntent
import com.kieronquinn.app.taptap.utils.extensions.getPermissionName
import com.kieronquinn.app.taptap.utils.extensions.getRequiredPermissions
import com.kieronquinn.app.taptap.utils.extensions.isPermissionDenied
import com.kieronquinn.app.taptap.utils.extensions.isServiceRunning
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.merge
import org.koin.compose.koinInject

class RequirementRequester(
    private val activity: Activity,
    private val permissionLauncher: ActivityResultLauncher<Array<String>>,
    private val permissionResults: MutableSharedFlow<Map<String, Boolean>>,
    val resumes: MutableSharedFlow<Unit>
) {

    private val context: Context = activity

    private suspend fun launchAndAwaitResume(intent: Intent) {
        activity.startActivity(intent)
        resumes.first()
    }

    /**
     *  Requests the permissions, through the app info screen if they were permanently denied.
     */
    suspend fun requestPermission(vararg permission: String): Boolean {
        val required = context.getRequiredPermissions(*permission)
        if (required.isEmpty()) return true
        required.forEach {
            if (activity.isPermissionDenied(it)) {
                Toast.makeText(context, context.getString(R.string.permission_toast, context.getPermissionName(it)), Toast.LENGTH_LONG).show()
                delay(250L)
                launchAndAwaitResume(context.getAppInfoIntent())
                if (activity.isPermissionDenied(it)) return false
            }
        }
        permissionLauncher.launch(required)
        return permissionResults.first().all { it.value }
    }

    suspend fun requestNotificationPolicy(): Boolean {
        if (context.doesHaveNotificationPolicyAccess()) return true
        launchAndAwaitResume(Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS))
        return context.doesHaveNotificationPolicyAccess()
    }

    suspend fun requestDisplayOverOtherApps(): Boolean {
        if (Settings.canDrawOverlays(context)) return true
        Toast.makeText(context, R.string.permission_toast_draw_over_apps, Toast.LENGTH_LONG).show()
        delay(250L)
        launchAndAwaitResume(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION).apply {
            data = Uri.parse("package:${BuildConfig.APPLICATION_ID}")
        })
        return Settings.canDrawOverlays(context)
    }

    suspend fun requestWriteSystemSettings(): Boolean {
        if (Settings.System.canWrite(context)) return true
        launchAndAwaitResume(Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS).apply {
            data = Uri.parse("package:${BuildConfig.APPLICATION_ID}")
        })
        return Settings.System.canWrite(context)
    }

    suspend fun requestAccessibilityService(): Boolean {
        if (context.isServiceRunning(TapTapAccessibilityService::class.java)) return true
        launchAndAwaitResume(context.getAccessibilityIntent(TapTapAccessibilityService::class.java))
        return context.isServiceRunning(TapTapAccessibilityService::class.java)
    }

    suspend fun requestGestureService(): Boolean {
        if (context.isServiceRunning(TapTapGestureAccessibilityService::class.java)) return true
        launchAndAwaitResume(context.getAccessibilityIntent(TapTapGestureAccessibilityService::class.java))
        return context.isServiceRunning(TapTapGestureAccessibilityService::class.java)
    }

    suspend fun requestTaskerPermission(): Boolean {
        if (context.doesHaveTaskerPermission()) return true
        Toast.makeText(context, R.string.tasker_content_toast, Toast.LENGTH_LONG).show()
        delay(250L)
        //The tno extra selects the Misc tab
        launchAndAwaitResume(Intent("net.dinglisch.android.tasker.ACTION_OPEN_PREFS").apply {
            addCategory(Intent.CATEGORY_DEFAULT)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            putExtra("tno", 3)
        })
        return context.doesHaveTaskerPermission()
    }

}

@Composable
fun rememberRequirementRequester(): RequirementRequester {
    val activity = LocalContext.current as Activity
    val permissionResults = remember { MutableSharedFlow<Map<String, Boolean>>(extraBufferCapacity = 1) }
    val resumes = remember { MutableSharedFlow<Unit>(extraBufferCapacity = 1) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
        permissionResults.tryEmit(it)
    }
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) { resumes.tryEmit(Unit) }
    val router = koinInject<TapTapAccessibilityRouter>()
    LaunchedEffect(router) {
        merge(router.accessibilityStartBus, router.gestureAccessibilityStartBus).collect {
            activity.startActivity(Intent(activity, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
            })
        }
    }
    return remember(activity, launcher) { RequirementRequester(activity, launcher, permissionResults, resumes) }
}
