package com.kieronquinn.app.taptap.ui.base

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.compose.runtime.Composable
import androidx.fragment.app.DialogFragment
import androidx.fragment.compose.content
import com.kieronquinn.app.taptap.ui.theme.TapTapTheme

abstract class ComposeDialogFragment: DialogFragment() {

    @Composable
    abstract fun Content()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) = content {
        TapTapTheme { Content() }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

}
