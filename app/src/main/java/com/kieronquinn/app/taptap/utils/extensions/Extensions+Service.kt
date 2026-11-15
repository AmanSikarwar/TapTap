package com.kieronquinn.app.taptap.utils.extensions

import android.app.Notification
import android.app.Service
import android.content.pm.ServiceInfo
import android.util.Log
import android.os.Build

fun Service.startForegroundCompat(notificationId: Int, notification: Notification): Boolean {
    return try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(
                notificationId,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
            )
        }else{
            startForeground(notificationId, notification)
        }
        true
    }catch (e: Exception) {
        //Caches ForegroundServiceStartNotAllowedException on S+ when unable to startForeground
        Log.e("ForegroundService", "Unable to start foreground", e)
        false
    }
}