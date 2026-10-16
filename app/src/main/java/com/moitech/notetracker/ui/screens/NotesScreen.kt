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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FilterList
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
import com.moitech.notetracker.data.model.SortOption
import com.moitech.notetracker.data.model.StageFilter
import com.moitech.notetracker.data.model.TrackedFunction
import com.moitech.notetracker.ui.components.FilterBottomSheet
import com.moitech.notetracker.ui.components.NoteCard
import com.moitech.notetracker.ui.theme.*

@Composable
fun NotesScreen(
    functions: List<TrackedFunction>,
    onFunctionClick: (TrackedFunction) -> Unit,
    onToggleStage: (func: TrackedFunction, stage: String, currentVal: Int) -> Unit,
    onAddNewFunction: () -> Unit,
    modifier: Modifier = Modifier
) {
    var filterState by remember { mutableStateOf(FilterState()) }
    var showFilterSheet by remember { mutableStateOf(false) }

    val distinctSystems = remember(functions) {
        functions.map { it.displaySystem }.distinct()
    }

    val filteredList = remember(functions, filterState) {
        var result = functions.filter { item ->
            val matchQuery = filterState.searchQuery.isBlank() ||
                item.funcName.contains(filterState.searchQuery, ignoreCase = true) ||
                (item.phoneNumber?.contains(filterState.searchQuery) == true) ||
                item.displaySystem.contains(filterState.searchQuery, ignoreCase = true)

            val matchSystem = filterState.selectedSystem == null || item.displaySystem == filterState.selectedSystem

            val matchStage = when (filterState.stageFilter) {
                StageFilter.ALL -> true
                StageFilter.PENDING -> !item.isFullyCompleted
                StageFilter.TYPED -> item.isTyped
                StageFilter.PRINT -> item.isPrint
                StageFilter.BINDING -> item.isBinding
                StageFilter.DELIVERED -> item.isDelivered
            }

            matchQuery && matchSystem && matchStage
        }

        when (filterState.sortOption) {
            SortOption.DATE_DESC -> result.sortedByDescending { it.funcDate }
            SortOption.DATE_ASC -> result.sortedBy { it.funcDate }
            SortOption.NAME_ASC -> result.sortedBy { it.funcName.lowercase() }
            SortOption.PROGRESS_ASC -> result.sortedBy { it.completedStagesCount }
        }
    }

    Scaffold(
        containerColor = CanvasBlack,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddNewFunction,
                containerColor = TextWhite,
                contentColor = CanvasBlack,
                shape = CircleShape,
                modifier = Modifier.padding(bottom = 60.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Function")
            }
        },
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
                    Text(
                        text = "NOTES & TRACKING",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite,
                        letterSpacing = 1.sp
                    )

                    Text(
                        text = filteredList.size.toString() + " items",
                        fontSize = 12.sp,
                        color = TextMuted
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

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
                            Text("Filter by name, phone, PC...", color = TextDimmed, fontSize = 13.sp)
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

                Spacer(modifier = Modifier.height(10.dp))

                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(StageFilter.values()) { stage ->
                        val isSelected = filterState.stageFilter == stage
                        val bg = if (isSelected) PillActive else SurfaceDark
                        val text = if (isSelected) CanvasBlack else TextMuted
                        val border = if (isSelected) TextWhite else BorderDark

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(bg)
                                .border(1.dp, border, RoundedCornerShape(10.dp))
                                .clickable { filterState = filterState.copy(stageFilter = stage) }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = stage.label,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = text
                            )
                        }
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
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
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
