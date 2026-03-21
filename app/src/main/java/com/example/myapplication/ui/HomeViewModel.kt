package com.example.myapplication.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.FakeSmartHomeRepository
import com.example.myapplication.model.HomeUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel para el estado de la casa inteligente.
 *
 * IMPORTANTE - Por qué el ViewModel sobrevive a rotaciones:
 * - Android mantiene las instancias de ViewModel en la memoria cuando se produce una rotación de pantalla.
 * - El ViewModel está vinculado al Scope del Fragment/Activity, no a la instancia de la UI.
 * - Cuando la Activity se recrea (por rotación), Android reutiliza la misma instancia de ViewModel.
 * - Por eso nuestro StateFlow mantiene los datos sin perderlos.
 *
 * SEPARACIÓN DE RESPONSABILIDADES:
 * - La UI (Compose) solo suscribe y dibuja. NO conoce la lógica ni los delays.
 * - El ViewModel contiene la lógica de negocio y orquesta las llamadas al repositorio.
 * - El Repositorio maneja los delays y simulaciones.
 * - Cada capa tiene su responsabilidad claramente definida.
 */
class HomeViewModel : ViewModel() {
    
    // Inicializar el repositorio falso
    private val repository = FakeSmartHomeRepository()
    
    // Estado privado (solo escritura dentro del ViewModel)
    private val _uiState = MutableStateFlow(HomeUiState())
    
    // Estado público (solo lectura para la UI)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    
    // ========== LIVING ROOM LIGHT ==========
    fun toggleLivingRoomLight() {
        viewModelScope.launch {
            // Activar estado de carga
            _uiState.value = _uiState.value.copy(isLivingRoomLightLoading = true)
            
            try {
                // Llamar al repositorio (este tiene los delays simulados)
                val newState = !_uiState.value.isLivingRoomLightOn
                repository.toggleLivingRoomLight(newState)
                
                // Actualizar el estado con el nuevo valor
                _uiState.value = _uiState.value.copy(
                    isLivingRoomLightOn = newState,
                    isLivingRoomLightLoading = false
                )
            } catch (e: Exception) {
                // En caso de error, desactivar el loading
                _uiState.value = _uiState.value.copy(isLivingRoomLightLoading = false)
            }
        }
    }
    
    // ========== KITCHEN LIGHT ==========
    fun toggleKitchenLight() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isKitchenLightLoading = true)
            
            try {
                val newState = !_uiState.value.isKitchenLightOn
                repository.toggleKitchenLight(newState)
                
                _uiState.value = _uiState.value.copy(
                    isKitchenLightOn = newState,
                    isKitchenLightLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isKitchenLightLoading = false)
            }
        }
    }
    
    // ========== AIR CONDITIONER ==========
    fun setAirConditionerTemp(temperature: Int) {
        // Limitar el rango 16-30
        val validTemp = temperature.coerceIn(16, 30)
        
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isAirConditionerLoading = true)
            
            try {
                repository.setAirConditionerTemp(validTemp)
                
                _uiState.value = _uiState.value.copy(
                    airConditionerTemp = validTemp,
                    isAirConditionerLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isAirConditionerLoading = false)
            }
        }
    }
    
    // ========== FAN ==========
    fun toggleFan() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isFanLoading = true)
            
            try {
                val newState = !_uiState.value.isFanOn
                repository.toggleFan(newState)
                
                _uiState.value = _uiState.value.copy(
                    isFanOn = newState,
                    isFanLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isFanLoading = false)
            }
        }
    }
    
    // ========== MAIN DOOR ==========
    fun toggleMainDoor() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isMainDoorLoading = true)
            
            try {
                val newState = !_uiState.value.isMainDoorOpen
                repository.toggleMainDoor(newState)
                
                _uiState.value = _uiState.value.copy(
                    isMainDoorOpen = newState,
                    isMainDoorLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isMainDoorLoading = false)
            }
        }
    }
    
    // ========== REFRESH ==========
    fun refreshHomeState() {
        viewModelScope.launch {
            try {
                val newState = repository.refreshHomeState()
                _uiState.value = newState
            } catch (e: Exception) {
                // Mantener el estado actual en caso de error
            }
        }
    }
}

