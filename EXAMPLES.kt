/**
 * EJEMPLOS DE EXTENSIÓN - Smart Home Hub
 * Este archivo contiene ejemplos de cómo agregar más dispositivos y funcionalidades
 */

// ============================================
// EJEMPLO 1: Agregar un nuevo dispositivo (Bombilla RGB)
// ============================================

// En HomeUiState.kt - agregar:
data class HomeUiState(
    // ...existing fields...
    val bedroomLightColor: Int = 0xFFFFFFFF,  // Color RGB como Int
    val isBedroomLightLoading: Boolean = false,
)

// En FakeSmartHomeRepository.kt - agregar:
suspend fun setBedroomLightColor(colorHex: Int): Boolean {
    delay(800)
    return true
}

// En HomeViewModel.kt - agregar:
fun setBedroomLightColor(colorHex: Int) {
    viewModelScope.launch {
        _uiState.value = _uiState.value.copy(isBedroomLightLoading = true)
        try {
            repository.setBedroomLightColor(colorHex)
            _uiState.value = _uiState.value.copy(
                bedroomLightColor = colorHex,
                isBedroomLightLoading = false
            )
        } catch (e: Exception) {
            _uiState.value = _uiState.value.copy(isBedroomLightLoading = false)
        }
    }
}

// En HomeScreen.kt - Composable para selector de color:
@Composable
fun ColorPickerDevice(
    title: String,
    currentColor: Color,
    isLoading: Boolean,
    onColorSelected: (Int) -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, fontWeight = FontWeight.Bold)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    Color.Red, Color.Green, Color.Blue,
                    Color.Yellow, Color.Cyan, Color.Magenta
                ).forEach { color ->
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(color)
                            .clickable { onColorSelected(color.toArgb()) }
                    )
                }
            }
        }
    }
}


// ============================================
// EJEMPLO 2: Dispositivo con múltiples estados (Cerradura Inteligente)
// ============================================

// Crear enum para estados
enum class LockState {
    LOCKED,
    UNLOCKED,
    UNKNOWN
}

// En HomeUiState.kt:
data class HomeUiState(
    // ...existing fields...
    val frontDoorLockState: LockState = LockState.LOCKED,
    val frontDoorLockLoading: Boolean = false,
)

// En FakeSmartHomeRepository.kt:
suspend fun changeLockState(state: LockState): Boolean {
    delay(2000)  // Cerradura tarda más
    return true
}

// En HomeViewModel.kt:
fun toggleDoorLock() {
    viewModelScope.launch {
        val currentState = _uiState.value.frontDoorLockState
        val newState = if (currentState == LockState.LOCKED) 
            LockState.UNLOCKED else LockState.LOCKED
            
        _uiState.value = _uiState.value.copy(frontDoorLockLoading = true)
        repository.changeLockState(newState)
        
        _uiState.value = _uiState.value.copy(
            frontDoorLockState = newState,
            frontDoorLockLoading = false
        )
    }
}


// ============================================
// EJEMPLO 3: Dispositivo con Range (Cortinas/Persianas)
// ============================================

// En HomeUiState.kt:
data class HomeUiState(
    // ...existing fields...
    val livingRoomCurtainPosition: Int = 0,  // 0-100%
    val isLivingRoomCurtainLoading: Boolean = false,
)

// En HomeViewModel.kt:
fun setCurtainPosition(position: Int) {
    val validPosition = position.coerceIn(0, 100)
    
    viewModelScope.launch {
        _uiState.value = _uiState.value.copy(isLivingRoomCurtainLoading = true)
        repository.setCurtainPosition(validPosition)
        
        _uiState.value = _uiState.value.copy(
            livingRoomCurtainPosition = validPosition,
            isLivingRoomCurtainLoading = false
        )
    }
}

// En HomeScreen.kt:
@Composable
fun CurtainControl(
    position: Int,
    isLoading: Boolean,
    onPositionChange: (Int) -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Cortinas", fontWeight = FontWeight.Bold)
                Text("${position}%", fontSize = 14.sp)
            }
            
            Slider(
                value = position.toFloat(),
                onValueChange = { onPositionChange(it.toInt()) },
                valueRange = 0f..100f
            )
        }
    }
}


// ============================================
// EJEMPLO 4: Sensor/Monitor (Solo lectura, sin control)
// ============================================

// En HomeUiState.kt:
data class HomeUiState(
    // ...existing fields...
    val livingRoomHumidity: Int = 50,  // 0-100%
    val livingRoomTemperature: Float = 22f,
)

// En FakeSmartHomeRepository.kt:
suspend fun getEnvironmentalData(): Pair<Float, Int> {
    delay(500)
    return Pair(
        Random.nextFloat() * 10 + 18,  // Temp 18-28°C
        Random.nextInt(30, 80)         // Humidity 30-80%
    )
}

