package com.moitech.notetracker.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.moitech.notetracker.data.model.FilterState
import com.moitech.notetracker.data.model.TrackedFunction
import com.moitech.notetracker.ui.components.FilterBottomSheet
import com.moitech.notetracker.ui.components.KPICard
import com.moitech.notetracker.ui.components.NoteCard
import com.moitech.notetracker.ui.theme.*

@Composable
fun HomeScreen(
    functions: List<TrackedFunction>,
    isLoading: Boolean,
    onRefresh: () -> Unit,
    onFunctionClick: (TrackedFunction) -> Unit,
    onToggleStage: (func: TrackedFunction, stage: String, currentVal: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var filterState by remember { mutableStateOf(FilterState()) }
    var showFilterSheet by remember { mutableStateOf(false) }

    val distinctSystems = remember(functions) {
        functions.map { it.displaySystem }.distinct()
    }

    val totalCount = functions.size
    val completedCount = functions.count { it.isFullyCompleted }
    val pendingCount = totalCount - completedCount
    val printCount = functions.count { it.isPrint }
    val bindingCount = functions.count { it.isBinding }

    val filteredList = remember(functions, filterState) {
        functions.filter { item ->
            val matchQuery = filterState.searchQuery.isBlank() ||
                item.funcName.contains(filterState.searchQuery, ignoreCase = true) ||
                (item.phoneNumber?.contains(filterState.searchQuery) == true) ||
                item.displaySystem.contains(filterState.searchQuery, ignoreCase = true)

            val matchSystem = filterState.selectedSystem == null || item.displaySystem == filterState.selectedSystem
            matchQuery && matchSystem
        }
    }

    Scaffold(
        containerColor = CanvasBlack,
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CanvasBlack)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "MOI NOTE TRACKER",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextWhite,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Multi-PC Function Synchronizer",
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        IconButton(
                            onClick = onRefresh,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(SurfaceDark)
                                .border(1.dp, BorderDark, CircleShape)
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    color = TextWhite,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "Refresh",
                                    tint = TextWhite,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = filterState.searchQuery,
                        onValueChange = { filterState = filterState.copy(searchQuery = it) },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        placeholder = {
                            Text("Search function, phone, PC...", color = TextDimmed, fontSize = 13.sp)
                        },
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = null, tint = TextMuted, modifier = Modifier.size(18.dp))
                        },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = SurfaceDark,
                            unfocusedContainerColor = SurfaceDark,
                            focusedBorderColor = TextWhite,
                            unfocusedBorderColor = BorderDark,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(SurfaceDark)
                            .border(1.dp, BorderDark, RoundedCornerShape(12.dp))
                            .clickable { showFilterSheet = true },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = "Filter",
                            tint = TextWhite,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    KPICard(
                        title = "Total Functions",
                        value = totalCount.toString(),
                        subtitle = "Synced from PCs",
                        modifier = Modifier.weight(1f)
                    )
                    KPICard(
                        title = "Completed",
                        value = completedCount.toString(),
                        subtitle = pendingCount.toString() + " pending",
                        progress = if (totalCount > 0) completedCount.toFloat() / totalCount else 0f,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    KPICard(
                        title = "Printed",
                        value = printCount.toString(),
                        subtitle = "Ready for Binding",
                        modifier = Modifier.weight(1f)
                    )
                    KPICard(
                        title = "In Binding",
                        value = bindingCount.toString(),
                        subtitle = "Ready for Customer",
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                Text(
                    text = "CONNECTED SYSTEMS",
                    fontSize = 11.sp,
                    color = TextMuted,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    item {
                        val isAll = filterState.selectedSystem == null
                        SystemChip(
                            label = "All Systems (" + totalCount + ")",
                            isSelected = isAll,
                            onClick = { filterState = filterState.copy(selectedSystem = null) }
                        )
                    }
                    items(distinctSystems) { sys ->
                        val count = functions.count { it.displaySystem == sys }
                        val isSelected = filterState.selectedSystem == sys
                        SystemChip(
                            label = sys + " (" + count + ")",
                            isSelected = isSelected,
                            onClick = { filterState = filterState.copy(selectedSystem = sys) }
                        )
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "FUNCTIONS (" + filteredList.size + ")",
                        fontSize = 12.sp,
                        color = TextMuted,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }
            }

            items(filteredList, key = { it.systemName + "_" + it.funcId }) { item ->
                NoteCard(
                    item = item,
                    onCardClick = { onFunctionClick(item) },
                    onToggleStage = { stage, currentVal ->
                        onToggleStage(item, stage, currentVal)
                    }
                )
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }

        if (showFilterSheet) {
            FilterBottomSheet(
                filterState = filterState,
                availableSystems = distinctSystems,
                onFilterChange = { filterState = it },
                onDismiss = { showFilterSheet = false }
            )
        }
    }
}

@Composable
fun SystemChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val bg = if (isSelected) PillActive else SurfaceDark
    val text = if (isSelected) CanvasBlack else TextMuted
    val border = if (isSelected) TextWhite else BorderDark

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(bg)
            .border(1.dp, border, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 7.dp)
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = text
        )
    }
}
