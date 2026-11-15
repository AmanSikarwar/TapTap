package com.kieronquinn.app.taptap.ui.screens.settings.gates.selector

import com.kieronquinn.app.taptap.ui.navigation.*

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kieronquinn.app.taptap.R
import com.kieronquinn.app.taptap.components.navigation.ContainerNavigation
import com.kieronquinn.app.taptap.models.gate.GateDataTypes
import com.kieronquinn.app.taptap.models.gate.GateSupportedRequirement
import com.kieronquinn.app.taptap.models.gate.TapTapGateDirectory
import com.kieronquinn.app.taptap.models.shared.SharedArgument
import com.kieronquinn.app.taptap.repositories.gates.GatesRepository
import kotlinx.coroutines.launch

abstract class SettingsGatesAddGenericViewModel: ViewModel() {

    abstract fun showShizukuPermission(gate: TapTapGateDirectory)
    abstract fun showAppPicker(gate: TapTapGateDirectory)
    abstract fun unwindToGates()
    abstract fun getFormattedDescriptionForGate(context: Context, gate: TapTapGateDirectory, data: String?): CharSequence
    abstract fun isGateDataSatisfied(context: Context, data: GateDataTypes, extraData: String): Boolean
    abstract fun getGateSupportedRequirement(context: Context, gate: TapTapGateDirectory): GateSupportedRequirement?

}

abstract class SettingsGatesAddGenericViewModelImpl(private val navigation: ContainerNavigation, private val gatesRepository: GatesRepository): SettingsGatesAddGenericViewModel() {

    override fun unwindToGates() {
        viewModelScope.launch {
            navigation.navigateUpTo(ContainerKey.GatesAddCategory::class, true)
        }
    }

    override fun showShizukuPermission(gate: TapTapGateDirectory) {
        viewModelScope.launch {
            navigation.navigate(ContainerKey.ShizukuPermission(SharedArgument(gate = gate)))
        }
    }

    override fun showAppPicker(gate: TapTapGateDirectory) {
        viewModelScope.launch {
            navigation.navigate(ContainerKey.PackagePicker(SharedArgument(gate = gate), R.string.action_launch_app, true))
        }
    }

    override fun getGateSupportedRequirement(context: Context, gate: TapTapGateDirectory): GateSupportedRequirement? {
        return gatesRepository.getGateSupportedRequirement(context, gate)
    }

    override fun getFormattedDescriptionForGate(
        context: Context,
        gate: TapTapGateDirectory,
        data: String?
    ): CharSequence {
        return gatesRepository.getFormattedDescriptionForGate(context, gate, data)
    }

    override fun isGateDataSatisfied(
        context: Context,
        data: GateDataTypes,
        extraData: String
    ): Boolean {
        return gatesRepository.isGateDataSatisfied(context, data, extraData)
    }

}