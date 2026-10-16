package com.moitech.notetracker

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.moitech.notetracker.data.api.RetrofitClient
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
            try {
                val api = RetrofitClient.getService(context)
                val response = api.getTrackedFunctions()
                if (response.isSuccessful && response.body()?.success == true) {
                    val freshData = response.body()?.data ?: emptyList()
                    functionsList = freshData
                    LocalStorage.saveCachedFunctions(context, freshData)
                    Toast.makeText(context, "Synced " + freshData.size + " functions", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Sync response error: " + response.message(), Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Offline mode - showing cached functions", Toast.LENGTH_SHORT).show()
            } finally {
                isLoading = false
            }
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
            try {
                val api = RetrofitClient.getService(context)
                val body = mapOf(stage to newVal)
                api.updateFunctionTracking(item.systemName, item.funcId, body)
            } catch (e: Exception) {
                // Kept in local cache
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
            try {
                val api = RetrofitClient.getService(context)
                val body = mapOf(
                    "phoneNumber" to phone,
                    "NickName" to nickName,
                    "notes" to notes,
                    "Typed" to typed,
                    "Print" to print,
                    "givenForBinding" to binding,
                    "givenToCustomer" to customer
                )
                api.updateFunctionTracking(item.systemName, item.funcId, body)
                Toast.makeText(context, "Saved and synchronized!", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
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
                                    try {
                                        val api = RetrofitClient.getService(context)
                                        api.updatePcNickname(mapOf("systemName" to sys, "NickName" to nick))
                                    } catch (e: Exception) {
                                        // Offline
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}
