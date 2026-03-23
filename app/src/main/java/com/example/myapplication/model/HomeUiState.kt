package com.example.myapplication.model

/**
 * Estado centralizado de la aplicación Smart Home.
 * Esta clase contiene todo el estado de la UI que se debe actualizar reactivamente.
 *
 * Los valores de Boolean pueden estar null para indicar que están cargando (loading state).
 */
data class HomeUiState(
    // Luces
    val isLivingRoomLightOn: Boolean = false,
    val isKitchenLightOn: Boolean = false,

    // Aire Acondicionado (rango 16-30°C)
    val airConditionerTemp: Int = 22,

    // Ventilador
    val isFanOn: Boolean = false,

    // Puerta Principal
    val isMainDoorOpen: Boolean = false,

    // Estados de carga por acción
    val isLivingRoomLightLoading: Boolean = false,
    val isKitchenLightLoading: Boolean = false,
    val isAirConditionerLoading: Boolean = false,
    val isFanLoading: Boolean = false,
    val isMainDoorLoading: Boolean = false,
)