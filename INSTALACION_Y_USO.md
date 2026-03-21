# 🚀 GUÍA DE INSTALACIÓN Y USO

## ⏱️ Tiempo Estimado: 15 minutos

---

## 📋 Requisitos Previos

### Hardware
- ✅ Computadora con Windows/Mac/Linux
- ✅ Mínimo 2GB RAM disponible
- ✅ 1GB almacenamiento libre

### Software
- ✅ Android Studio instalado (Arctic Fox o superior)
- ✅ JDK 11 o superior
- ✅ SDK Android 24+
- ✅ Emulador Android o dispositivo físico
- ✅ Git (opcional)

### Verificar Instalación
```powershell
# En PowerShell
java -version          # Debe mostrar Java 11+
adb --version          # Debe mostrar ADB version
```

---

## 🔧 PASO 1: PREPARAR EL ENTORNO (5 min)

### Abrir Project en Android Studio

1. **Abrir Android Studio**
2. **File → Open**
3. **Seleccionar carpeta:**
   ```
   C:\Users\minor\AndroidStudioProjects\MyApplication2
   ```
4. **Esperar a que indexe** (~2 minutos)

### Verificar Gradle
```powershell
# En PowerShell, en la carpeta del proyecto
cd C:\Users\minor\AndroidStudioProjects\MyApplication2

# Ver versión de Gradle
.\gradlew --version
```

Debería mostrar:
```
Gradle 9.3.1
```

---

## 🏗️ PASO 2: COMPILAR (5 min)

### Compilación desde Terminal

```powershell
# En PowerShell
cd C:\Users\minor\AndroidStudioProjects\MyApplication2

# Limpiar proyecto (opcional pero recomendado)
.\gradlew.bat clean

# Compilar debug
.\gradlew.bat build
```

**Resultado esperado:**
```
BUILD SUCCESSFUL in 1m 29s
94 actionable tasks: 82 executed, 12 up-to-date
```

### Compilación desde Android Studio

1. **Build → Build Bundle(s) / APK(s) → Build APK(s)**
2. **O presiona: Ctrl + F9**
3. **Esperar a que termine**

**Si hay errores:**
- ❌ "Unresolved reference" → Hacer: Build → Clean Project
- ❌ "Gradle sync failed" → Hacer: File → Sync Now
- ❌ Otro error → Ver TROUBLESHOOTING más abajo

---

## 📱 PASO 3: EMULADOR O DISPOSITIVO (3 min)

### Opción A: Usar Emulador

1. **Abrir Android Studio**
2. **Tools → Device Manager**
3. **Crear o iniciar un emulador**
   - Click en "Play" para iniciar
   - Esperar a que bootee (~30 segundos)
4. **Verificar conexión:**
   ```powershell
   adb devices
   ```
   Debería mostrar:
   ```
   List of attached devices
   emulator-5554  device
   ```

### Opción B: Dispositivo Físico

1. **Conectar Android a la computadora** (USB)
2. **En el dispositivo: Habilitar "Depuración USB"**
   - Settings → About Phone → Build Number (tap 7 veces)
   - Developer Options → USB Debugging: ON
3. **Aceptar aviso de conexión** en el dispositivo
4. **Verificar conexión:**
   ```powershell
   adb devices
   ```
   Debería mostrar:
   ```
   List of attached devices
   ABC123DEF    device
   ```

---

## 📦 PASO 4: INSTALAR (2 min)

### Instalación desde Android Studio

1. **Select Run → Run 'app'**
2. **O presiona: Shift + F10**
3. **Seleccionar dispositivo/emulador**
4. **Click OK**
5. **Esperar a que instale** (~20 segundos)

### Instalación desde Terminal

```powershell
# Instalar en el dispositivo/emulador
.\gradlew.bat installDebug

# Output esperado:
# > Task :app:installDebugAndroidTest SKIPPED
# > Task :app:installDebug
# BUILD SUCCESSFUL
```

---

## ▶️ PASO 5: EJECUTAR LA APP (1 min)

### Forma 1: Android Studio
- **Run → Run 'app'** (ya debe estar instalada)
- **O Shift + F10**

### Forma 2: Desde Terminal
```powershell
# Lanzar la aplicación
adb shell am start -n com.example.myapplication/.MainActivity
```

