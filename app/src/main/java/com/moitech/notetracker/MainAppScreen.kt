package com.moitech.notetracker

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.moitech.notetracker.data.db.AivenDbHelper
import com.moitech.notetracker.data.local.LocalStorage
import com.moitech.notetracker.data.model.TrackedFunction
import com.moitech.notetracker.ui.components.BottomNavBar
import com.moitech.notetracker.ui.components.NavTab
import com.moitech.notetracker.ui.screens.*
import com.moitech.notetracker.ui.theme.CanvasBlack
import kotlinx.coroutines.launch

@Composable
fun MainAppScreen() {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var currentTab by remember { mutableStateOf(NavTab.HOME) }
    var selectedDetailItem by remember { mutableStateOf<TrackedFunction?>(null) }

    var functionsList by remember { mutableStateOf(LocalStorage.getCachedFunctions(context)) }
    var isLoading by remember { mutableStateOf(false) }

    fun refreshData() {
        coroutineScope.launch {
            isLoading = true
            val result = AivenDbHelper.fetchTrackedFunctions(context)
            if (result.isSuccess) {
                val freshData = result.getOrNull() ?: emptyList()
                functionsList = freshData
                LocalStorage.saveCachedFunctions(context, freshData)
                Toast.makeText(context, "Synced " + freshData.size + " functions from Aiven Cloud", Toast.LENGTH_SHORT).show()
            } else {
                val err = result.exceptionOrNull()?.localizedMessage ?: "Unknown error"
                if (functionsList.isNotEmpty()) {
                    Toast.makeText(context, "Cloud sync failed (" + err + "). Showing " + functionsList.size + " cached functions.", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(context, "Could not connect to Aiven Cloud: " + err, Toast.LENGTH_LONG).show()
                }
            }
            isLoading = false
        }
    }

    fun updateStage(item: TrackedFunction, stage: String, currentVal: Int) {
        val newVal = if (currentVal == 1) 0 else 1
        val updated = functionsList.map {
            if (it.systemName == item.systemName && it.funcId == item.funcId) {
                when (stage) {
                    "Typed" -> it.copy(typed = newVal)
                    "Print" -> it.copy(print = newVal)
                    "givenForBinding" -> it.copy(givenForBinding = newVal)
                    "givenToCustomer" -> it.copy(givenToCustomer = newVal)
                    else -> it
                }
            } else it
        }
        functionsList = updated
        LocalStorage.saveCachedFunctions(context, updated)

        coroutineScope.launch {
            val res = AivenDbHelper.updateStage(context, item.systemName, item.funcId, stage, newVal)
            if (res.isFailure) {
                // Kept in local cache
                Toast.makeText(context, "Updated locally (offline mode)", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun updateFunctionFull(
        item: TrackedFunction,
        phone: String,
        nickName: String,
        notes: String,
        typed: Int,
        print: Int,
        binding: Int,
        customer: Int
    ) {
        val updatedItem = item.copy(
            phoneNumber = phone,
            nickName = nickName,
            notes = notes,
            typed = typed,
            print = print,
            givenForBinding = binding,
            givenToCustomer = customer
        )
        val updated = functionsList.map {
            if (it.systemName == item.systemName && it.funcId == item.funcId) updatedItem else it
        }
        functionsList = updated
        LocalStorage.saveCachedFunctions(context, updated)
        selectedDetailItem = null

        coroutineScope.launch {
            val res = AivenDbHelper.updateFunctionFull(
                context,
                item.systemName,
                item.funcId,
                phone,
                nickName,
                notes,
                typed,
                print,
                binding,
                customer
            )
            if (res.isSuccess) {
                Toast.makeText(context, "Saved & synced to Aiven Cloud!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Saved locally (offline)", Toast.LENGTH_SHORT).show()
            }
        }
    }

    LaunchedEffect(Unit) {
        if (functionsList.isEmpty()) {
            refreshData()
        }
    }

    Scaffold(
        containerColor = CanvasBlack,
        bottomBar = {
            if (selectedDetailItem == null) {
                BottomNavBar(
                    currentTab = currentTab,
                    onTabSelected = { currentTab = it }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (selectedDetailItem != null) {
                NoteDetailScreen(
                    item = selectedDetailItem!!,
                    onBack = { selectedDetailItem = null },
                    onSaveUpdates = { phone, nick, notes, typed, print, binding, cust ->
                        updateFunctionFull(selectedDetailItem!!, phone, nick, notes, typed, print, binding, cust)
                    }
                )
            } else {
                when (currentTab) {
                    NavTab.HOME -> {
                        HomeScreen(
                            functions = functionsList,
                            isLoading = isLoading,
                            onRefresh = { refreshData() },
                            onFunctionClick = { selectedDetailItem = it },
                            onToggleStage = { func, stage, currentVal ->
                                updateStage(func, stage, currentVal)
                            }
                        )
                    }
                    NavTab.NOTES -> {
                        NotesScreen(
                            functions = functionsList,
                            onFunctionClick = { selectedDetailItem = it },
                            onToggleStage = { func, stage, currentVal ->
                                updateStage(func, stage, currentVal)
                            },
                            onAddNewFunction = {
                                Toast.makeText(context, "Functions sync automatically from connected desktop PCs", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                    NavTab.CLIENTS -> {
                        ClientsScreen(
                            functions = functionsList
                        )
                    }
                    NavTab.SETTINGS -> {
                        SettingsScreen(
                            functions = functionsList,
                            onSaveNicknames = { sys, nick ->
                                coroutineScope.launch {
                                    AivenDbHelper.updatePcNickname(context, sys, nick)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}
