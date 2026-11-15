package com.kieronquinn.app.taptap.ui.base

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.fragment.compose.content
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.kieronquinn.app.taptap.ui.theme.TapTapTheme

@OptIn(ExperimentalMaterial3Api::class)
abstract class ComposeBottomSheetFragment: BottomSheetDialogFragment() {

    @Composable
    abstract fun ColumnScope.Content()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) = content {
        TapTapTheme {
            Surface(shape = BottomSheetDefaults.ExpandedShape, color = BottomSheetDefaults.ContainerColor) {
                Column(
                    Modifier.fillMaxWidth().windowInsetsPadding(WindowInsets.navigationBars),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    BottomSheetDefaults.DragHandle()
                    Content()
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        (view?.parent as? View)?.setBackgroundColor(Color.TRANSPARENT)
        (dialog as? BottomSheetDialog)?.behavior?.state = BottomSheetBehavior.STATE_EXPANDED
    }

}
