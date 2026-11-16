package com.kieronquinn.app.taptap.ui.compose

import androidx.annotation.RawRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.airbnb.lottie.LottieProperty
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.airbnb.lottie.compose.rememberLottieDynamicProperties
import com.airbnb.lottie.compose.rememberLottieDynamicProperty

@Composable
fun TintedLottie(
    @RawRes res: Int,
    color: Color,
    vararg layers: String,
    modifier: Modifier = Modifier,
    iterations: Int = LottieConstants.IterateForever
) {
    val composition = rememberLottieComposition(LottieCompositionSpec.RawRes(res)).value
    val properties = rememberLottieDynamicProperties(
        *layers.map { rememberLottieDynamicProperty(LottieProperty.COLOR, color.toArgb(), it, "**") }.toTypedArray()
    )
    LottieAnimation(composition, modifier = modifier, iterations = iterations, dynamicProperties = properties)
}
