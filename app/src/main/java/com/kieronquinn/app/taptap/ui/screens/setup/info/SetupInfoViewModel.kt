package com.kieronquinn.app.taptap.ui.screens.setup.info

import com.kieronquinn.app.taptap.ui.navigation.*

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.kieronquinn.app.taptap.components.navigation.RootNavigation
import com.kieronquinn.app.taptap.components.settings.TapTapSettings
import com.kieronquinn.app.taptap.ui.screens.setup.base.BaseSetupViewModel
import com.kieronquinn.app.taptap.utils.extensions.hasNotificationPermission
import kotlinx.coroutines.launch

abstract class SetupInfoViewModel: BaseSetupViewModel() {

    abstract fun onLinkClicked(url: String)
    abstract fun onSourceClicked()
    abstract fun onNextClicked(context: Context)

}

class SetupInfoViewModelImpl(private val settings: TapTapSettings, private val navigation: RootNavigation): SetupInfoViewModel() {

    override fun onLinkClicked(url: String) {
        viewModelScope.launch {
            navigation.navigate(Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(url)
            })
        }
    }

    override fun onSourceClicked() {
        viewModelScope.launch {
            navigation.navigate(RootKey.SetupInfoWarning)
        }
    }

    override fun onNextClicked(context: Context) {
        viewModelScope.launch {
            if(context.hasNotificationPermission()) {
                navigation.navigate(RootKey.SetupGesture)
            }else{
                navigation.navigate(RootKey.SetupNotifications)
            }
            if(!settings.internetAllowed.exists()) {
                navigation.navigate(RootKey.InternetPermission)
            }
        }
    }

    override fun onBackPressed(): Boolean {
        viewModelScope.launch {
            navigation.navigateBack()
        }
        return true
    }

}