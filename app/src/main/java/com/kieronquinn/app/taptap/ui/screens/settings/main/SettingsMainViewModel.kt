package com.kieronquinn.app.taptap.ui.screens.settings.main

import com.kieronquinn.app.taptap.ui.navigation.*

import android.content.Context
import android.os.PowerManager
import androidx.lifecycle.viewModelScope
import com.kieronquinn.app.taptap.BuildConfig
import com.kieronquinn.app.taptap.components.navigation.ContainerNavigation
import com.kieronquinn.app.taptap.components.navigation.RootNavigation
import com.kieronquinn.app.taptap.components.settings.TapTapSettings
import com.kieronquinn.app.taptap.ui.screens.settings.generic.GenericSettingsViewModel
import kotlinx.coroutines.launch

abstract class SettingsMainViewModel: GenericSettingsViewModel() {

    abstract val batteryOptimisationDisabled: Boolean

    abstract fun checkInternetPermission()
    abstract fun onBatteryOptimisationClicked()
    abstract fun onDoubleTapActionsClicked()
    abstract fun onTripleTapActionsClicked()
    abstract fun onGatesClicked()
    abstract fun onFeedbackClicked()
    abstract fun onReRunSetupClicked()

}

class SettingsMainViewModelImpl(private val navigation: ContainerNavigation, private val rootNavigation: RootNavigation, private val settings: TapTapSettings, context: Context): SettingsMainViewModel() {

    private val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager

    override val batteryOptimisationDisabled: Boolean
        get() = powerManager.isIgnoringBatteryOptimizations(BuildConfig.APPLICATION_ID)

    override fun onBatteryOptimisationClicked() {
        viewModelScope.launch {
            navigation.navigate(ContainerKey.Battery)
        }
    }

    override fun onDoubleTapActionsClicked() {
        viewModelScope.launch {
            navigation.navigate(ContainerKey.ActionsDouble)
        }
    }

    override fun onTripleTapActionsClicked() {
        viewModelScope.launch {
            navigation.navigate(ContainerKey.ActionsTriple)
        }
    }

    override fun onGatesClicked() {
        viewModelScope.launch {
            navigation.navigate(ContainerKey.Gates)
        }
    }

    override fun onFeedbackClicked() {
        viewModelScope.launch {
            navigation.navigate(ContainerKey.Feedback)
        }
    }

    override fun onReRunSetupClicked() {
        viewModelScope.launch {
            rootNavigation.navigate(RootKey.SetupLanding)
        }
    }

    override fun checkInternetPermission() {
        viewModelScope.launch {
            if (!settings.internetAllowed.exists()) {
                rootNavigation.navigate(RootKey.InternetPermission)
            }
        }
    }

}