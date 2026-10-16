package com.moitech.notetracker

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.moitech.notetracker.data.local.LocalStorage
import com.moitech.notetracker.ui.theme.*

class MainActivity : ComponentActivity() {
    private var crashDetail by mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Disable StrictMode network blocking to prevent any NetworkOnMainThreadException
        try {
            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)
        } catch (t: Throwable) {
            Log.e("MainActivity", "StrictMode init error", t)
        }

        // 2. Global Uncaught Exception Handler to prevent app termination and show diagnostic UI
        val previousHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            val stack = Log.getStackTraceString(throwable)
            Log.e("MoiCrash", "Fatal Crash on ${thread.name}: $stack")
            runOnUiThread {
                crashDetail = "${throwable.javaClass.simpleName}: ${throwable.message ?: "Fatal Exception"}\n\nThread: ${thread.name}\n\n$stack"
            }
        }

        setContent {
            MoiNoteTrackerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = CanvasBlack
                ) {
                    if (crashDetail != null) {
                        CrashReportScreen(
                            error = crashDetail!!,
                            onClearCacheAndRestart = {
                                LocalStorage.clearCache(this@MainActivity)
                                crashDetail = null
                                recreate()
                            },
                            onDismiss = {
                                crashDetail = null
                            }
                        )
                    } else {
                        MainAppScreen()
                    }
                }
            }
        }
    }
}

@Composable
fun CrashReportScreen(
    error: String,
    onClearCacheAndRestart: () -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CanvasBlack)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = "Error",
                tint = TextWhite,
                modifier = Modifier.size(28.dp)
            )
            Text(
                text = "DIAGNOSTIC CRASH REPORT",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextWhite,
                letterSpacing = 1.sp
            )
        }

        Text(
            text = "An unhandled exception was caught. The app stayed active to show you what happened.",
            fontSize = 12.sp,
            color = TextDimmed
        )

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(SurfaceHighlight)
                .border(1.dp, BorderDark, RoundedCornerShape(12.dp))
                .padding(12.dp)
        ) {
            Text(
                text = error,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                color = TextWhite,
                modifier = Modifier.verticalScroll(rememberScrollState())
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = {
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    clipboard.setPrimaryClip(ClipData.newPlainText("Crash Report", error))
                    Toast.makeText(context, "Copied error report to clipboard", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = SurfaceAction, contentColor = TextWhite),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Copy Log")
            }

            Button(
                onClick = onClearCacheAndRestart,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = TextWhite, contentColor = CanvasBlack),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Clear & Reset", fontWeight = FontWeight.Bold)
            }
        }

        OutlinedButton(
            onClick = onDismiss,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = TextDimmed)
        ) {
            Text("Dismiss and Return to App")
        }
    }
}
