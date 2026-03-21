/*
 * 🚀 ANDROID MVVM + STATEFLOW + COMPOSE - CHEAT SHEET
 * Smart Home Hub Edition
 */

// ══════════════════════════════════════════════════════════════════════
// 1. ESTRUCTURA BÁSICA - Copy/Paste para nuevos proyectos
// ══════════════════════════════════════════════════════════════════════

// build.gradle.kts - Dependencias mínimas
implementation(libs.androidx.lifecycle.viewmodel.compose)
implementation(libs.androidx.compose.material3)
implementation(libs.kotlinx.coroutines.android)
implementation(platform(libs.androidx.compose.bom))

// ══════════════════════════════════════════════════════════════════════
// 2. DATA CLASS - Estado centralizado
// ══════════════════════════════════════════════════════════════════════

data class HomeUiState(
    val isDeviceOn: Boolean = false,
    val isDeviceLoading: Boolean = false,
    val deviceValue: Int = 0,
)

// ══════════════════════════════════════════════════════════════════════
// 3. REPOSITORY - Simula operaciones
// ══════════════════════════════════════════════════════════════════════

class FakeRepository {
    suspend fun toggleDevice(isOn: Boolean): Boolean {
        delay(Random.nextLong(800, 1200))  // ← SIEMPRE suspend + delay()
        return true
    }
}

// ══════════════════════════════════════════════════════════════════════
// 4. VIEWMODEL - Orquesta la lógica
// ══════════════════════════════════════════════════════════════════════

class MyViewModel : ViewModel() {
    private val repository = FakeRepository()
    
    private val _state = MutableStateFlow(HomeUiState())
    val state: StateFlow<HomeUiState> = _state.asStateFlow()
    
    fun toggleDevice() {
        viewModelScope.launch {  // ← SIEMPRE viewModelScope, NUNCA GlobalScope
            _state.value = _state.value.copy(isDeviceLoading = true)
            try {
                val newState = !_state.value.isDeviceOn
                repository.toggleDevice(newState)
                _state.value = _state.value.copy(
                    isDeviceOn = newState,
                    isDeviceLoading = false
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(isDeviceLoading = false)
            }
        }
    }
}

// ══════════════════════════════════════════════════════════════════════
// 5. COMPOSABLE - La UI
// ══════════════════════════════════════════════════════════════════════

@Composable
fun MyScreen(viewModel: MyViewModel = viewModel()) {
    val state by viewModel.state.collectAsState()  // ← Recolectar StateFlow
    
    Switch(
        checked = state.isDeviceOn,
        onCheckedChange = { viewModel.toggleDevice() },  // ← Evento al VM
        enabled = !state.isDeviceLoading  // ← Deshabilitar mientras carga
    )
    
    if (state.isDeviceLoading) {
        CircularProgressIndicator()  // ← Mostrar mientras carga
    }
}

// ══════════════════════════════════════════════════════════════════════
// 6. MAIN ACTIVITY - Solo inicializar
// ══════════════════════════════════════════════════════════════════════

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyTheme {
                MyScreen()  // ← Todo va aquí
            }
        }
    }
}

// ══════════════════════════════════════════════════════════════════════
// 7. PATRÓN PARA AGREGAR UN NUEVO DISPOSITIVO (5 pasos)
// ══════════════════════════════════════════════════════════════════════

// PASO 1: Agregar campos a HomeUiState
data class HomeUiState(
    // ...existing...
    val newDeviceState: Boolean = false,
    val isNewDeviceLoading: Boolean = false,
)

// PASO 2: Agregar función suspend en Repository
suspend fun toggleNewDevice(isOn: Boolean): Boolean {
    delay(Random.nextLong(800, 1200))
    return true
}

// PASO 3: Agregar función en ViewModel
fun toggleNewDevice() {
    viewModelScope.launch {
        _state.value = _state.value.copy(isNewDeviceLoading = true)
        val newState = !_state.value.newDeviceState
        repository.toggleNewDevice(newState)
        _state.value = _state.value.copy(
            newDeviceState = newState,
            isNewDeviceLoading = false
        )
    }
}

