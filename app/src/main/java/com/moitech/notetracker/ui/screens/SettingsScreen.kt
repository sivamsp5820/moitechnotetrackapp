package com.moitech.notetracker.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.moitech.notetracker.data.db.AivenConfigStorage
import com.moitech.notetracker.data.db.AivenDbHelper
import com.moitech.notetracker.data.local.LocalStorage
import com.moitech.notetracker.data.model.TrackedFunction
import com.moitech.notetracker.ui.theme.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun SettingsScreen(
    functions: List<TrackedFunction>,
    onSaveNicknames: (systemName: String, nickname: String) -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var isTestingConnection by remember { mutableStateOf(false) }
    var connectionStatus by remember { mutableStateOf("Ready to test direct Aiven MySQL cloud connection") }

    var host by remember { mutableStateOf(AivenConfigStorage.getHost(context)) }
    var port by remember { mutableStateOf(AivenConfigStorage.getPort(context)) }
    var db by remember { mutableStateOf(AivenConfigStorage.getDatabase(context)) }
    var user by remember { mutableStateOf(AivenConfigStorage.getUser(context)) }

    val nicknamesMap = remember { LocalStorage.getNicknames(context) }
    val distinctSystems = remember(functions) {
        functions.map { it.systemName }.distinct().filter { it.isNotBlank() }
    }

    val lastSync = LocalStorage.getLastSyncTime(context)
    val formattedSync = if (lastSync > 0) {
        SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date(lastSync))
    } else {
        "Never"
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CanvasBlack)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "SETTINGS & CLOUD",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextWhite,
                letterSpacing = 1.sp
            )

            // Direct Aiven Cloud Status & Config Card
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(SurfaceDark)
                    .border(1.dp, BorderDark, RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CloudDone,
                        contentDescription = "Cloud",
                        tint = TextWhite,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "AIVEN CLOUD MYSQL DATABASE",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextMuted,
                        letterSpacing = 1.sp
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Direct secure cloud connectivity via MariaDB TLS driver. Works globally without local server.",
                    fontSize = 11.sp,
                    color = TextDimmed
                )
                Spacer(modifier = Modifier.height(12.dp))

                // Database Parameters Table
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(SurfaceHighlight)
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(text = "Host: " + host, fontSize = 11.sp, color = TextWhite)
                    Text(text = "Port: " + port, fontSize = 11.sp, color = TextWhite)
                    Text(text = "Database: " + db, fontSize = 11.sp, color = TextWhite)
                    Text(text = "User: " + user, fontSize = 11.sp, color = TextWhite)
                    Text(text = "Credentials: Cloud Master (Verified)", fontSize = 11.sp, color = TextWhite)
                    Text(text = "SSL Encryption: TLS / SSL Enabled", fontSize = 11.sp, color = TextMuted)
                }

                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = connectionStatus,
                    fontSize = 11.sp,
                    color = if (connectionStatus.contains("Connected")) TextWhite else TextDimmed
                )
                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        coroutineScope.launch {
                            isTestingConnection = true
                            connectionStatus = "Testing direct TLS connection to Aiven MySQL..."
                            val res = AivenDbHelper.testConnection(context)
                            if (res.isSuccess) {
                                connectionStatus = res.getOrNull() ?: "Connected successfully!"
                                Toast.makeText(context, connectionStatus, Toast.LENGTH_SHORT).show()
                            } else {
                                connectionStatus = "Connection Failed: " + (res.exceptionOrNull()?.localizedMessage ?: "Unknown error")
                                Toast.makeText(context, connectionStatus, Toast.LENGTH_LONG).show()
                            }
                            isTestingConnection = false
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isTestingConnection,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TextWhite,
                        contentColor = CanvasBlack
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (isTestingConnection) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = CanvasBlack,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Testing...", fontWeight = FontWeight.Bold)
                    } else {
                        Text("Test Direct Cloud Connection", fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
                    onClick = {
                        AivenConfigStorage.resetToDefaults(context)
                        host = AivenConfigStorage.getHost(context)
                        port = AivenConfigStorage.getPort(context)
                        db = AivenConfigStorage.getDatabase(context)
                        user = AivenConfigStorage.getUser(context)
                        connectionStatus = "Cloud credentials reset to verified defaults."
                        Toast.makeText(context, "Cloud credentials reset to defaults", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextDimmed)
                ) {
                    Text("Reset to Cloud Defaults", fontSize = 12.sp)
                }
            }

            // PC Nickname Mapping Card
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
                    text = "Assign friendly office counter names to computer hostnames (synced across all devices)",
                    fontSize = 11.sp,
                    color = TextDimmed
                )
                Spacer(modifier = Modifier.height(12.dp))

                if (distinctSystems.isEmpty()) {
                    Text(
                        text = "No synced systems found yet. Sync functions from desktop PC to see hostnames.",
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

            // Offline Cache Card
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
                Text(text = "Last Full Cloud Sync: " + formattedSync, fontSize = 12.sp, color = TextDimmed)

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

            // About Card
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
                Text(text = "Version: 1.0.3 (Direct Aiven Cloud Edition)", fontSize = 12.sp, color = TextDimmed)
                Text(text = "Theme: Pure Monochrome Matte Dark", fontSize = 12.sp, color = TextDimmed)
            }

            Spacer(modifier = Modifier.height(60.dp))
        }
    }
}
