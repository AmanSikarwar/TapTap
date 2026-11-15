package com.kieronquinn.app.taptap.ui.screens.settings.shared.selector.quicksetting

import androidx.compose.runtime.Composable
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import com.kieronquinn.app.taptap.repositories.quicksettings.QuickSettingsRepository
import com.kieronquinn.app.taptap.ui.base.BackAvailable
import com.kieronquinn.app.taptap.ui.base.ComposeFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsSharedQuickSettingSelectorFragment: ComposeFragment(), BackAvailable {

    companion object {
        const val FRAGMENT_RESULT_KEY_QUICK_SETTING = "fragment_result_quick_setting"
    }

    private val viewModel by viewModel<SettingsSharedQuickSettingSelectorViewModel>()

    @Composable
    override fun Content() = SettingsSharedQuickSettingSelectorScreen(viewModel, ::onQuickSettingClicked)

    private fun onQuickSettingClicked(quickSetting: QuickSettingsRepository.QuickSetting){
        setFragmentResult(FRAGMENT_RESULT_KEY_QUICK_SETTING, bundleOf(FRAGMENT_RESULT_KEY_QUICK_SETTING to quickSetting))
        viewModel.onQuickSettingClicked()
    }

}