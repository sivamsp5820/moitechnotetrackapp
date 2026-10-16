package com.moitech.notetracker.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.moitech.notetracker.data.api.RetrofitClient
import com.moitech.notetracker.data.local.LocalStorage
import com.moitech.notetracker.data.model.TrackedFunction
import com.moitech.notetracker.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun SettingsScreen(
    functions: List<TrackedFunction>,
    onSaveNicknames: (systemName: String, nickname: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    var baseUrl by remember { mutableStateOf(LocalStorage.getBaseUrl(context)) }
    val lastSyncTime = remember { LocalStorage.getLastSyncTime(context) }

    val distinctSystems = remember(functions) {
        functions.map { it.systemName }.distinct()
    }

    val nicknamesMap = remember { LocalStorage.getNicknames(context).toMutableMap() }

    val formattedSync = remember(lastSyncTime) {
        if (lastSyncTime > 0) {
            SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date(lastSyncTime))
        } else {
            "Never"
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
                Text(
                    text = "SETTINGS",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Configuration & Device Sync Manager",
                    fontSize = 11.sp,
                    color = TextMuted
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(SurfaceDark)
                    .border(1.dp, BorderDark, RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Text(
                    text = "BACKEND SERVER URL",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextMuted,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Point to your Moi Software host (e.g. http://192.168.1.100:3000/api/ or cloud API)",
                    fontSize = 11.sp,
                    color = TextDimmed
                )
                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = baseUrl,
                    onValueChange = { baseUrl = it },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = SurfaceHighlight,
                        unfocusedContainerColor = SurfaceHighlight,
                        focusedBorderColor = TextWhite,
                        unfocusedBorderColor = BorderDark,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        LocalStorage.setBaseUrl(context, baseUrl)
                        RetrofitClient.invalidate()
                        Toast.makeText(context, "Server URL updated!", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TextWhite,
                        contentColor = CanvasBlack
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Save Server URL", fontWeight = FontWeight.Bold)
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(SurfaceDark)
                    .border(1.dp, BorderDark, RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Text(
                    text = "PC NICKNAME MAPPINGS",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextMuted,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Assign friendly office counter names to computer hostnames",
                    fontSize = 11.sp,
                    color = TextDimmed
                )
                Spacer(modifier = Modifier.height(12.dp))

                if (distinctSystems.isEmpty()) {
                    Text(
                        text = "No synced systems found yet. Connect your PCs and click Refresh on Home screen.",
                        fontSize = 12.sp,
                        color = TextDimmed
                    )
                } else {
                    distinctSystems.forEach { sys ->
                        var editNick by remember { mutableStateOf(nicknamesMap[sys] ?: "") }

                        Column(modifier = Modifier.padding(vertical = 6.dp)) {
                            Text(text = sys, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                    value = editNick,
                                    onValueChange = { editNick = it },
                                    placeholder = { Text("e.g. Counter 1", color = TextDimmed, fontSize = 12.sp) },
                                    modifier = Modifier.weight(1f),
                                    singleLine = true,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedContainerColor = SurfaceHighlight,
                                        unfocusedContainerColor = SurfaceHighlight,
                                        focusedBorderColor = TextWhite,
                                        unfocusedBorderColor = BorderDark,
                                        focusedTextColor = TextWhite,
                                        unfocusedTextColor = TextWhite
                                    ),
                                    shape = RoundedCornerShape(10.dp)
                                )
                                Button(
                                    onClick = {
                                        LocalStorage.saveNickname(context, sys, editNick)
                                        onSaveNicknames(sys, editNick)
                                        Toast.makeText(context, "Saved for " + sys, Toast.LENGTH_SHORT).show()
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = SurfaceAction,
                                        contentColor = TextWhite
                                    ),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text("Set")
                                }
                            }
                        }
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(SurfaceDark)
                    .border(1.dp, BorderDark, RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Text(
                    text = "OFFLINE STORAGE & CACHE",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextMuted,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(text = "Cached Functions: " + functions.size, fontSize = 12.sp, color = TextWhite)
                Text(text = "Last Full Sync: " + formattedSync, fontSize = 12.sp, color = TextDimmed)

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedButton(
                    onClick = {
                        LocalStorage.clearCache(context)
                        Toast.makeText(context, "Local cache cleared", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextMuted),
                    border = ButtonDefaults.outlinedButtonBorder.copy(width = 1.dp)
                ) {
                    Text("Clear Local Cache")
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(SurfaceDark)
                    .border(1.dp, BorderDark, RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Text(
                    text = "ABOUT MOI NOTE TRACKER",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextMuted,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(text = "Native Android App (Jetpack Compose + Material 3)", fontSize = 12.sp, color = TextWhite)
                Text(text = "Version: 1.0.0 (Release Build)", fontSize = 12.sp, color = TextDimmed)
                Text(text = "Theme: Pure Monochrome Matte Dark (No colorful)", fontSize = 12.sp, color = TextDimmed)
            }

            Spacer(modifier = Modifier.height(60.dp))
        }
    }
}