// PASO 4: Agregar UI en Composable
DeviceCard(
    icon = Icons.Default.SomeIcon,
    title = "New Device",
    isOn = state.newDeviceState,
    isLoading = state.isNewDeviceLoading,
    onToggle = { viewModel.toggleNewDevice() }
)

// ¡LISTO! Nuevo dispositivo funcionando

// ══════════════════════════════════════════════════════════════════════
// 8. ERRORES MÁS COMUNES - CÓMO EVITARLOS
// ══════════════════════════════════════════════════════════════════════

// ❌ MAL: Bloquear UI con Thread.sleep()
viewModelScope.launch {
    Thread.sleep(1000)  // ❌ NUNCA!
}

// ✅ BIEN: Usar delay() de coroutines
viewModelScope.launch {
    delay(1000)  // ✅ Correcto
}

// ────────────────────────────────────────────────────────────────────

// ❌ MAL: GlobalScope en ViewModel
GlobalScope.launch {  // ❌ NUNCA!
    // Pueden causar memory leaks
}

// ✅ BIEN: Usar viewModelScope
viewModelScope.launch {  // ✅ Correcto
    // Se cancela automáticamente
}

// ────────────────────────────────────────────────────────────────────

// ❌ MAL: Olvidar desactivar loading en error
fun toggle() {
    viewModelScope.launch {
        _state.value = _state.value.copy(isLoading = true)
        try {
            repository.toggle()
            // Actualizar
        } catch (e: Exception) {
            // ❌ Olvidó desactivar loading!
        }
    }
}

// ✅ BIEN: Manejar ambas ramas
fun toggle() {
    viewModelScope.launch {
        _state.value = _state.value.copy(isLoading = true)
        try {
            repository.toggle()
            _state.value = _state.value.copy(isLoading = false)
        } catch (e: Exception) {
            _state.value = _state.value.copy(isLoading = false)
        }
    }
}

// ────────────────────────────────────────────────────────────────────

// ❌ MAL: Crear ViewModel dentro de Composable
@Composable
fun Screen() {
    val vm = MyViewModel()  // ❌ Se recrea en cada recomposición
}

// ✅ BIEN: Usar viewModel()
@Composable
fun Screen(vm: MyViewModel = viewModel()) {  // ✅ Singleton
}

// ══════════════════════════════════════════════════════════════════════
// 9. DEBUGGING - CÓMO INSPECCIONAR ESTADO
// ══════════════════════════════════════════════════════════════════════

// En ViewModel, agregar logs
fun toggleDevice() {
    Log.d("MyVM", "Toggle called")
    viewModelScope.launch {
        val old = _state.value.isDeviceOn
        Log.d("MyVM", "Before: $old")
        
        _state.value = _state.value.copy(isLoading = true)
        Log.d("MyVM", "Loading...")
        
        repository.toggleDevice(!old)
        
        _state.value = _state.value.copy(
            isDeviceOn = !old,
            isLoading = false
        )
        Log.d("MyVM", "After: ${_state.value.isDeviceOn}")
    }
}

// Ver logs:
// adb logcat | grep "MyVM"

// ══════════════════════════════════════════════════════════════════════
// 10. TESTING - Testear sin UI
// ══════════════════════════════════════════════════════════════════════

@Test
fun testToggleDevice() = runTest {
    // Arrange
    val vm = MyViewModel()
    assert(!vm.state.value.isDeviceOn)
    
    // Act
    vm.toggleDevice()
    advanceUntilIdle()  // Esperar coroutines
    
    // Assert
    assert(vm.state.value.isDeviceOn)
}

// ══════════════════════════════════════════════════════════════════════
// 11. COMPARATIVA RÁPIDA - MVVM vs MVC
// ══════════════════════════════════════════════════════════════════════

/*
ASPECTO                 MVC                    MVVM
────────────────────────────────────────────────────────────────
Datos                   Activity maneja        ViewModel maneja
Ciclo de vida           Destruido en rotación  Persiste
Reactividad             Manual (listeners)     Automática
Testing                 Difícil (UI acoplada)  Fácil
State management        Esparcido              Centralizado
Rotation handling       Problematic            Seamless
Best for 2024           ❌ Deprecated          ✅ Moderno
*/

