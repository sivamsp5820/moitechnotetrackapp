package com.moitech.notetracker.data.model

data class ClientItem(
    val phoneNumber: String,
    val customerName: String,
    val totalFunctions: Int,
    val pendingCount: Int,
    val completedCount: Int,
    val lastFunctionDate: String,
    val systemName: String
)
