package com.moitech.notetracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.moitech.notetracker.ui.theme.CanvasBlack
import com.moitech.notetracker.ui.theme.MoiNoteTrackerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MoiNoteTrackerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = CanvasBlack
                ) {
                    MainAppScreen()
                }
            }
        }
    }
}
