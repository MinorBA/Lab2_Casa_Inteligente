# 🏠 Smart Home Hub - Aplicación Android Completa

Una aplicación Android moderna que demuestra una **arquitectura MVVM limpia** con **Jetpack Compose** y **StateFlow**. Simula un panel de control de casa inteligente completo y funcional.

---

## 📋 Características Principales

### ✨ Dispositivos Controlables (5+)
1. **Luz Sala** - Switch on/off con cambio de color de fondo
2. **Luz Cocina** - Switch on/off con cambio de color de fondo
3. **Aire Acondicionado** - Slider 16°C - 30°C con texto de temperatura actual
4. **Ventilador** - Switch on/off con ícono reactivo
5. **Puerta Principal** - Switch que tarda 3 segundos en simular apertura/cierre

### 🏗️ Arquitectura MVVM Limpia
```
UI (Compose)
    ↓
ViewModel (StateFlow)
    ↓
Repository (Fake con delays)
    ↓
Data (Modelos)
```

### 💾 Persistencia de Estado
- **StateFlow** (no LiveData) para reactividad
- **ViewModel** sobrevive a rotaciones de pantalla
- **viewModelScope** + **coroutines** para async

---

## 🗂️ Estructura del Proyecto

```
com.example.myapplication/
├── MainActivity.kt              # Punto de entrada (solo dibuja)
├── data/
│   └── FakeSmartHomeRepository.kt    # Simula API con delays
├── model/
│   └── HomeUiState.kt          # Data class de estado centralizado
└── ui/
    ├── HomeScreen.kt           # Composables principales
    ├── HomeViewModel.kt        # Lógica y estado
    └── theme/
        ├── Color.kt
        ├── Theme.kt
        └── Type.kt
```

---

## 🔧 Dependencias Agregadas

```kotlin
// ViewModel Compose
implementation(libs.androidx.lifecycle.viewmodel.compose)

// Coroutines Android
implementation(libs.kotlinx.coroutines.android)

// Material Icons Extended
implementation(libs.androidx.compose.material.icons.extended)
```

---

## 📊 Comportamiento de Delays

El repositorio falso simula retrasos de red reales:

| Dispositivo | Delay | Propósito |
|---|---|---|
| Luces | 800-1200 ms | Simulación realista de lámpara |
| Ventilador | 800-1200 ms | Cambio de velocidad |
| Temperatura | 500 ms | Rápido (ajuste fino) |
| Puerta | 2500-3500 ms | Motor mecánico lento |

```kotlin
// Ejemplo en FakeSmartHomeRepository
suspend fun toggleLivingRoomLight(isOn: Boolean): Boolean {
    val delay = Random.nextLong(800, 1200)
    delay(delay)  // kotlinx.coroutines.delay()
    return true
}
```

---

## 🧠 Por qué el ViewModel Sobrevive a Rotaciones

### Conceptos Clave:

1. **ViewModel no se destruye**
   - Android mantiene ViewModels en el scope de Fragment/Activity
   - Durante rotación, se crea nueva Activity pero el ViewModel persiste

2. **StateFlow != LiveData**
   - StateFlow es coleccionable manualmente (sin anotaciones)
   - No está vinculado al lifecycle de la Activity
   - La UI (Composable) se resuscribe automáticamente

3. **collectAsState() en Compose**
   ```kotlin
   val state by viewModel.uiState.collectAsState()
   // Recompone la UI siempre que cambia state
   ```

---

## 🎨 Modelo de Estado (HomeUiState)

```kotlin
data class HomeUiState(
    // Estados de dispositivos
    val isLivingRoomLightOn: Boolean = false,
    val isKitchenLightOn: Boolean = false,
    val airConditionerTemp: Int = 22,
    val isFanOn: Boolean = false,
    val isMainDoorOpen: Boolean = false,
    
    // Estados de carga (para mostrar spinners)
    val isLivingRoomLightLoading: Boolean = false,
    val isKitchenLightLoading: Boolean = false,
    val isAirConditionerLoading: Boolean = false,
    val isFanLoading: Boolean = false,
    val isMainDoorLoading: Boolean = false,
)
```

---

## 🚀 Flujo de Ejecución

### Cuando el usuario toca el Switch de la Luz Sala:

```
1. onClick → viewModel.toggleLivingRoomLight()
   
2. ViewModel activa estado de carga:
   _uiState.value = _uiState.value.copy(
       isLivingRoomLightLoading = true
   )
   
3. Lanza coroutine en viewModelScope:
   - Invoca: repository.toggleLivingRoomLight(newState)
   - Repository: delay(800-1200ms)
   
4. Actualiza StateFlow con nuevo estado:
   _uiState.value = _uiState.value.copy(
       isLivingRoomLightOn = newState,
       isLivingRoomLightLoading = false
   )
   
5. UI (collectAsState) detecta cambio:
   - Recompone automáticamente
   - Luz ahora muestra estado "encendida" (color amarillo)
   - Desaparece el spinner de carga
```

