package com.kieronquinn.app.taptap.components.columbus.sensors

import android.content.res.AssetManager
import android.util.Log
import com.google.android.columbus.sensors.TfClassifier
import com.kieronquinn.app.shared.tflite.Classifier
import com.kieronquinn.app.taptap.components.settings.TapModel
import com.kieronquinn.app.taptap.utils.extensions.runOnClose
import org.koin.core.scope.Scope
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.channels.FileChannel

class TapTapTfClassifier(
    assetManager: AssetManager,
    private val tapModel: TapModel,
    scope: Scope
) : TfClassifier(), Classifier {

    companion object {
        private const val TAG = "Columbus"
    }

    private val interpreter by lazy {
        try {
            assetManager.openFd(tapModel.path).let {
                Triple(
                    FileInputStream(it.fileDescriptor).channel,
                    it.startOffset,
                    it.declaredLength
                )
            }.run {
                Interpreter(first.map(FileChannel.MapMode.READ_ONLY, second, third))
            }.apply {
                Log.d(TAG, "tflite file loaded: ${tapModel.path}")
            }
        } catch (e: Exception) {
            Log.d(TAG, "load tflite file error: ${tapModel.path}")
            Log.d(TAG, "tflite file: $e")
            null
        }
    }

    override fun predict(input: ArrayList<Float>, size: Int): ArrayList<ArrayList<Float>> {
        val interpreter = interpreter ?: return ArrayList()
        return when (tapModel.modelType) {
            TapModel.ModelType.NEW -> predict12(interpreter, input, size)
            TapModel.ModelType.LEGACY -> predict11(interpreter, input, size)
            else -> throw RuntimeException("Unsupported")
        }
    }

    override fun predictArray(input: Array<FloatArray>, output: Array<FloatArray>) {
        val interpreter = interpreter ?: return
        return when (tapModel) {
            TapModel.SAMSUNG -> interpreter.run(input, output)
            else -> throw RuntimeException("Unsupported")
        }
    }

    init {
        scope.runOnClose {
            interpreter?.close()
        }
    }

}