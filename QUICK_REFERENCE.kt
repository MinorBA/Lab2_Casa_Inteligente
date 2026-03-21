/**
 * GUÍA RÁPIDA - Smart Home Hub
 * Referencia rápida de los puntos clave de la aplicación
 */

// ============================================
// 1. CICLO DE VIDA - Cómo funciona todo junto
// ============================================

/*
Usuario toca Switch → 
  HomeScreen.DeviceCard(onToggle = { viewModel.toggleLivingRoomLight() }) →
  
  HomeViewModel.toggleLivingRoomLight() {
    viewModelScope.launch {  // Coroutine en el scope del ViewModel
      _uiState.value = copy(isLoading = true)
      
      repository.toggleLivingRoomLight(newState)  // delay(800-1200)
      
      _uiState.value = copy(
        isLivingRoomLightOn = newState,
        isLoading = false
      )
    }
  } →
  
  HomeScreen recolecta StateFlow {
    val state by viewModel.uiState.collectAsState()
    // Recompone automáticamente
  } →
  
  UI se redibuja con nuevo estado (luz encendida, color amarillo)
*/


// ============================================
// 2. PUNTOS CLAVE DEL CÓDIGO
// ============================================

// A) HomeUiState - El fuente único de verdad
data class HomeUiState(
    val isLivingRoomLightOn: Boolean = false,      // Estado del dispositivo
    val isLivingRoomLightLoading: Boolean = false  // Para mostrar spinner
    // Todos los estados de todos los dispositivos aquí
)

// B) FakeSmartHomeRepository - Simula red/API
suspend fun toggleLivingRoomLight(isOn: Boolean): Boolean {
    val delay = Random.nextLong(800, 1200)
    delay(delay)  // kotlinx.coroutines.delay() - SUSPENDING
    return true   // Simular éxito siempre
}

// C) HomeViewModel - Orquesta la lógica
fun toggleLivingRoomLight() {
    viewModelScope.launch {  // Cancela automáticamente si ViewModel muere
        _uiState.value = _uiState.value.copy(isLoading = true)
        repository.toggleLivingRoomLight(newState)
        _uiState.value = _uiState.value.copy(
            isOn = newState,
            isLoading = false
        )
    }
}

// D) HomeScreen - Solo dibuja lo que el ViewModel dice
@Composable
fun HomeScreen(viewModel: HomeViewModel = viewModel()) {
    val state by viewModel.uiState.collectAsState()
    
    // Aquí state es siempre lo que el ViewModel tiene
    Switch(
        checked = state.isLivingRoomLightOn,
        onCheckedChange = { viewModel.toggleLivingRoomLight() }
    )
}


// ============================================
// 3. MÁS ESTADOS = MÁS DISPOSITIVOS
// ============================================

// Patrón para agregar un nuevo dispositivo:

// 1. En HomeUiState.kt - Agregar campos
data class HomeUiState(
    // ... existing ...
    val newDeviceState: Boolean = false,
    val isNewDeviceLoading: Boolean = false,
)

// 2. En FakeSmartHomeRepository.kt - Simular operación
suspend fun toggleNewDevice(isOn: Boolean): Boolean {
    delay(Random.nextLong(800, 1200))
    return true
}

// 3. En HomeViewModel.kt - Función toggle
fun toggleNewDevice() {
    viewModelScope.launch {
        _uiState.value = _uiState.value.copy(isNewDeviceLoading = true)
        val newState = !_uiState.value.newDeviceState
        repository.toggleNewDevice(newState)
        _uiState.value = _uiState.value.copy(
            newDeviceState = newState,
            isNewDeviceLoading = false
        )
    }
}

// 4. En HomeScreen.kt - Agregar Card
DeviceCard(
    icon = Icons.Default.SomeIcon,
    title = "New Device",
    isOn = state.newDeviceState,
    isLoading = state.isNewDeviceLoading,
    onToggle = { viewModel.toggleNewDevice() }
)

// ¡Listo! Nuevo dispositivo funcionando


// ============================================
// 4. ERRORES COMUNES Y SOLUCIONES
// ============================================

// ❌ ERROR: Actualizar UI directamente en ViewModel
fun toggleLight() {
    isLightOn.value = !isLightOn.value  // ❌ INCORRECTO
}

// ✅ CORRECTO: Usar viewModelScope + StateFlow
fun toggleLight() {
    viewModelScope.launch {  // ✅ CORRECTO
        _uiState.value = _uiState.value.copy(isLightOn = !isLightOn)
    }
}

// ❌ ERROR: Usar delay() sin suspend
viewModelScope.launch {
    Thread.sleep(1000)  // ❌ Bloquea el thread
}

// ✅ CORRECTO: Usar delay() de coroutines
viewModelScope.launch {
    delay(1000)  // ✅ No bloquea, es suspending
}

// ❌ ERROR: No desactivar loading después de error
fun toggleLight() {
    viewModelScope.launch {
        _uiState.value = _uiState.value.copy(isLoading = true)
        try {
            repository.toggleLight()
            // Actualizar
        } catch (e: Exception) {
            // ❌ Olvidó desactivar loading
        }
    }
}

