package com.example.cappucaresuministrosmedicos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.cappucaresuministrosmedicos.view.HomeScreen
import com.example.cappucaresuministrosmedicos.view.theme.CappucareSuministrosMedicosTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CappucareSuministrosMedicosTheme {
                HomeScreen(
                    onIrAProductos = { /* TODO: navegar a gestión de productos */ },
                    onIrAMovimientos = { /* TODO: navegar a gestión de movimientos */ }
                )
            }
        }
    }
}