package com.kieronquinn.app.taptap.ui.screens.settings.generic

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.Modifier
import androidx.fragment.app.Fragment
import androidx.fragment.compose.content
import com.kieronquinn.app.taptap.ui.compose.SettingsList
import com.kieronquinn.app.taptap.ui.compose.containerBottomPadding
import com.kieronquinn.app.taptap.ui.screens.container.ContainerSharedViewModel
import com.kieronquinn.app.taptap.ui.screens.settings.generic.GenericSettingsViewModel.SettingsItem
import com.kieronquinn.app.taptap.ui.theme.TapTapTheme
import com.kieronquinn.app.taptap.utils.extensions.whenResumed
import org.koin.androidx.viewmodel.ext.android.activityViewModel

abstract class GenericSettingsFragment: Fragment() {

    abstract val viewModel: GenericSettingsViewModel
    abstract val items: List<SettingsItem>
    protected val containerViewModel by activityViewModel<ContainerSharedViewModel>()
    private val refreshKey = mutableIntStateOf(0)

    protected open val header: (@Composable () -> Unit)? = null

    @Composable
    protected open fun contentPadding(): PaddingValues = containerBottomPadding()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) = content {
        TapTapTheme {
            val header = header
            SettingsList(
                items = items,
                modifier = Modifier.fillMaxSize(),
                refreshKey = refreshKey.intValue,
                contentPadding = contentPadding(),
                header = header?.let { h -> { item { h() } } }
            )
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        whenResumed {
            viewModel.restartService?.collect {
                containerViewModel.restartService(requireContext())
            }
        }
    }

    protected fun refreshVisibleItems() {
        refreshKey.intValue++
    }

}
