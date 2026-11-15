package com.kieronquinn.app.taptap.ui.screens.settings.shared.selector.appshortcuts

import androidx.compose.runtime.Composable
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import com.kieronquinn.app.taptap.models.columbus.AppShortcutData
import com.kieronquinn.app.taptap.ui.base.BackAvailable
import com.kieronquinn.app.taptap.ui.base.ComposeFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsSharedAppShortcutsSelectorFragment : ComposeFragment(), BackAvailable {

    companion object {
        const val FRAGMENT_RESULT_KEY_APP_SHORTCUT = "fragment_result_app_shortcut"
    }

    private val viewModel by viewModel<SettingsSharedAppShortcutsSelectorViewModel>()

    @Composable
    override fun Content() = SettingsSharedAppShortcutsSelectorScreen(viewModel, ::onAppShortcutClicked)

    private fun onAppShortcutClicked(shortcut: AppShortcutData) {
        setFragmentResult(FRAGMENT_RESULT_KEY_APP_SHORTCUT, bundleOf(FRAGMENT_RESULT_KEY_APP_SHORTCUT to shortcut))
        viewModel.onAppShortcutClicked()
    }

}
