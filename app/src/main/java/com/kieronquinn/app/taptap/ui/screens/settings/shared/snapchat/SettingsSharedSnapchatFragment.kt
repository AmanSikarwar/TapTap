package com.kieronquinn.app.taptap.ui.screens.settings.shared.snapchat

import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.navArgs
import com.kieronquinn.app.taptap.models.shared.ARG_NAME_SHARED_ARGUMENT
import com.kieronquinn.app.taptap.ui.base.BackAvailable
import com.kieronquinn.app.taptap.ui.base.ComposeFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsSharedSnapchatFragment : ComposeFragment(), BackAvailable {

    companion object {
        const val FRAGMENT_RESULT_KEY_SNAPCHAT = "fragment_result_snapchat"
    }

    private val args by navArgs<SettingsSharedSnapchatFragmentArgs>()
    private val viewModel by viewModel<SettingsSharedSnapchatViewModel>()

    @Composable
    override fun Content() = SettingsSharedSnapchatScreen(viewModel) {
        setFragmentResult(FRAGMENT_RESULT_KEY_SNAPCHAT, formResultBundle(it))
    }

    private fun formResultBundle(available: Boolean): Bundle {
        return bundleOf(
            FRAGMENT_RESULT_KEY_SNAPCHAT to available,
            ARG_NAME_SHARED_ARGUMENT to args.argument
        )
    }

}
