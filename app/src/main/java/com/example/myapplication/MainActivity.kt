package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.myapplication.presentation.views.HomeScreen
import com.example.myapplication.presentation.theme.MyApplicationTheme

/**
 * MainActivity - Actividad principal de la aplicación.
 *
 * RESPONSABILIDADES:
 * - Solo inicializa Compose
 * - Solo "dibuja" lo que el ViewModel le dice
 * - No tiene lógica de negocio
 *
 * El ViewModel (HomeViewModel) maneja todo el estado y la lógica.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                // Aquí delegamos toda la UI al composable HomeScreen
                HomeScreen()
            }
        }
    }
}
