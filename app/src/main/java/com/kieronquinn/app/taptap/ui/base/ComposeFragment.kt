package com.kieronquinn.app.taptap.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.compose.runtime.Composable
import androidx.fragment.app.Fragment
import androidx.fragment.compose.content
import com.kieronquinn.app.taptap.ui.theme.TapTapTheme

abstract class ComposeFragment: Fragment() {

    @Composable
    abstract fun Content()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) = content {
        TapTapTheme { Content() }
    }

}
