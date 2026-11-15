package com.kieronquinn.app.taptap.ui.screens.settings.shared.shizuku

import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.navArgs
import com.kieronquinn.app.taptap.models.shared.ARG_NAME_SHARED_ARGUMENT
import com.kieronquinn.app.taptap.ui.base.BackAvailable
import com.kieronquinn.app.taptap.ui.base.ComposeFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsSharedShizukuPermissionFlowFragment : ComposeFragment(), BackAvailable {

    companion object {
        const val FRAGMENT_RESULT_KEY_SHIZUKU_PERMISSION = "fragment_result_shizuku_permission"
    }

    private val viewModel by viewModel<SettingsSharedShizukuPermissionFlowViewModel>()
    private val args by navArgs<SettingsSharedShizukuPermissionFlowFragmentArgs>()

    @Composable
    override fun Content() = SettingsSharedShizukuPermissionFlowScreen(viewModel, args.argument) {
        setFragmentResult(FRAGMENT_RESULT_KEY_SHIZUKU_PERMISSION, formResultBundle(it))
    }

    private fun formResultBundle(permissionGranted: Boolean): Bundle {
        return bundleOf(
            FRAGMENT_RESULT_KEY_SHIZUKU_PERMISSION to permissionGranted,
            ARG_NAME_SHARED_ARGUMENT to args.argument
        )
    }

}
