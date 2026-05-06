package com.example.foro2dsm

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.foro2dsm.navegacion.AppNavigation
import com.example.foro2dsm.ui.theme.Foro2DSMTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Foro2DSMTheme {
                AppNavigation()
            }
        }
    }
}