// ══════════════════════════════════════════════════════════════════════
// 12. REFERENCIAS RÁPIDAS
// ══════════════════════════════════════════════════════════════════════

// StateFlow vs LiveData
data class State(val value: Int = 0)

// StateFlow (moderno, recomendado)
val stateFlow: StateFlow<State> = MutableStateFlow(State()).asStateFlow()
val state by stateFlow.collectAsState()  // En Compose

// LiveData (legacy, no recomendado)
val liveData: LiveData<State> = MutableLiveData(State())
liveData.observe(this) { newState -> }  // En Activity

// ────────────────────────────────────────────────────────────────────

// Delays comunes
delay(500)      // Muy rápido - Cambios menores
delay(800)      // Rápido - Luces, ventilador
delay(1500)     // Normal - API calls
delay(3000)     // Lento - Motores, procesos largos

// ════════════════════════════════════════════════════════════════════════
// 13. ARQUITECTURA EN CAPAS - RESPONSABILIDADES
// ════════════════════════════════════════════════════════════════════════

UI LAYER (Compose)
├─ ✅ Dibuja lo que le dice ViewModel
├─ ✅ Reacciona a cambios de State
├─ ✅ Envía eventos al ViewModel
└─ ❌ NO tiene lógica de negocio
  
VIEWMODEL LAYER
├─ ✅ Maneja estado con StateFlow
├─ ✅ Ejecuta coroutines
├─ ✅ Llama a Repository
├─ ✅ Sobrevive rotaciones
└─ ❌ NO sabe de UI
  
REPOSITORY LAYER
├─ ✅ Abstrae operaciones (network, db, etc)
├─ ✅ Usa suspend functions
├─ ✅ Implementa delays/simulaciones
└─ ❌ NO sabe de ViewModel
  
DATA LAYER
├─ ✅ Data classes puros
├─ ✅ Inmutable
├─ ✅ Fuente única de verdad
└─ ❌ NO tiene lógica

// ════════════════════════════════════════════════════════════════════════
// 14. FLUJO COMPLETO - USER INTERACCIÓN
// ════════════════════════════════════════════════════════════════════════

/*
User: [TAP SWITCH]
  ↓
UI (Compose):
  Switch(onCheckedChange = { viewModel.toggleDevice() })
  ↓
ViewModel:
  fun toggleDevice() {
    viewModelScope.launch {
      _state.value = copy(isLoading = true)  // ← UI recibe, mostrará spinner
      repository.toggleDevice(newState)      // ← Async call
      _state.value = copy(                   // ← UI recibe cambio final
        isDeviceOn = newState,
        isLoading = false
      )
    }
  }
  ↓
Repository:
  suspend fun toggleDevice(isOn: Boolean) {
    delay(800)  // ← Simula red
    return true
  }
  ↓
UI recompone automáticamente:
  val state by viewModel.state.collectAsState()
  // collectAsState observa el StateFlow
  // Cuando cambia, UI se redibuja
*/

// ════════════════════════════════════════════════════════════════════════
// 15. PRODUCTION CHECKLIST
// ════════════════════════════════════════════════════════════════════════

// Antes de lanzar a producción:
// ☐ Reemplazar FakeRepository con Retrofit (API real)
// ☐ Agregar error handling (try-catch, retry logic)
// ☐ Implementar autenticación
// ☐ Agregar Room Database (cache local)
// ☐ Configurar SSL Pinning
// ☐ Agregar logging/analytics
// ☐ Testear en dispositivos reales
// ☐ Revisar performance
// ☐ Encriptar datos sensibles
// ☐ Probar con rotaciones de pantalla
// ☐ Probar con bajo storage
// ☐ Probar sin conexión (offline mode)
// ☐ Auditar permisos necesarios
// ☐ Code review con el equipo
// ☐ Publicar en Google Play

// ════════════════════════════════════════════════════════════════════════

// 🎉 ¡Ahora eres experto en MVVM + StateFlow + Compose! 🎉