### Forma 3: Desde el Emulador/Dispositivo
- Buscar "Smart Home Hub" o "MyApplication"
- Tocar el ícono para abrir

**¡La app debería abrirse en 2-3 segundos!**

---

## 🎮 USANDO LA APLICACIÓN

### Pantalla Principal
```
🏠 Mi Casa Inteligente

🛋️ SALA
  💡 Luz Sala          [OFF] ← Tap para encender

🍳 COCINA
  💡 Luz Cocina        [OFF]

❄️ CLIMA
  🌡️ Aire Acondicionado 22°C
  |===●=========| 16° ← → 30° ← Desliza para cambiar
  🌀 Ventilador [OFF]

🔐 SEGURIDAD
  🚪 Puerta Principal [OFF]

[🔄 Refrescar Estado]
```

### Interacciones

**Encender/Apagar Luces:**
```
1. Tap en el switch
2. Spinner aparece (~1 segundo)
3. Luz se enciende (amarilla) o apaga (gris)
```

**Ajustar Temperatura:**
```
1. Desliza el slider izq/derecha
2. Número cambia inmediatamente
3. Spinner muestra carga (~500ms)
4. AC se actualiza
```

**Usar Ventilador:**
```
1. Tap en switch
2. Spinner (~1 segundo)
3. Ícono cambia de gris a naranja
```

**Abrir/Cerrar Puerta:**
```
1. Tap en switch
2. Spinner GRANDE (más lento)
3. Espera ~3 segundos (simula motor)
4. Puerta se abre (roja) o cierra (verde)
```

**Refrescar:**
```
1. Tap en botón "Refrescar Estado"
2. Sincroniza con servidor (~1 segundo)
```

---

## 🔄 PRUEBAS IMPORTANTES

### Prueba 1: Rotación de Pantalla
```
1. Encender la luz Sala
2. Spinner aparece (cargando)
3. Girar el dispositivo 90°
4. ✅ La luz sigue cargando (no se pierde estado)
5. Esperar a que termine
6. ✅ La luz está encendida (amarilla)
```

**¿Qué sucede?**
- ✅ ViewModel persiste en memoria
- ✅ StateFlow mantiene el estado
- ✅ UI se recrea pero datos persisten

### Prueba 2: Múltiples Acciones Rápidas
```
1. Tap Luz Sala
2. Tap Luz Cocina
3. Desliza Temperatura
4. Tap Ventilador
5. ✅ Cada una carga en orden
```

### Prueba 3: Indicador de Carga
```
1. Tap cualquier dispositivo
2. ✅ Spinner aparece inmediatamente
3. ✅ Switch se deshabilita
4. ✅ No puedes hacer click mientras carga
5. ✅ Spinner desaparece cuando termina
```

### Prueba 4: Colores Reactivos
```
Luz ON:           ✅ Fondo AMARILLO
Luz OFF:          ✅ Fondo GRIS
Puerta ABIERTA:   ✅ Ícono ROJO + Fondo rojo
Puerta CERRADA:   ✅ Ícono VERDE + Fondo verde
Ventilador ON:    ✅ Ícono NARANJA
Ventilador OFF:   ✅ Ícono GRIS
```

---

## 🐛 TROUBLESHOOTING

### Problem: "Build failed: Unresolved reference"
**Solución:**
```powershell
# Opción 1: Limpiar y reconstruir
.\gradlew.bat clean build

# Opción 2: Desde Android Studio
Build → Clean Project
Build → Rebuild Project
```

### Problem: "No connected devices found"
**Solución:**
```powershell
# Verificar conexión
adb devices

# Reiniciar ADB server
adb kill-server
adb start-server

# Reconectar dispositivo USB
# Desconectar → Esperar 3 seg → Conectar
```

### Problem: "Gradle sync failed"
**Solución:**
```powershell
# Opción 1: Desde terminal
.\gradlew.bat clean

# Opción 2: Desde Android Studio
File → Sync Now

# Opción 3: Invalidar cache
File → Invalidate Caches → Invalidate and Restart
```

### Problem: "App crashes on startup"
**Solución:**
1. Ver logs: `adb logcat | grep com.example`
2. Si dice "Theme not found": Ejecutar Clean Build
3. Si dice "ViewModel error": Verificar imports

