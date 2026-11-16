package com.kieronquinn.app.taptap.ui.screens.reachability

import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kieronquinn.app.taptap.R

@Composable
fun ReachabilityScreen(viewModel: ReachabilityViewModel) {
    val context = LocalContext.current
    val leftHanded by viewModel.isLeftHanded.collectAsState(false)
    LaunchedEffect(viewModel) {
        viewModel.sendStartAction()
        if (!viewModel.getHasLeftHandedSet()) {
            Toast.makeText(context, R.string.reachability_left_handed_info, Toast.LENGTH_LONG).show()
        }
    }
    val buttonSize = dimensionResource(R.dimen.reachability_button_size)
    Surface(Modifier.fillMaxSize()) {
        BoxWithConstraints(Modifier.fillMaxSize()) {
            if (maxHeight < buttonSize + 32.dp) return@BoxWithConstraints
            Row(
                Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .wrapContentSize(if (leftHanded) Alignment.BottomStart else Alignment.BottomEnd),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ReachabilityButton(R.drawable.ic_action_notifications, buttonSize, viewModel::onNotificationsClicked, viewModel::onNotificationsLongClicked)
                ReachabilityButton(R.drawable.ic_action_quick_settings, buttonSize, viewModel::onQuickSettingsClicked)
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ReachabilityButton(icon: Int, size: Dp, onClick: () -> Unit, onLongClick: (() -> Unit)? = null) {
    Surface(
        shape = CircleShape,
        color = MaterialTheme.colorScheme.primaryContainer,
        modifier = Modifier.size(size)
    ) {
        Icon(
            painterResource(icon),
            null,
            Modifier.combinedClickable(onClick = onClick, onLongClick = onLongClick).padding(size / 4),
            tint = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}
