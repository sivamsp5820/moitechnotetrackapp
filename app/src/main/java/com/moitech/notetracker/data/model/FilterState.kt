package com.moitech.notetracker.data.model

enum class StageFilter(val label: String) {
    ALL("All"),
    PENDING("Pending"),
    TYPED("Typed"),
    PRINT("Print"),
    BINDING("Binding"),
    DELIVERED("Delivered")
}

enum class SortOption(val label: String) {
    DATE_DESC("Newest First"),
    DATE_ASC("Oldest First"),
    NAME_ASC("Function Name (A-Z)"),
    PROGRESS_ASC("Pending First")
}

data class FilterState(
    val searchQuery: String = "",
    val selectedSystem: String? = null,
    val stageFilter: StageFilter = StageFilter.ALL,
    val sortOption: SortOption = SortOption.DATE_DESC
)
