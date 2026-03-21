package com.example.myapplication.data

import com.example.myapplication.model.HomeUiState
import kotlinx.coroutines.delay
import kotlin.random.Random

/**
 * Repositorio falso que simula operaciones de una API o base de datos.
 * Cada acción tiene un delay simulado para representar latencia de red.
 *
 * En una aplicación real, aquí irían llamadas HTTP o queries a la base de datos.
 */
class FakeSmartHomeRepository {
    
    /**
     * Simula cambiar el estado de la luz de la sala.
     * Delay: 800-1200ms
     */
    suspend fun toggleLivingRoomLight(isOn: Boolean): Boolean {
        val delay = Random.nextLong(800, 1200)
        delay(delay)
        return true // Siempre éxito en esta simulación
    }
    
    /**
     * Simula cambiar el estado de la luz de la cocina.
     * Delay: 800-1200ms
     */
    suspend fun toggleKitchenLight(isOn: Boolean): Boolean {
        val delay = Random.nextLong(800, 1200)
        delay(delay)
        return true
    }
    
    /**
     * Simula cambiar la temperatura del aire acondicionado.
     * Delay: 500ms (más rápido que otros dispositivos)
     */
    suspend fun setAirConditionerTemp(temperature: Int): Boolean {
        delay(500)
        return true
    }
    
    /**
     * Simula cambiar el estado del ventilador.
     * Delay: 800-1200ms
     */
    suspend fun toggleFan(isOn: Boolean): Boolean {
        val delay = Random.nextLong(800, 1200)
        delay(delay)
        return true
    }
    
    /**
     * Simula abrir/cerrar la puerta principal.
     * Delay: 2500-3500ms (simulando un motor más lento)
     */
    suspend fun toggleMainDoor(isOpen: Boolean): Boolean {
        val delay = Random.nextLong(2500, 3500)
        delay(delay)
        return true
    }
    
    /**
     * Simula obtener el estado actual de todos los dispositivos desde el servidor.
     * En la práctica, reestablecería la sincronización con el servidor real.
     */
    suspend fun refreshHomeState(): HomeUiState {
        delay(1000) // Simular tiempo de refrescamiento
        return HomeUiState()
    }
}