// ✅ CORRECTO: Try-catch con finally o en ambas ramas
fun toggleLight() {
    viewModelScope.launch {
        _uiState.value = _uiState.value.copy(isLoading = true)
        try {
            repository.toggleLight()
            _uiState.value = _uiState.value.copy(isLoading = false)
        } catch (e: Exception) {
            _uiState.value = _uiState.value.copy(isLoading = false)
        }
    }
}


// ============================================
// 5. DIFERENCIAS: StateFlow vs LiveData
// ============================================

/*
STATEFLOW (moderno)                | LIVEDATA (legacy)
-----------------------------------|----------------------------------
Flow-based, Kotlin coroutines      | Lifecycle-aware, Android-specific
Coleccionable manualmente           | Solo con lifecycle observer
Funciona fuera de Android context   | Requiere Activity/Fragment
Mejor para testing                  | Más difícil de testear
Recomendado en 2024+                | Deprecated en proyectos nuevos

// StateFlow
val state: StateFlow<State> = _state.asStateFlow()
val state by state.collectAsState()  // En Compose

// LiveData (viejo)
val state: LiveData<State> = _state.asLiveData()
state.observe(this) { newState -> }  // En Activity
*/


// ============================================
// 6. STRUCTURE PRESERVA ESTADO EN ROTACIÓN
// ============================================

/*
¿Por qué funciona?

Activity (original) → User rota dispositivo → Activity (nueva)
    ↓                                              ↓
ViewModel1 ←← ViewModelStore ←→ ViewModel1
    ↓                                              ↓
StateFlow1 ←← Datos persistentes ←→ StateFlow1

Android mantiene el ViewModelStore entre recreaciones de Activity.
El StateFlow sigue siendo el mismo, así que el estado persiste.

// Sin ViewModel:
Activity (muere) → onDestroy → Datos perdidos ❌

// Con ViewModel:
Activity (muere) → ViewModel (vive) → Nueva Activity → Mismo ViewModel ✅
*/


// ============================================
// 7. DEBUGGING - Cómo inspeccionar el estado
// ============================================

// En HomeViewModel, agregar logging:
fun toggleLivingRoomLight() {
    viewModelScope.launch {
        val currentState = _uiState.value.isLivingRoomLightOn
        Log.d("HomeVM", "Toggle: from $currentState to ${!currentState}")
        
        _uiState.value = _uiState.value.copy(
            isLivingRoomLightLoading = true
        )
        Log.d("HomeVM", "Loading: true")
        
        repository.toggleLivingRoomLight(!currentState)
        
        _uiState.value = _uiState.value.copy(
            isLivingRoomLightOn = !currentState,
            isLivingRoomLightLoading = false
        )
        Log.d("HomeVM", "Final: ${_uiState.value}")
    }
}

// Ver logs:
// adb logcat | grep "HomeVM"


// ============================================
// 8. PERFORMANCE - Tips importantes
// ============================================

/*
1. No crear ViewModel adentro de composables
   ❌ val vm = HomeViewModel()  // Se recrea en cada recomposición
   ✅ val vm: HomeViewModel = viewModel()  // Cached

2. No hacer operaciones en collectAsState
   ❌ val state by vm.uiState.collectAsState()
      List(1000) { ... }  // En cada recomposición
   
   ✅ val state by vm.uiState.collectAsState()
      val expensive = remember { computeExpensive() }

3. No actualizar State frecuentemente en coroutines
   ✅ Batching: val updates = listOf(...)
      updates.forEach { _state.value = ... }

4. Usar State delegation en UI
   ✅ val (value, setValue) = remember { mutableStateOf(0) }
*/


// ============================================
// 9. TESTING RÁPIDO
// ============================================

/*
// Tests básicos del ViewModel (sin UI)
class HomeViewModelTest {
    @Test
    fun test_toggle_light() = runTest {
        val vm = HomeViewModel()
        
        // Initial state
        assert(!vm.uiState.value.isLivingRoomLightOn)
        
        // Toggle
        vm.toggleLivingRoomLight()
        advanceUntilIdle()  // Esperar coroutines
        
        // Verify
        assert(vm.uiState.value.isLivingRoomLightOn)
    }
}

// Ventajas:
- Sin UI
- Sin Activity
- Correr en JVM (rápido)
- Independiente de Android
*/


// ============================================
// 10. ROADMAP PARA PRODUCCIÓN
// ============================================

/*
Paso 1: Agregar API real (Retrofit)
├── Reemplazar FakeSmartHomeRepository
├── Agregar manejo de errores
└── Implementar retry logic

Paso 2: Persistencia (Room)
├── Cachear datos locales
├── Sincronizar con servidor
└── Offline mode

Paso 3: Autenticación
├── OAuth / JWT
├── Session management
└── Refresh tokens

Paso 4: Features avanzadas
├── Automatizaciones
├── Historial
├── Notificaciones
└── Sincronización en tiempo real (WebSocket)

Paso 5: Seguridad
├── Encriptación de datos
├── SSL Pinning
├── Validación de entrada
└── Auditoría

Paso 6: Analytics
├── Tracking de eventos
├── Crash reporting
└── Performance monitoring
*/