// En HomeViewModel.kt:
fun refreshEnvironmentalData() {
    viewModelScope.launch {
        try {
            val (temp, humidity) = repository.getEnvironmentalData()
            _uiState.value = _uiState.value.copy(
                livingRoomTemperature = temp,
                livingRoomHumidity = humidity
            )
        } catch (e: Exception) {
            // Handle error
        }
    }
}

// En HomeScreen.kt:
@Composable
fun EnvironmentalSensor(
    temperature: Float,
    humidity: Int
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.Thermostat, "Temperature")
                Text("${String.format("%.1f", temperature)}°C")
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.Opacity, "Humidity")
                Text("$humidity%")
            }
        }
    }
}


// ============================================
// EJEMPLO 5: Testing del ViewModel
// ============================================

// En tests/HomeViewModelTest.kt:
/*
@RunWith(AndroidJUnit4::class)
class HomeViewModelTest {
    
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()
    
    private lateinit var viewModel: HomeViewModel
    private lateinit var mockRepository: FakeSmartHomeRepository
    
    @Before
    fun setup() {
        mockRepository = FakeSmartHomeRepository()
        viewModel = HomeViewModel(mockRepository)
    }
    
    @Test
    fun toggleLivingRoomLight_updatesState() = runTest {
        // Arrange
        val initialState = viewModel.uiState.value
        assert(!initialState.isLivingRoomLightOn)
        
        // Act
        viewModel.toggleLivingRoomLight()
        advanceUntilIdle()  // Esperar coroutines
        
        // Assert
        val newState = viewModel.uiState.value
        assert(newState.isLivingRoomLightOn)
    }
    
    @Test
    fun setTemperature_clampValues() = runTest {
        // Arrange
        val temperature = 50  // Fuera de rango
        
        // Act
        viewModel.setAirConditionerTemp(temperature)
        advanceUntilIdle()
        
        // Assert
        val state = viewModel.uiState.value
        assertEquals(30, state.airConditionerTemp)  // Max clamped
    }
}
*/


// ============================================
// EJEMPLO 6: Agregar Autenticación
// ============================================

// En FakeSmartHomeRepository.kt:
class FakeSmartHomeRepository(
    private val userId: String = "default_user"
) {
    suspend fun authenticateUser(credentials: Credentials): Boolean {
        delay(1500)
        return credentials.username.isNotEmpty() && 
               credentials.password.isNotEmpty()
    }
}

// En HomeViewModel.kt:
private val _authState = MutableStateFlow<AuthState>(AuthState.Unauthenticated)
val authState: StateFlow<AuthState> = _authState.asStateFlow()

fun login(username: String, password: String) {
    viewModelScope.launch {
        _authState.value = AuthState.Loading
        try {
            val success = repository.authenticateUser(
                Credentials(username, password)
            )
            _authState.value = if (success) 
                AuthState.Authenticated 
            else 
                AuthState.Error("Credenciales inválidas")
        } catch (e: Exception) {
            _authState.value = AuthState.Error(e.message ?: "Error")
        }
    }
}


// ============================================
// EJEMPLO 7: Persitencia con Room Database
// ============================================

/*
// En data/SmartHomeEntity.kt:
@Entity(tableName = "smart_home_devices")
data class SmartHomeEntity(
    @PrimaryKey val id: String,
    val name: String,
    val isOn: Boolean,
    val lastUpdated: Long
)

// En data/SmartHomeDao.kt:
@Dao
interface SmartHomeDao {
    @Query("SELECT * FROM smart_home_devices")
    suspend fun getAllDevices(): List<SmartHomeEntity>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDevice(device: SmartHomeEntity)
}

// En data/SmartHomeRepository.kt (Real):
class SmartHomeRepository(
    private val dao: SmartHomeDao,
    private val apiService: SmartHomeApiService
) {
    suspend fun toggleDevice(id: String): Boolean {
        return try {
            val result = apiService.toggleDevice(id)
            dao.insertDevice(
                SmartHomeEntity(id, "Device", result.isOn, System.currentTimeMillis())
            )
            true
        } catch (e: Exception) {
            false
        }
    }
}
*/


// ============================================
// EJEMPLO 8: Retrofit para API Real
// ============================================

/*
// En data/SmartHomeApiService.kt:
interface SmartHomeApiService {
    @PUT("devices/{id}/toggle")
    suspend fun toggleDevice(@Path("id") deviceId: String): DeviceResponse
    
    @POST("devices/{id}/set-temperature")
    suspend fun setTemperature(
        @Path("id") deviceId: String,
        @Body request: TemperatureRequest
    ): DeviceResponse
}

// En data/models/:
data class DeviceResponse(
    val id: String,
    val name: String,
    val isOn: Boolean,
    val value: Int = 0
)

data class TemperatureRequest(val temperature: Int)

// En HomeViewModel (con Retrofit):
class HomeViewModel(
    private val repository: SmartHomeRepository
) : ViewModel() {
    // ... mismo patrón que antes pero con API real
}
*/


