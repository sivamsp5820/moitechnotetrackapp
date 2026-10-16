package com.moitech.notetracker.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.moitech.notetracker.data.model.TrackedFunction
import com.moitech.notetracker.ui.theme.*

@Composable
fun NoteDetailScreen(
    item: TrackedFunction,
    onBack: () -> Unit,
    onSaveUpdates: (
        phone: String,
        nickName: String,
        notes: String,
        typed: Int,
        print: Int,
        binding: Int,
        customer: Int
    ) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    var phone by remember { mutableStateOf(item.phoneNumber ?: "") }
    var nickName by remember { mutableStateOf(item.nickName ?: "") }
    var notes by remember { mutableStateOf(item.notes ?: "") }

    var typed by remember { mutableStateOf(item.typed) }
    var print by remember { mutableStateOf(item.print) }
    var binding by remember { mutableStateOf(item.givenForBinding) }
    var customer by remember { mutableStateOf(item.givenToCustomer) }

    Scaffold(
        containerColor = CanvasBlack,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CanvasBlack)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(SurfaceDark)
                            .border(1.dp, BorderDark, CircleShape)
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextWhite)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "FUNCTION DETAILS",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite,
                        letterSpacing = 1.sp
                    )
                }

                Button(
                    onClick = {
                        onSaveUpdates(phone, nickName, notes, typed, print, binding, customer)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TextWhite,
                        contentColor = CanvasBlack
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Save", fontWeight = FontWeight.Bold)
                }
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
                    text = item.funcName,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Date: " + item.funcDate, fontSize = 12.sp, color = TextMuted)
                    Text(text = "System: " + item.systemName, fontSize = 12.sp, color = TextDimmed)
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
                    text = "WORKFLOW STAGES",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextMuted,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(12.dp))

                DetailCheckRow(
                    label = "Typed (Entry Finished)",
                    isChecked = typed == 1,
                    onToggle = { typed = if (typed == 1) 0 else 1 }
                )
                Divider(color = BorderDark, thickness = 1.dp, modifier = Modifier.padding(vertical = 10.dp))

                DetailCheckRow(
                    label = "Print (Printed Out)",
                    isChecked = print == 1,
                    onToggle = { print = if (print == 1) 0 else 1 }
                )
                Divider(color = BorderDark, thickness = 1.dp, modifier = Modifier.padding(vertical = 10.dp))

                DetailCheckRow(
                    label = "Given for Binding",
                    isChecked = binding == 1,
                    onToggle = { binding = if (binding == 1) 0 else 1 }
                )
                Divider(color = BorderDark, thickness = 1.dp, modifier = Modifier.padding(vertical = 10.dp))

                DetailCheckRow(
                    label = "Given to Customer",
                    isChecked = customer == 1,
                    onToggle = { customer = if (customer == 1) 0 else 1 }
                )
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
                    text = "CUSTOMER PHONE",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextMuted,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Enter 10-digit mobile number", color = TextDimmed) },
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

                if (phone.isNotBlank()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                val intent = Intent(Intent.ACTION_DIAL).apply {
                                    data = Uri.parse("tel:" + phone.trim())
                                }
                                context.startActivity(intent)
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = SurfaceAction,
                                contentColor = TextWhite
                            )
                        ) {
                            Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Call")
                        }

                        Button(
                            onClick = {
                                val cleanNum = phone.replace("+", "").replace(" ", "").trim()
                                val formatted = if (cleanNum.length == 10) "91" + cleanNum else cleanNum
                                val intent = Intent(Intent.ACTION_VIEW).apply {
                                    data = Uri.parse("https://api.whatsapp.com/send?phone=" + formatted)
                                }
                                context.startActivity(intent)
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = SurfaceAction,
                                contentColor = TextWhite
                            )
                        ) {
                            Icon(Icons.Default.Message, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("WhatsApp")
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
                    text = "PC NICKNAME",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextMuted,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Customize friendly name for " + item.systemName + " (e.g. Counter 1)",
                    fontSize = 11.sp,
                    color = TextDimmed
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = nickName,
                    onValueChange = { nickName = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("e.g. Front Billing Counter", color = TextDimmed) },
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
                    text = "ADDITIONAL NOTES",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextMuted,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp),
                    placeholder = { Text("Add specific client instructions, binding style, delivery time...", color = TextDimmed) },
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
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun DetailCheckRow(
    label: String,
    isChecked: Boolean,
    onToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle() },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = if (isChecked) TextWhite else TextMuted
        )

        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(if (isChecked) TextWhite else SurfaceAction)
                .border(1.dp, if (isChecked) TextWhite else BorderDark, RoundedCornerShape(6.dp)),
            contentAlignment = Alignment.Center
        ) {
            if (isChecked) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = CanvasBlack,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
