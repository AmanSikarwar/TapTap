package com.kieronquinn.app.taptap.ui.screens.settings.shared.selector.packagename

import android.os.Bundle
import android.view.View
import androidx.compose.runtime.Composable
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.navArgs
import com.kieronquinn.app.taptap.models.shared.ARG_NAME_SHARED_ARGUMENT
import com.kieronquinn.app.taptap.ui.base.BackAvailable
import com.kieronquinn.app.taptap.ui.base.ComposeFragment
import com.kieronquinn.app.taptap.ui.base.ProvidesTitle
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsSharedPackageSelectorFragment : ComposeFragment(), BackAvailable, ProvidesTitle {

    companion object {
        const val FRAGMENT_RESULT_KEY_PACKAGE = "fragment_result_package"
        const val FRAGMENT_EXTRA_TITLE = "title"
        const val FRAGMENT_EXTRA_SHOW_ALL_APPS = "show_all_apps"
    }

    private val viewModel by viewModel<SettingsSharedPackageSelectorViewModel>()
    private val args by navArgs<SettingsSharedPackageSelectorFragmentArgs>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.setShowAllApps(args.showAllApps)
    }

    @Composable
    override fun Content() = SettingsSharedPackageSelectorScreen(viewModel, ::onAppClicked)

    private fun onAppClicked(packageName: String) {
        setFragmentResult(FRAGMENT_RESULT_KEY_PACKAGE, formResultBundle(packageName))
        viewModel.onAppClicked()
    }

    private fun formResultBundle(packageName: String): Bundle {
        return bundleOf(
            FRAGMENT_RESULT_KEY_PACKAGE to packageName,
            ARG_NAME_SHARED_ARGUMENT to args.argument
        )
    }

    override fun getTitle(): CharSequence? {
        return getString(args.title)
    }

}