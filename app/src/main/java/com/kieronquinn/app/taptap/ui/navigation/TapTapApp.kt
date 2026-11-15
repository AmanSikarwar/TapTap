package com.kieronquinn.app.taptap.ui.navigation

import androidx.activity.compose.LocalActivity
import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.scene.DialogSceneStrategy
import androidx.navigation3.ui.NavDisplay
import com.kieronquinn.app.taptap.R
import com.kieronquinn.app.taptap.components.navigation.RootNavigation
import com.kieronquinn.app.taptap.ui.compose.SheetMessage
import com.kieronquinn.app.taptap.ui.screens.container.ContainerScreen
import com.kieronquinn.app.taptap.ui.screens.decision.DecisionViewModel
import com.kieronquinn.app.taptap.ui.screens.disablecolumbus.DisableColumbusScreen
import com.kieronquinn.app.taptap.ui.screens.nogyro.NoGyroscopeScreen
import com.kieronquinn.app.taptap.ui.screens.root.RootSharedViewModel
import com.kieronquinn.app.taptap.ui.screens.setup.base.BaseSetupViewModel
import com.kieronquinn.app.taptap.ui.screens.setup.complete.SetupCompleteScreen
import com.kieronquinn.app.taptap.ui.screens.setup.complete.SetupCompleteViewModel
import com.kieronquinn.app.taptap.ui.screens.setup.gesture.SetupGestureScreen
import com.kieronquinn.app.taptap.ui.screens.setup.info.SetupInfoScreen
import com.kieronquinn.app.taptap.ui.screens.setup.info.SetupInfoViewModel
import com.kieronquinn.app.taptap.ui.screens.setup.landing.SetupLandingScreen
import com.kieronquinn.app.taptap.ui.screens.setup.notifications.SetupNotificationsScreen
import com.kieronquinn.app.taptap.ui.screens.setup.notifications.SetupNotificationsViewModel
import com.kieronquinn.app.taptap.ui.screens.setup.upgrade.SetupUpgradeScreen
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinActivityViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
private fun SetupBack(viewModel: BaseSetupViewModel) {
    BackHandler { viewModel.onBackPressed() }
}

@Composable
fun TapTapApp() {
    val navigation = koinInject<RootNavigation>()
    val scope = rememberCoroutineScope()
    NavigationEffects(navigation)
    NavDisplay(
        backStack = navigation.backStack,
        onBack = { scope.launch { navigation.navigateBack() } },
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        sceneStrategies = listOf(DialogSceneStrategy(), BottomSheetSceneStrategy()),
        entryProvider = entryProvider {
            entry<RootKey.Decision> {
                val viewModel = koinViewModel<DecisionViewModel>()
                val root = koinActivityViewModel<RootSharedViewModel>()
                LaunchedEffect(viewModel) { viewModel.decisionMade.collect { root.postDecisionMade() } }
            }
            entry<RootKey.SetupLanding> { SetupLandingScreen(koinViewModel()) }
            entry<RootKey.SetupInfo> {
                val viewModel = koinViewModel<SetupInfoViewModel>()
                SetupBack(viewModel)
                SetupInfoScreen(viewModel)
            }
            entry<RootKey.SetupInfoWarning>(metadata = BottomSheetSceneStrategy.bottomSheet()) {
                SheetMessage(
                    title = stringResource(R.string.setup_foss_info_bs_title),
                    text = stringResource(R.string.setup_foss_info_bs_content),
                    positive = stringResource(android.R.string.ok),
                    onPositive = back(navigation)
                )
            }
            entry<RootKey.SetupNotifications> {
                val viewModel = koinViewModel<SetupNotificationsViewModel>()
                SetupBack(viewModel)
                SetupNotificationsScreen(viewModel)
            }
            entry<RootKey.SetupGesture> { SetupGestureScreen() }
            entry<RootKey.SetupComplete> {
                val viewModel = koinViewModel<SetupCompleteViewModel>()
                SetupBack(viewModel)
                SetupCompleteScreen(viewModel)
            }
            entry<RootKey.SetupUpgrade> { SetupUpgradeScreen(koinViewModel()) }
            entry<RootKey.NoGyroscope> {
                val activity = LocalActivity.current!!
                NoGyroscopeScreen(onClose = activity::finish)
            }
            entry<RootKey.DisableColumbus> { DisableColumbusScreen(koinViewModel()) }
            entry<RootKey.InternetPermission>(metadata = NonDismissableDialog) { InternetPermissionEntry(navigation) }
            entry<RootKey.Container> { ContainerScreen() }
        }
    )
}
