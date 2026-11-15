package com.kieronquinn.app.taptap.ui.screens.setup.base

import android.os.Bundle
import android.view.View
import androidx.activity.addCallback
import com.kieronquinn.app.taptap.ui.base.ComposeFragment
import com.kieronquinn.app.taptap.ui.base.ProvidesBack

abstract class BaseSetupFragment: ComposeFragment() {

    abstract val viewModel: BaseSetupViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(this is ProvidesBack) {
            requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
                onBackPressed()
            }
        }
    }

}
