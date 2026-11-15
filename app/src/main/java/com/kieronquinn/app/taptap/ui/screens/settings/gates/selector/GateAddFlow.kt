package com.kieronquinn.app.taptap.ui.screens.settings.gates.selector

import android.Manifest
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import com.kieronquinn.app.taptap.components.navigation.NavigationResults
import com.kieronquinn.app.taptap.components.navigation.NavigationResults.Key
import com.kieronquinn.app.taptap.models.gate.GateDataTypes
import com.kieronquinn.app.taptap.models.gate.GateRequirement
import com.kieronquinn.app.taptap.models.gate.TapTapGateDirectory
import com.kieronquinn.app.taptap.models.gate.TapTapUIGate
import com.kieronquinn.app.taptap.models.shared.SharedArgument
import com.kieronquinn.app.taptap.ui.screens.container.ContainerSharedViewModel
import com.kieronquinn.app.taptap.ui.screens.settings.shared.RequirementRequester
import com.kieronquinn.app.taptap.ui.screens.settings.shared.rememberRequirementRequester
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinActivityViewModel

class GateAddFlow(
    private val context: Context,
    private val scope: CoroutineScope,
    private val viewModel: SettingsGatesAddGenericViewModel,
    private val requester: RequirementRequester,
    private val results: NavigationResults,
    private val sharedViewModel: ContainerSharedViewModel
) {

    fun onGateClicked(gate: TapTapGateDirectory) {
        scope.launch { handleGate(gate) }
    }

    fun onChipClicked(requirement: GateRequirement.UserDisplayedGateRequirement) {
        sharedViewModel.showSnackbar(context.getText(requirement.desc))
    }

    suspend fun handleGate(
        gate: TapTapGateDirectory,
        extraData: String? = null,
        isReturningData: Boolean = false,
        isReturningRequirement: Boolean = false
    ) {
        if (!handleGateRequirement(gate, isReturningRequirement)) return
        if (gate.dataType != null && !viewModel.isGateDataSatisfied(context, gate.dataType, extraData ?: "")) {
            if (isReturningData) return
            if (gate.dataType == GateDataTypes.PACKAGE_NAME) viewModel.showAppPicker(gate)
            return
        }
        val description = viewModel.getFormattedDescriptionForGate(context, gate, extraData)
        //Index & id are handled later
        results.send(Key.GATE, TapTapUIGate(-1, gate, true, -1, extraData ?: "", description))
        viewModel.unwindToGates()
    }

    private suspend fun handleGateRequirement(gate: TapTapGateDirectory, isReturning: Boolean): Boolean {
        val requirements = gate.gateRequirement ?: return true
        return requirements.all { requirement ->
            when (requirement) {
                is GateRequirement.ReadPhoneStatePermission -> requester.requestPermission(Manifest.permission.READ_PHONE_STATE)
                is GateRequirement.Accessibility -> requester.requestAccessibilityService()
                is GateRequirement.Shizuku -> {
                    if (isReturning) return@all true
                    viewModel.showShizukuPermission(gate)
                    return false
                }
                is GateRequirement.UserDisplayedGateRequirement, is GateRequirement.Permission ->
                    throw NotImplementedError("Requirement $requirement is not implemented")
            }
        }
    }

}

@Composable
fun rememberGateAddFlow(viewModel: SettingsGatesAddGenericViewModel): GateAddFlow {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val requester = rememberRequirementRequester()
    val results = koinInject<NavigationResults>()
    val sharedViewModel = koinActivityViewModel<ContainerSharedViewModel>()
    val flow = remember(viewModel, requester) {
        GateAddFlow(context, scope, viewModel, requester, results, sharedViewModel)
    }
    LaunchedEffect(flow) {
        launch {
            results.receive<Pair<SharedArgument, String>>(Key.PACKAGE).collect { (argument, packageName) ->
                flow.handleGate(argument.gate ?: return@collect, packageName, isReturningRequirement = true)
            }
        }
        launch {
            results.receive<Pair<SharedArgument, Boolean>>(Key.SHIZUKU).collect { (argument, granted) ->
                if (granted) flow.handleGate(argument.gate ?: return@collect, isReturningRequirement = true)
            }
        }
    }
    return flow
}
