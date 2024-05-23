package com.example.customui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.customui.components.MyCanvas
import com.example.customui.ui.theme.CustomUITheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CustomUITheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(), containerColor = Color.White
                ) { innerPadding ->
                    MyCanvas(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}


