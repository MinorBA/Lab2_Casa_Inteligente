package com.example.myapplication.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.Garage
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Router
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.model.HomeUiState

/**
 * Pantalla principal de la casa inteligente.
 *
 * Esta composable recibe el ViewModel y suscribe al StateFlow.
 * Siempre que el estado cambia, la UI se redibuja automáticamente.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: HomeViewModel = viewModel()) {
    // Suscribirse al estado - collectAsState convierte Flow a Compose State
    val state by viewModel.uiState.collectAsState()
    
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "🏠 Mi Casa Inteligente",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ========== SALA ==========
            item {
                RoomSection(
                    title = "🛋️ SALA",
                    backgroundColor = if (state.isLivingRoomLightOn)
                        Color(0xFFFFF59D) else Color(0xFFFAFAFA)
                ) {
                    DeviceCard(
                        icon = Icons.Default.Lightbulb,
                        title = "Luz Sala",
                        isOn = state.isLivingRoomLightOn,
                        isLoading = state.isLivingRoomLightLoading,
                        onToggle = { viewModel.toggleLivingRoomLight() }
                    )
                }
            }
            
            // ========== COCINA ==========
            item {
                RoomSection(
                    title = "🍳 COCINA",
                    backgroundColor = if (state.isKitchenLightOn)
                        Color(0xFFFFF59D) else Color(0xFFFAFAFA)
                ) {
                    DeviceCard(
                        icon = Icons.Default.Lightbulb,
                        title = "Luz Cocina",
                        isOn = state.isKitchenLightOn,
                        isLoading = state.isKitchenLightLoading,
                        onToggle = { viewModel.toggleKitchenLight() }
                    )
                }
            }
            
            // ========== CLIMA ==========
            item {
                RoomSection(
                    title = "❄️ CLIMA",
                    backgroundColor = Color(0xFFB3E5FC)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Aire Acondicionado
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    modifier = Modifier.weight(1f),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AcUnit,
                                        contentDescription = "AC",
                                        tint = Color(0xFF0288D1),
                                        modifier = Modifier.then(
                                            if (state.isAirConditionerLoading)
                                                Modifier
                                            else Modifier
                                        )
                                    )
                                    Column {
                                        Text("Aire Acondicionado", fontWeight = FontWeight.Bold)
                                        Text(
                                            "${state.airConditionerTemp}°C",
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF0288D1)
                                        )
                                    }
                                }
                                if (state.isAirConditionerLoading) {
                                    CircularProgressIndicator(
                                        modifier = Modifier
                                            .padding(end = 8.dp),
                                        strokeWidth = 2.dp,
                                        color = Color(0xFF0288D1)
                                    )
                                }
                            }
                        }
                        
                        // Slider de temperatura
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Slider(
                                    value = state.airConditionerTemp.toFloat(),
                                    onValueChange = { newTemp ->
                                        viewModel.setAirConditionerTemp(newTemp.toInt())
                                    },
                                    valueRange = 16f..30f,
                                    steps = 13, // Pasos de 1 grado
                                    colors = SliderDefaults.colors(
                                        thumbColor = Color(0xFF0288D1),
                                        activeTrackColor = Color(0xFF0288D1)
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Text(
                                    "Rango: 16°C - 30°C",
                                    fontSize = 12.sp,
                                    color = Color.Gray
                                )
                            }
                        }
                        
                        // Ventilador
                        DeviceCard(
                            icon = Icons.Default.Router,
                            title = "Ventilador",
                            isOn = state.isFanOn,
                            isLoading = state.isFanLoading,
                            onToggle = { viewModel.toggleFan() },
                            iconColor = Color(0xFFFF6F00)
                        )
                    }
                }
            }
            
            // ========== SEGURIDAD ==========
            item {
                RoomSection(
                    title = "🔐 SEGURIDAD",
                    backgroundColor = if (state.isMainDoorOpen)
                        Color(0xFFFFCDD2) else Color(0xFFE8F5E9)
                ) {
                    DeviceCard(
                        icon = Icons.Default.Garage,
                        title = "Puerta Principal",
                        isOn = state.isMainDoorOpen,
                        isLoading = state.isMainDoorLoading,
                        onToggle = { viewModel.toggleMainDoor() },
                        statusLabel = if (state.isMainDoorOpen) "Abierta" else "Cerrada",
                        iconColor = if (state.isMainDoorOpen) Color(0xFFC62828) else Color(0xFF2E7D32)
                    )
                }
            }
            
            // ========== BOTÓN DE REFRESH ==========
            item {
                Button(
                    onClick = { viewModel.refreshHomeState() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refrescar"
                    )
                    Spacer(modifier = Modifier.padding(8.dp))
                    Text("Refrescar Estado")
                }
            }
            
            // Espaciador inferior
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

/**
 * Composable para agrupar dispositivos por habitación/sección.
 */
@Composable
fun RoomSection(
    title: String,
    backgroundColor: Color,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                title,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color.Black
            )
            content()
        }
    }
}

/**
 * Composable reutilizable para un dispositivo (luz, ventilador, puerta).
 */
@Composable
fun DeviceCard(
    icon: ImageVector,
    title: String,
    isOn: Boolean,
    isLoading: Boolean,
    onToggle: () -> Unit,
    statusLabel: String = "",
    iconColor: Color = Color(0xFFFDD835)
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Ícono del dispositivo
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = if (isOn) iconColor else Color.Gray,
                    modifier = Modifier
                )
                
                Column {
                    Text(
                        title,
                        fontWeight = FontWeight.Bold
                    )
                    if (statusLabel.isNotEmpty()) {
                        Text(
                            statusLabel,
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }
            }
            
            // Área del switch + loading
            Box(
                modifier = Modifier
                    .width(60.dp)
                    .height(50.dp),
                contentAlignment = Alignment.Center
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.padding(8.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.primary
                    )
                } else {
                    Switch(
                        checked = isOn,
                        onCheckedChange = { onToggle() },
                        enabled = !isLoading
                    )
                }
            }
        }
    }
}













