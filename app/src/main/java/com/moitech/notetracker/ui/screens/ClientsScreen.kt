package com.moitech.notetracker.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.moitech.notetracker.data.model.ClientItem
import com.moitech.notetracker.data.model.TrackedFunction
import com.moitech.notetracker.ui.theme.*

@Composable
fun ClientsScreen(
    functions: List<TrackedFunction>,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }

    val clients = remember(functions) {
        functions
            .filter { !it.phoneNumber.isNullOrBlank() }
            .groupBy { it.phoneNumber!!.trim() }
            .map { (phone, funcList) ->
                val latest = funcList.maxByOrNull { it.funcDate } ?: funcList.first()
                ClientItem(
                    phoneNumber = phone,
                    customerName = latest.customerName?.takeIf { it.isNotBlank() } ?: latest.funcName,
                    totalFunctions = funcList.size,
                    pendingCount = funcList.count { !it.isFullyCompleted },
                    completedCount = funcList.count { it.isFullyCompleted },
                    lastFunctionDate = latest.funcDate,
                    systemName = latest.displaySystem
                )
            }
            .sortedByDescending { it.lastFunctionDate }
    }

    val filteredClients = remember(clients, searchQuery) {
        if (searchQuery.isBlank()) clients
        else clients.filter {
            it.phoneNumber.contains(searchQuery) ||
            it.customerName.contains(searchQuery, ignoreCase = true) ||
            it.systemName.contains(searchQuery, ignoreCase = true)
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
                    text = "CLIENT DIRECTORY",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite,
                    letterSpacing = 1.sp
                )
                Text(
                    text = clients.size.toString() + " distinct contacts with active functions",
                    fontSize = 11.sp,
                    color = TextMuted
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    placeholder = {
                        Text("Search client phone or name...", color = TextDimmed, fontSize = 13.sp)
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
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(filteredClients, key = { it.phoneNumber }) { client ->
                ClientCard(client = client)
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@Composable
fun ClientCard(
    client: ClientItem,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceDark)
            .border(1.dp, BorderDark, RoundedCornerShape(16.dp))
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = client.phoneNumber,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite
                )
                Text(
                    text = client.customerName,
                    fontSize = 13.sp,
                    color = TextMuted
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(SurfaceAction)
                        .border(1.dp, BorderDark, CircleShape)
                        .clickable {
                            val intent = Intent(Intent.ACTION_DIAL).apply {
                                data = Uri.parse("tel:" + client.phoneNumber.trim())
                            }
                            context.startActivity(intent)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Call,
                        contentDescription = "Call",
                        tint = TextWhite,
                        modifier = Modifier.size(16.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(SurfaceAction)
                        .border(1.dp, BorderDark, CircleShape)
                        .clickable {
                            val cleanNum = client.phoneNumber.replace("+", "").replace(" ", "").trim()
                            val formatted = if (cleanNum.length == 10) "91" + cleanNum else cleanNum
                            val intent = Intent(Intent.ACTION_VIEW).apply {
                                data = Uri.parse("https://api.whatsapp.com/send?phone=" + formatted)
                            }
                            context.startActivity(intent)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Message,
                        contentDescription = "WhatsApp",
                        tint = TextWhite,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Total: " + client.totalFunctions + "  |  Pending: " + client.pendingCount,
                fontSize = 11.sp,
                color = TextDimmed
            )
            Text(
                text = "Last: " + client.lastFunctionDate,
                fontSize = 11.sp,
                color = TextDimmed
            )
        }
    }
}
