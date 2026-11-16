package com.kieronquinn.app.taptap.ui.screens.container

import com.kieronquinn.app.taptap.ui.navigation.*

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kieronquinn.app.taptap.R
import com.kieronquinn.app.taptap.components.navigation.ContainerNavigation
import com.kieronquinn.app.taptap.components.settings.SETTINGS_VERSION
import com.kieronquinn.app.taptap.components.settings.TapTapSettings
import com.kieronquinn.app.taptap.repositories.update.UpdateRepository
import com.kieronquinn.app.taptap.utils.extensions.getColumbusSettingAsFlow
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

abstract class ContainerViewModel: ViewModel() {

    abstract val showUpdateSnackbar: StateFlow<Boolean>

    abstract fun writeSettingsVersion()
    abstract fun setCanShowSnackbar(showSnackbar: Boolean)
    abstract fun onNavigationItemClicked(key: ContainerKey)
    abstract fun onBackPressed()
    abstract fun onUpdateClicked()
    abstract fun onUpdateDismissed()
    abstract fun phoenix()

}

class ContainerViewModelImpl(context: Context, private val settings: TapTapSettings, private val navigation: ContainerNavigation, updateRepository: UpdateRepository): ContainerViewModel() {

    private val canShowSnackbar = MutableStateFlow(false)
    private val hasDismissedSnackbar = MutableStateFlow(false)

    private val gitHubUpdate = flow {
        emit(updateRepository.getUpdate())
    }.stateIn(viewModelScope, SharingStarted.Eagerly, null)

    override val showUpdateSnackbar = combine(canShowSnackbar, gitHubUpdate, hasDismissedSnackbar){ canShow, update, dismissed ->
        canShow && update != null && !dismissed
    }.stateIn(viewModelScope, SharingStarted.Eagerly, false)

    override fun onNavigationItemClicked(key: ContainerKey) {
        viewModelScope.launch {
            navigation.replace(key)
        }
    }

    override fun onBackPressed() {
        viewModelScope.launch {
            navigation.navigateBack()
        }
    }

    override fun setCanShowSnackbar(showSnackbar: Boolean) {
        viewModelScope.launch {
            canShowSnackbar.emit(showSnackbar)
        }
    }

    override fun onUpdateClicked() {
        val release = gitHubUpdate.value ?: return
        viewModelScope.launch {
            navigation.navigate(ContainerKey.Update(release))
        }
    }

    override fun onUpdateDismissed() {
        viewModelScope.launch {
            hasDismissedSnackbar.emit(true)
        }
    }

    override fun phoenix() {
        viewModelScope.launch {
            navigation.phoenix()
        }
    }

    override fun writeSettingsVersion() {
        viewModelScope.launch {
            settings.settingsVersion.set(SETTINGS_VERSION)
        }
    }

}