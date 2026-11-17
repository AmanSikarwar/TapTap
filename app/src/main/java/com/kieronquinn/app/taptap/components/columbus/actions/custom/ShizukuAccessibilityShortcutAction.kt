package com.kieronquinn.app.taptap.components.columbus.actions.custom

import android.accessibilityservice.AccessibilityService
import android.content.Context
import androidx.lifecycle.Lifecycle
import com.google.android.columbus.feedback.FeedbackEffect
import com.google.android.columbus.sensors.GestureSensor
import com.kieronquinn.app.taptap.components.columbus.actions.TapTapAction
import com.kieronquinn.app.taptap.components.columbus.gates.TapTapWhenGate
import com.kieronquinn.app.taptap.repositories.service.TapTapShizukuServiceRepository

class ShizukuAccessibilityShortcutAction(
    serviceLifecycle: Lifecycle,
    context: Context,
    whenGates: List<TapTapWhenGate>,
    effects: Set<FeedbackEffect>,
    private val service: TapTapShizukuServiceRepository
) : TapTapAction(
    serviceLifecycle, context, whenGates, effects
) {

    override val tag = "ShizukuAccessibilityShortcutAction"

    override suspend fun onTriggered(
        detectionProperties: GestureSensor.DetectionProperties,
        isTripleTap: Boolean
    ) {
        val result = service.runWithShellService {
            it.callAccessibilitySystemAction(AccessibilityService.GLOBAL_ACTION_ACCESSIBILITY_SHORTCUT)
        }
        if(result is TapTapShizukuServiceRepository.ShizukuServiceResponse.Failed){
            showShizukuNotification(result.reason.contentRes)
        }
    }

}