---

## 🎯 Separación de Responsabilidades

### MainActivity
```kotlin
// Solo inicializa Compose, nada de lógica
setContent {
    MyApplicationTheme {
        HomeScreen()  // Delega todo al Composable
    }
}
```

### HomeScreen (UI)
```kotlin
// Dibuja basándose en el estado
// No conoce delays, ni lógica
val state by viewModel.uiState.collectAsState()
Switch(checked = state.isLivingRoomLightOn, 
       onCheckedChange = { viewModel.toggleLivingRoomLight() })
```

### HomeViewModel
```kotlin
// Contiene toda la lógica
fun toggleLivingRoomLight() {
    viewModelScope.launch {
        _uiState.value = _uiState.value.copy(isLoading = true)
        repository.toggleLivingRoomLight(!state.value.isLivingRoomLightOn)
        // Actualizar estado
    }
}
```

### FakeSmartHomeRepository
```kotlin
// Simula operaciones
suspend fun toggleLivingRoomLight(isOn: Boolean): Boolean {
    delay(Random.nextLong(800, 1200))
    return true
}
```

---

## 💡 Features Educativos

### 1. **Indicadores de Carga**
```kotlin
Box {
    if (isLoading) {
        CircularProgressIndicator()  // Spinner visible
    } else {
        Switch()  // Switch visible
    }
}
```

### 2. **Cambios Visuales Reactivos**
```kotlin
// El color del fondo cambia según el estado
RoomSection(
    backgroundColor = if (state.isLivingRoomLightOn)
        Color(0xFFFFF59D)  // Amarillo
    else
        Color(0xFFFAFAFA)  // Gris claro
)
```

### 3. **Coroutines en ViewModel**
```kotlin
fun toggleLivingRoomLight() {
    viewModelScope.launch {  // Scope automáticamente cancelado
        // Código suspendible aquí
        repository.toggleLivingRoomLight(newState)
    }
}
```

### 4. **Slider Reactivo**
```kotlin
Slider(
    value = state.airConditionerTemp.toFloat(),
    onValueChange = { newTemp ->
        viewModel.setAirConditionerTemp(newTemp.toInt())
    },
    valueRange = 16f..30f
)
```

---

## 🎬 Cómo Usar la Aplicación

1. **Abrir la app** - Verás el panel "Mi Casa Inteligente"

2. **Encender/Apagar Luces** - Los Switches cambian color de fondo

3. **Ajustar Temperatura** - Slider actualiza la temperatura en tiempo real

4. **Controlar Ventilador** - Switch con animación de carga

5. **Abrir/Cerrar Puerta** - Tarda 3 segundos (simula motor)

6. **Refrescar** - Botón que simula sincronización con servidor

---

## 🔄 Diferencias MVVM vs MVC

| Aspecto | MVC | MVVM |
|---|---|---|
| **Datos** | Activity maneja datos | ViewModel centraliza |
| **Ciclo de vida** | Activity destruida en rotación | ViewModel persiste |
| **Reactividad** | Manual (listeners) | Automática (StateFlow) |
| **Testing** | Difícil (atado a Activity) | Fácil (independiente) |

---

## 🚀 Extensiones Futuras

```kotlin
// TODO: Agregar más dispositivos
- Cámara de seguridad
- Cerraduras inteligentes
- Sensores de movimiento

// TODO: Persistencia real
- Room Database
- DataStore

// TODO: API real
- Retrofit para llamadas HTTP
- WebSocket para tiempo real

// TODO: Más features
- Historial de acciones
- Automatizaciones
- Escenas predefinidas
```

---

## 📚 Aprendizajes Clave

✅ **ViewModel** - Persiste en rotaciones, sobrevive lifecycle  
✅ **StateFlow** - Reactivo, coleccionable en Compose  
✅ **viewModelScope** - Cancela automáticamente  
✅ **Coroutines** - async/await para operaciones largas  
✅ **Compose** - UI declarativa y reactiva  
✅ **Separation of Concerns** - Cada capa tiene su rol  
✅ **Testing** - ViewModel fácil de testear sin Android  

---

## 🏃 Comandos Útiles

```bash
# Compilar
./gradlew build

# Ejecutar tests
./gradlew test

# Instalar APK debug
./gradlew installDebug

# Limpiar
./gradlew clean
```

---

## 📝 Autor

Creado como ejemplo didáctico de arquitectura Android moderna (2025-2026).

---

**¡Esperamos que esta aplicación te ayude a entender MVVM, StateFlow y Jetpack Compose! 🚀**

