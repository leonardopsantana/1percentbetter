

@file:OptIn(ExperimentalMaterial3Api::class)

package com.onepercentbetter.core.designsystem.component

import androidx.annotation.StringRes
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.onepercentbetter.core.designsystem.icon.OPBIcons
import com.onepercentbetter.core.designsystem.theme.OPBTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OPBTopAppBar(
    @StringRes titleRes: Int,
    actionIcon: ImageVector,
    actionIconContentDescription: String,
    modifier: Modifier = Modifier,
    colors: TopAppBarColors = TopAppBarDefaults.centerAlignedTopAppBarColors(),
    onActionClick: () -> Unit = {},
) {
    CenterAlignedTopAppBar(
        title = { Text(text = stringResource(id = titleRes)) },
//        navigationIcon = {
//            IconButton(onClick = onNavigationClick) {
//                Icon(
//                    imageVector = navigationIcon,
//                    contentDescription = navigationIconContentDescription,
//                    tint = MaterialTheme.colorScheme.onSurface,
//                )
//            }
//        },
        actions = {
            IconButton(onClick = onActionClick) {
                Icon(
                    imageVector = actionIcon,
                    contentDescription = actionIconContentDescription,
                    tint = MaterialTheme.colorScheme.onSurface,
                )
            }
        },
        colors = colors,
        modifier = modifier.testTag("OPBTopAppBar"),
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview("Top App Bar")
@Composable
private fun OPBTopAppBarPreview() {
    OPBTheme {
        OPBTopAppBar(
            titleRes = android.R.string.untitled,
            actionIcon = OPBIcons.MoreVert,
            actionIconContentDescription = "Action icon",
        )
    }
}
