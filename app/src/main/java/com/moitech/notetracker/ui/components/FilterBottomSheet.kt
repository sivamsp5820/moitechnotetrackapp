package com.moitech.notetracker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.moitech.notetracker.data.model.FilterState
import com.moitech.notetracker.data.model.SortOption
import com.moitech.notetracker.data.model.StageFilter
import com.moitech.notetracker.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterBottomSheet(
    filterState: FilterState,
    availableSystems: List<String>,
    onFilterChange: (FilterState) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = SurfaceDark,
        scrimColor = CanvasBlack.copy(alpha = 0.7f),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            Text(
                text = "FILTER & SORT",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextWhite,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Section: PC System Filter
            Text(
                text = "PC System",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextMuted
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    val isAll = filterState.selectedSystem == null
                    SelectChip(
                        label = "All Systems",
                        isSelected = isAll,
                        onClick = { onFilterChange(filterState.copy(selectedSystem = null)) }
                    )
                }
                items(availableSystems) { sys ->
                    val isSelected = filterState.selectedSystem == sys
                    SelectChip(
                        label = sys,
                        isSelected = isSelected,
                        onClick = { onFilterChange(filterState.copy(selectedSystem = sys)) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Section: Stage Filter
            Text(
                text = "Status Stage",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextMuted
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(StageFilter.values()) { stage ->
                    SelectChip(
                        label = stage.label,
                        isSelected = filterState.stageFilter == stage,
                        onClick = { onFilterChange(filterState.copy(stageFilter = stage)) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Section: Sort Order
            Text(
                text = "Sort Order",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextMuted
            )
            Spacer(modifier = Modifier.height(8.dp))
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                SortOption.values().forEach { sort ->
                    val isSelected = filterState.sortOption == sort
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) SurfaceHighlight else SurfaceDark)
                            .border(1.dp, if (isSelected) TextWhite else BorderDark, RoundedCornerShape(12.dp))
                            .clickable { onFilterChange(filterState.copy(sortOption = sort)) }
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = sort.label,
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) TextWhite else TextMuted
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Reset & Done buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        onFilterChange(FilterState())
                        onDismiss()
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = TextMuted
                    ),
                    border = ButtonDefaults.outlinedButtonBorder.copy(width = 1.dp)
                ) {
                    Text("Reset")
                }

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TextWhite,
                        contentColor = CanvasBlack
                    )
                ) {
                    Text("Apply", fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun SelectChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val bg = if (isSelected) PillActive else SurfaceHighlight
    val textColor = if (isSelected) CanvasBlack else TextMuted
    val border = if (isSelected) TextWhite else BorderDark

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(bg)
            .border(1.dp, border, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = textColor
        )
    }
}
