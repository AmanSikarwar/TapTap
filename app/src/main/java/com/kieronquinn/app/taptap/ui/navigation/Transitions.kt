package com.kieronquinn.app.taptap.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.navigation3.scene.Scene
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation3.runtime.NavEntryDecorator

private const val DURATION = 400
private val easing = CubicBezierEasing(0.2f, 0f, 0f, 1f)

fun <T : Any> slideForward(): AnimatedContentTransitionScope<Scene<T>>.() -> ContentTransform = {
    slideInHorizontally(tween(DURATION, easing = easing)) { it } togetherWith
        slideOutHorizontally(tween(DURATION, easing = easing)) { -it / 4 }
}

fun <T : Any> slideBack(): AnimatedContentTransitionScope<Scene<T>>.() -> ContentTransform = {
    (slideInHorizontally(tween(DURATION, easing = easing)) { -it / 4 } togetherWith
        slideOutHorizontally(tween(DURATION, easing = easing)) { it }).apply { targetContentZIndex = -1f }
}

fun <T : Any> slidePredictiveBack(): AnimatedContentTransitionScope<Scene<T>>.(Int) -> ContentTransform = {
    slideBack<T>()()
}

@Composable
fun <T : Any> rememberBackgroundNavEntryDecorator(color: Color): NavEntryDecorator<T> = remember(color) {
    NavEntryDecorator { entry ->
        if (entry.metadata.isEmpty()) {
            Box(Modifier.fillMaxSize().background(color)) { entry.Content() }
        } else {
            entry.Content()
        }
    }
}
