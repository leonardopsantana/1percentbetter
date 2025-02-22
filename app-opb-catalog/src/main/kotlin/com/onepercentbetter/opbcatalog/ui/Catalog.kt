

package com.onepercentbetter.opbcatalog.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.onepercentbetter.core.designsystem.component.OPBButton
import com.onepercentbetter.core.designsystem.component.OPBFilterChip
import com.onepercentbetter.core.designsystem.component.OPBIconToggleButton
import com.onepercentbetter.core.designsystem.component.OPBNavigationBar
import com.onepercentbetter.core.designsystem.component.OPBNavigationBarItem
import com.onepercentbetter.core.designsystem.component.OPBOutlinedButton
import com.onepercentbetter.core.designsystem.component.OPBTab
import com.onepercentbetter.core.designsystem.component.OPBTabRow
import com.onepercentbetter.core.designsystem.component.OPBTextButton
import com.onepercentbetter.core.designsystem.component.OPBTopicTag
import com.onepercentbetter.core.designsystem.component.OPBViewToggleButton
import com.onepercentbetter.core.designsystem.icon.OPBIcons
import com.onepercentbetter.core.designsystem.theme.OPBTheme

/**
 * OPB component catalog.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun OpbCatalog() {
    OPBTheme {
        Surface {
            val contentPadding = WindowInsets
                .systemBars
                .add(WindowInsets(left = 16.dp, top = 16.dp, right = 16.dp, bottom = 16.dp))
                .asPaddingValues()
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = contentPadding,
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                item {
                    Text(
                        text = "OPB Catalog",
                        style = MaterialTheme.typography.headlineSmall,
                    )
                }
                item { Text("Buttons", Modifier.padding(top = 16.dp)) }
                item {
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        OPBButton(onClick = {}) {
                            Text(text = "Enabled")
                        }
                        OPBOutlinedButton(onClick = {}) {
                            Text(text = "Enabled")
                        }
                        OPBTextButton(onClick = {}) {
                            Text(text = "Enabled")
                        }
                    }
                }
                item { Text("Disabled buttons", Modifier.padding(top = 16.dp)) }
                item {
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        OPBButton(
                            onClick = {},
                            enabled = false,
                        ) {
                            Text(text = "Disabled")
                        }
                        OPBOutlinedButton(
                            onClick = {},
                            enabled = false,
                        ) {
                            Text(text = "Disabled")
                        }
                        OPBTextButton(
                            onClick = {},
                            enabled = false,
                        ) {
                            Text(text = "Disabled")
                        }
                    }
                }
                item { Text("Buttons with leading icons", Modifier.padding(top = 16.dp)) }
                item {
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        OPBButton(
                            onClick = {},
                            text = { Text(text = "Enabled") },
                            leadingIcon = {
                                Icon(imageVector = OPBIcons.Add, contentDescription = null)
                            },
                        )
                        OPBOutlinedButton(
                            onClick = {},
                            text = { Text(text = "Enabled") },
                            leadingIcon = {
                                Icon(imageVector = OPBIcons.Add, contentDescription = null)
                            },
                        )
                        OPBTextButton(
                            onClick = {},
                            text = { Text(text = "Enabled") },
                            leadingIcon = {
                                Icon(imageVector = OPBIcons.Add, contentDescription = null)
                            },
                        )
                    }
                }
                item { Text("Disabled buttons with leading icons", Modifier.padding(top = 16.dp)) }
                item {
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        OPBButton(
                            onClick = {},
                            enabled = false,
                            text = { Text(text = "Disabled") },
                            leadingIcon = {
                                Icon(imageVector = OPBIcons.Add, contentDescription = null)
                            },
                        )
                        OPBOutlinedButton(
                            onClick = {},
                            enabled = false,
                            text = { Text(text = "Disabled") },
                            leadingIcon = {
                                Icon(imageVector = OPBIcons.Add, contentDescription = null)
                            },
                        )
                        OPBTextButton(
                            onClick = {},
                            enabled = false,
                            text = { Text(text = "Disabled") },
                            leadingIcon = {
                                Icon(imageVector = OPBIcons.Add, contentDescription = null)
                            },
                        )
                    }
                }
                item { Text("Dropdown menus", Modifier.padding(top = 16.dp)) }
                item { Text("Chips", Modifier.padding(top = 16.dp)) }
                item {
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        var firstChecked by rememberSaveable { mutableStateOf(false) }
                        OPBFilterChip(
                            selected = firstChecked,
                            onSelectedChange = { checked -> firstChecked = checked },
                            label = { Text(text = "Enabled") },
                        )
                        var secondChecked by rememberSaveable { mutableStateOf(true) }
                        OPBFilterChip(
                            selected = secondChecked,
                            onSelectedChange = { checked -> secondChecked = checked },
                            label = { Text(text = "Enabled") },
                        )
                        OPBFilterChip(
                            selected = false,
                            onSelectedChange = {},
                            enabled = false,
                            label = { Text(text = "Disabled") },
                        )
                        OPBFilterChip(
                            selected = true,
                            onSelectedChange = {},
                            enabled = false,
                            label = { Text(text = "Disabled") },
                        )
                    }
                }
                item { Text("Icon buttons", Modifier.padding(top = 16.dp)) }
                item {
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        var firstChecked by rememberSaveable { mutableStateOf(false) }
                        OPBIconToggleButton(
                            checked = firstChecked,
                            onCheckedChange = { checked -> firstChecked = checked },
                            icon = {
                                Icon(
                                    imageVector = OPBIcons.BookmarkBorder,
                                    contentDescription = null,
                                )
                            },
                            checkedIcon = {
                                Icon(
                                    imageVector = OPBIcons.Bookmark,
                                    contentDescription = null,
                                )
                            },
                        )
                        var secondChecked by rememberSaveable { mutableStateOf(true) }
                        OPBIconToggleButton(
                            checked = secondChecked,
                            onCheckedChange = { checked -> secondChecked = checked },
                            icon = {
                                Icon(
                                    imageVector = OPBIcons.BookmarkBorder,
                                    contentDescription = null,
                                )
                            },
                            checkedIcon = {
                                Icon(
                                    imageVector = OPBIcons.Bookmark,
                                    contentDescription = null,
                                )
                            },
                        )
                        OPBIconToggleButton(
                            checked = false,
                            onCheckedChange = {},
                            icon = {
                                Icon(
                                    imageVector = OPBIcons.BookmarkBorder,
                                    contentDescription = null,
                                )
                            },
                            checkedIcon = {
                                Icon(
                                    imageVector = OPBIcons.Bookmark,
                                    contentDescription = null,
                                )
                            },
                            enabled = false,
                        )
                        OPBIconToggleButton(
                            checked = true,
                            onCheckedChange = {},
                            icon = {
                                Icon(
                                    imageVector = OPBIcons.BookmarkBorder,
                                    contentDescription = null,
                                )
                            },
                            checkedIcon = {
                                Icon(
                                    imageVector = OPBIcons.Bookmark,
                                    contentDescription = null,
                                )
                            },
                            enabled = false,
                        )
                    }
                }
                item { Text("View toggle", Modifier.padding(top = 16.dp)) }
                item {
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        var firstExpanded by rememberSaveable { mutableStateOf(false) }
                        OPBViewToggleButton(
                            expanded = firstExpanded,
                            onExpandedChange = { expanded -> firstExpanded = expanded },
                            compactText = { Text(text = "Compact view") },
                            expandedText = { Text(text = "Expanded view") },
                        )
                        var secondExpanded by rememberSaveable { mutableStateOf(true) }
                        OPBViewToggleButton(
                            expanded = secondExpanded,
                            onExpandedChange = { expanded -> secondExpanded = expanded },
                            compactText = { Text(text = "Compact view") },
                            expandedText = { Text(text = "Expanded view") },
                        )
                        OPBViewToggleButton(
                            expanded = false,
                            onExpandedChange = {},
                            compactText = { Text(text = "Disabled") },
                            expandedText = { Text(text = "Disabled") },
                            enabled = false,
                        )
                    }
                }
                item { Text("Tags", Modifier.padding(top = 16.dp)) }
                item {
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        OPBTopicTag(
                            followed = true,
                            onClick = {},
                            text = { Text(text = "Topic 1".uppercase()) },
                        )
                        OPBTopicTag(
                            followed = false,
                            onClick = {},
                            text = { Text(text = "Topic 2".uppercase()) },
                        )
                        OPBTopicTag(
                            followed = false,
                            onClick = {},
                            text = { Text(text = "Disabled".uppercase()) },
                            enabled = false,
                        )
                    }
                }
                item { Text("Tabs", Modifier.padding(top = 16.dp)) }
                item {
                    var selectedTabIndex by rememberSaveable { mutableIntStateOf(0) }
                    val titles = listOf("Topics", "People")
                    OPBTabRow(selectedTabIndex = selectedTabIndex) {
                        titles.forEachIndexed { index, title ->
                            OPBTab(
                                selected = selectedTabIndex == index,
                                onClick = { selectedTabIndex = index },
                                text = { Text(text = title) },
                            )
                        }
                    }
                }
                item { Text("Navigation", Modifier.padding(top = 16.dp)) }
                item {
                    var selectedItem by rememberSaveable { mutableIntStateOf(0) }
                    val items = listOf("For you", "Saved", "Interests")
                    val icons = listOf(
                        OPBIcons.UpcomingBorder,
                        OPBIcons.BookmarksBorder,
                        OPBIcons.Grid3x3,
                    )
                    val selectedIcons = listOf(
                        OPBIcons.Upcoming,
                        OPBIcons.Bookmarks,
                        OPBIcons.Grid3x3,
                    )
                    OPBNavigationBar {
                        items.forEachIndexed { index, item ->
                            OPBNavigationBarItem(
                                icon = {
                                    Icon(
                                        imageVector = icons[index],
                                        contentDescription = item,
                                    )
                                },
                                selectedIcon = {
                                    Icon(
                                        imageVector = selectedIcons[index],
                                        contentDescription = item,
                                    )
                                },
                                label = { Text(item) },
                                selected = selectedItem == index,
                                onClick = { selectedItem = index },
                            )
                        }
                    }
                }
            }
        }
    }
}
