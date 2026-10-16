package com.moitech.notetracker.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.Message
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.moitech.notetracker.data.model.TrackedFunction
import com.moitech.notetracker.ui.theme.*

@Composable
fun NoteCard(
    item: TrackedFunction,
    onCardClick: () -> Unit,
    onToggleStage: (stage: String, currentVal: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceDark)
            .border(1.dp, BorderDark, RoundedCornerShape(16.dp))
            .clickable { onCardClick() }
            .padding(14.dp)
    ) {
        // Top Row: PC Name / Nickname Badge & Date
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(SurfaceHighlight)
                    .border(1.dp, BorderDark, RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Computer,
                    contentDescription = null,
                    tint = TextMuted,
                    modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = item.displaySystem,
                    fontSize = 11.sp,
                    color = TextMuted,
                    fontWeight = FontWeight.Medium
                )
            }

            Text(
                text = item.funcDate,
                fontSize = 11.sp,
                color = TextDimmed,
                fontWeight = FontWeight.Normal
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Function Name
        Text(
            text = item.funcName,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextWhite,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )

        // Customer & Phone Row (with 1-Tap Dial & WhatsApp)
        if (!item.phoneNumber.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = item.phoneNumber,
                    fontSize = 13.sp,
                    color = TextMuted,
                    fontWeight = FontWeight.Medium
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Call Button
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .clip(CircleShape)
                            .background(SurfaceAction)
                            .border(1.dp, BorderDark, CircleShape)
                            .clickable {
                                val intent = Intent(Intent.ACTION_DIAL).apply {
                                    data = Uri.parse("tel:${item.phoneNumber.trim()}")
                                }
                                context.startActivity(intent)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Call,
                            contentDescription = "Call",
                            tint = TextWhite,
                            modifier = Modifier.size(14.dp)
                        )
                    }

                    // WhatsApp Button
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .clip(CircleShape)
                            .background(SurfaceAction)
                            .border(1.dp, BorderDark, CircleShape)
                            .clickable {
                                try {
                                    val cleanNum = item.phoneNumber.replace("+", "").replace(" ", "").trim()
                                    val formatted = if (cleanNum.length == 10) "91$cleanNum" else cleanNum
                                    val intent = Intent(Intent.ACTION_VIEW).apply {
                                        data = Uri.parse("https://api.whatsapp.com/send?phone=$formatted")
                                    }
                                    context.startActivity(intent)
                                } catch (e: Exception) {
                                    // Fallback
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Message,
                            contentDescription = "WhatsApp",
                            tint = TextWhite,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 4 Checkbox / Progress Stages: Typed, Print, Binding, Delivered
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            StageCheckChip(
                label = "Typed",
                isChecked = item.isTyped,
                onToggle = { onToggleStage("Typed", item.typed) },
                modifier = Modifier.weight(1f)
            )
            StageCheckChip(
                label = "Print",
                isChecked = item.isPrint,
                onToggle = { onToggleStage("Print", item.print) },
                modifier = Modifier.weight(1f)
            )
            StageCheckChip(
                label = "Binding",
                isChecked = item.isBinding,
                onToggle = { onToggleStage("givenForBinding", item.givenForBinding) },
                modifier = Modifier.weight(1f)
            )
            StageCheckChip(
                label = "Customer",
                isChecked = item.isDelivered,
                onToggle = { onToggleStage("givenToCustomer", item.givenToCustomer) },
                modifier = Modifier.weight(1f)
            )
        }

        // Progress Bar
        Spacer(modifier = Modifier.height(10.dp))
        LinearProgressIndicator(
            progress = item.stageProgress.coerceIn(0f, 1f),
            modifier = Modifier
                .fillMaxWidth()
                .height(3.dp)
                .clip(RoundedCornerShape(1.5.dp)),
            color = TextWhite,
            trackColor = SurfaceHighlight
        )
    }
}

@Composable
fun StageCheckChip(
    label: String,
    isChecked: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bg = if (isChecked) PillActive else SurfaceHighlight
    val textColor = if (isChecked) CanvasBlack else TextMuted
    val borderColor = if (isChecked) TextWhite else BorderDark

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(bg)
            .border(1.dp, borderColor, RoundedCornerShape(10.dp))
            .clickable { onToggle() }
            .padding(vertical = 6.dp, horizontal = 4.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isChecked) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = CanvasBlack,
                modifier = Modifier.size(12.dp)
            )
            Spacer(modifier = Modifier.width(3.dp))
        }
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = if (isChecked) FontWeight.Bold else FontWeight.Medium,
            color = textColor,
            maxLines = 1
        )
    }
}
