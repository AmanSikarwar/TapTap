package com.kieronquinn.app.taptap.ui.screens.settings.contributions

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.kieronquinn.app.taptap.R
import com.kieronquinn.app.taptap.ui.screens.settings.generic.GenericSettingsScreen
import com.kieronquinn.app.taptap.ui.screens.settings.generic.GenericSettingsViewModel
import com.kieronquinn.app.taptap.ui.screens.settings.generic.GenericSettingsViewModel.SettingsItem
import com.kieronquinn.app.taptap.utils.extensions.getResourceIdArray
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SettingsContributionsScreen(viewModel: SettingsContributionsViewModel = koinViewModel()) {
    val context = LocalContext.current
    val items = remember(viewModel) {
        listOf(
            SettingsItem.Text(
                icon = R.drawable.ic_gesture,
                titleRes = R.string.about_contributors_main,
                contentRes = R.string.about_contributors_main_content,
                linkClicked = viewModel::onLinkClicked
            ),
            SettingsItem.Text(
                icon = R.drawable.ic_community,
                titleRes = R.string.about_contributors_community,
                contentRes = R.string.about_contributors_community_content
            ),
            SettingsItem.Text(
                icon = R.drawable.ic_contributions_icons,
                titleRes = R.string.about_contributors_icons,
                contentRes = R.string.about_contributors_icons_content,
                linkClicked = viewModel::onLinkClicked
            ),
            SettingsItem.Header(R.string.about_translators),
            *getTranslatorsList(context).toTypedArray()
        )
    }
    GenericSettingsScreen(viewModel, items)
}


private fun getTranslatorsList(context: Context): List<SettingsItem.Text> {
    val headings = context.resources.getResourceIdArray(R.array.about_translators_headings)
    val content = context.resources.getResourceIdArray(R.array.about_translators_content)
    val flags = context.resources.getResourceIdArray(R.array.about_translators_flags)
    return headings.mapIndexed { index, resource ->
        SettingsItem.Text(
            icon = flags[index],
            titleRes = resource,
            contentRes = content[index]
        )
    }
}