### Problem: "App instala pero no abre"
**Solución:**
```powershell
# Desinstalar completamente
adb uninstall com.example.myapplication

# Limpiar e instalar nuevamente
.\gradlew.bat clean installDebug
```

### Problem: "Emulador muy lento"
**Solución:**
- Usar emulador con aceleración (KVM en Linux, HAXM en Windows)
- O usar dispositivo físico real
- O aumentar RAM del emulador (Device Manager → Edit)

---

## 📖 PRÓXIMAS ACTIVIDADES

### Inmediato (después de ejecutar)
1. ✅ Explorar todos los dispositivos
2. ✅ Probar rotación de pantalla
3. ✅ Observar spinners de carga
4. ✅ Verificar colores cambian

### Esta sesión
1. ✅ Leer README.md
2. ✅ Revisar HomeScreen.kt
3. ✅ Revisar HomeViewModel.kt
4. ✅ Entender flujo de datos

### Esta semana
1. ✅ Leer QUICK_REFERENCE.kt
2. ✅ Revisar todos los archivos .kt
3. ✅ Agregar comentarios propios
4. ✅ Intentar agregar dispositivo

### Este mes
1. ✅ Implementar nuevo dispositivo
2. ✅ Modificar delays
3. ✅ Cambiar colores
4. ✅ Escribir test

---

## 📝 LOGS Y DEBUGGING

### Ver Logs en Tiempo Real
```powershell
# Ver todos los logs
adb logcat

# Filtrar por app
adb logcat | grep com.example.myapplication

# Limpiar y ver nuevos
adb logcat -c
# (hace algo)
adb logcat
```

### Ver Logs desde Android Studio
1. **View → Tool Windows → Logcat**
2. **Filtrar por "MyViewModel" o "HomeScreen"**
3. **Observar mensajes de debug**

---

## 🎓 COMANDO ÚTILES

```powershell
# Ver dispositivos conectados
adb devices

# Instalar APK específico
adb install path/to/app.apk

# Desinstalar app
adb uninstall com.example.myapplication

# Clear app data
adb shell pm clear com.example.myapplication

# Ver información del dispositivo
adb shell getprop ro.build.version.release

# Reboot device
adb reboot

# Enter shell
adb shell
```

---

## ✅ CHECKLIST DE VERIFICACIÓN

```
INSTALACIÓN:
  ☐ Java instalado (java -version)
  ☐ Android SDK instalado
  ☐ Gradle actualizado
  ☐ Proyecto sincronizado
  
COMPILACIÓN:
  ☐ Build sin errores
  ☐ 0 warnings críticos
  ☐ APK generado
  
INSTALACIÓN APP:
  ☐ Dispositivo conectado/emulador corriendo
  ☐ App instalada correctamente
  ☐ Permisos otorgados
  
EJECUCIÓN:
  ☐ App abre sin crashes
  ☐ Interfaz visible
  ☐ Todos los dispositivos aparecen
  ☐ Luces se encienden/apagan
  ☐ Temperatura se ajusta
  ☐ Puerta abre/cierra
  ☐ Spinners muestran loading
  
PRUEBAS:
  ☐ Pantalla se rota sin perder estado
  ☐ Múltiples acciones funcionan
  ☐ Colores cambian correctamente
  ☐ Delays se respetan
```

---

## 🎉 ¡LISTO PARA EXPLORAR!

Si llegaste aquí sin errores:
1. ✅ La app está instalada
2. ✅ La app está funcionando
3. ✅ Tienes Smart Home Hub en tu dispositivo

**Ahora puedes:**
- 📖 Leer la documentación
- 🔍 Explorar el código
- 🛠️ Hacer modificaciones
- 🧪 Experimentar
- 📚 Aprender

---

## 📞 ¿PROBLEMAS?

**Si algo no funciona:**
1. Revisa TROUBLESHOOTING arriba
2. Verifica REQUISITOS PREVIOS
3. Intenta Clean Build
4. Desinstala y reinstala
5. Reinicia emulador/dispositivo

**Si aún no funciona:**
- Revisar errores en Logcat
- Buscar mensaje de error en README.md
- Revisar QUICK_REFERENCE.kt - Debugging

---

**¡Bienvenido al Smart Home Hub! 🏠** 

Ahora que tienes la app corriendo, es momento de aprender.
Lee README.md para entender la arquitectura.


