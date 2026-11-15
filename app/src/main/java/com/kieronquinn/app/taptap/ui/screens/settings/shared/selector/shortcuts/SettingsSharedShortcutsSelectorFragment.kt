package com.kieronquinn.app.taptap.ui.screens.settings.shared.selector.shortcuts

import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import com.kieronquinn.app.taptap.R
import com.kieronquinn.app.taptap.ui.base.BackAvailable
import com.kieronquinn.app.taptap.utils.extensions.serialize
import org.koin.androidx.viewmodel.ext.android.viewModel
import androidx.compose.runtime.Composable
import com.kieronquinn.app.taptap.ui.base.ComposeFragment

class SettingsSharedShortcutsSelectorFragment : ComposeFragment(), BackAvailable {

    companion object {
        const val FRAGMENT_RESULT_KEY_SHORTCUT = "fragment_result_shortcut"
    }

    private val viewModel by viewModel<SettingsSharedShortcutsSelectorViewModel>()

    private val activityResultContract = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        it?.data?.getParcelableExtra<Intent>(Intent.EXTRA_SHORTCUT_INTENT)?.let { intent ->
            onShortcutResult(intent)
        } ?: run {
            onUnsupportedShortcut()
        }
    }

    @Composable
    override fun Content() = SettingsSharedShortcutsSelectorScreen(viewModel, ::onShortcutClicked)

    private fun onShortcutClicked(componentName: ComponentName) {
        Intent().apply {
            `package` = componentName.packageName
            component = componentName
        }.also {
            activityResultContract.launch(it)
        }
    }

    private fun onShortcutResult(intent: Intent) {
        val serializedIntent = intent.serialize() ?: run {
            onUnsupportedShortcut()
            return
        }
        setFragmentResult(FRAGMENT_RESULT_KEY_SHORTCUT, bundleOf(FRAGMENT_RESULT_KEY_SHORTCUT to serializedIntent))
        viewModel.unwind()
    }

    private fun onUnsupportedShortcut(){
        Toast.makeText(requireContext(), R.string.action_launch_shortcut_toast, Toast.LENGTH_LONG).show()
    }

}
