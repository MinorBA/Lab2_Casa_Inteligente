# 🏠 Smart Home Hub - GUÍA DE USO VISUAL

## 🎯 Lo Que Verás en Pantalla

```
┌─────────────────────────────────────────┐
│  🏠 Mi Casa Inteligente                │  ← TopAppBar Azul
├─────────────────────────────────────────┤
│                                         │
│  🛋️ SALA                               │  ← Sección agrupada
│ ┌────────────────────────────────────┐ │
│ │ 💡 Luz Sala          [Switch ON]  │ │  ← Dispositivo con Switch
│ └────────────────────────────────────┘ │    Fondo amarillo si está ON
│                                         │
│  🍳 COCINA                              │
│ ┌────────────────────────────────────┐ │
│ │ 💡 Luz Cocina        [Switch OFF] │ │  ← Switch OFF
│ └────────────────────────────────────┘ │    Fondo gris
│                                         │
│  ❄️ CLIMA                              │
│ ┌────────────────────────────────────┐ │
│ │ 🌡️ Aire Acondicionado  22°C        │ │  ← Muestra temperatura
│ │    [Spinner rotating...]           │ │    Spinner mientras carga
│ └────────────────────────────────────┘ │
│ ┌────────────────────────────────────┐ │
│ │ |===●========| 16°C ← → 30°C      │ │  ← Slider interactivo
│ │ Rango: 16°C - 30°C                 │ │
│ └────────────────────────────────────┘ │
│ ┌────────────────────────────────────┐ │
│ │ 🌀 Ventilador        [Switch ON]  │ │  ← Ícono color si ON
│ └────────────────────────────────────┘ │
│                                         │
│  🔐 SEGURIDAD                           │
│ ┌────────────────────────────────────┐ │
│ │ 🚪 Puerta Principal   Cerrada      │ │  ← Estado: Cerrada/Abierta
│ │ [Switch OFF]                        │ │    Color gris (cerrada)
│ └────────────────────────────────────┘ │
│                                         │
│  ┌────────────────────────────────────┐ │
│  │ 🔄 Refrescar Estado               │ │  ← Botón para refrescar
│  └────────────────────────────────────┘ │
│                                         │
└─────────────────────────────────────────┘
```

---

## 🎮 Interacciones Disponibles

### 1. Encender/Apagar Luces

```
ANTES (OFF)                    DESPUÉS (ON)
┌──────────────────┐          ┌──────────────────┐
│ 💡 Luz Sala      │          │ 💡 Luz Sala      │
│ Fondo: GRIS      │ →click→  │ Fondo: AMARILLO  │
│ [0════●]         │          │ [●════1]         │
└──────────────────┘          └──────────────────┘

Tiempo: ~800-1200ms
Verás spinner mientras se procesa
```

### 2. Ajustar Temperatura

```
Puedes deslizar el slider:
|=====●========| 22°C
    ↓
|==========●===| 25°C  
    ↓
|===============●| 30°C

Max: 30°C
Min: 16°C
Delay por cambio: ~500ms
```

### 3. Ventilador

```
OFF: Ícono gris         ON: Ícono naranja
    🌀                     🌀
[0═══●]                 [●═══1]
```

### 4. Puerta Principal

```
CERRADA (Verde)              ABIERTA (Roja)
┌─────────────────┐          ┌─────────────────┐
│ 🚪 Puerta       │          │ 🚪 Puerta       │
│ Estado: Cerrada │          │ Estado: Abierta │
│ Fondo: VERDE    │ →click→  │ Fondo: ROJO     │
│ [0═══●]         │ (3 seg)  │ [●═══1]         │
└─────────────────┘          └─────────────────┘

MÁS LENTO porque simula motor
```

---

## ⏱️ Líneas de Tiempo de Operaciones

### Encender Luz Sala

```
[0ms] Usuario toca Switch
      ↓
[50ms] Spinner aparece (AnimatedVisibility)
       Switch se deshabilita
       ↓
[800-1200ms] Repository simula red
      ↓
[1250ms] Spinner desaparece
         Switch se habilita
         Luz ahora está ON (fondo amarillo)
         UI completamente actualizada
```

### Ajustar Temperatura

```
[0ms] Usuario desliza Slider a 25°C
      ↓
[20ms] Text actualiza: "25°C"
       Spinner aparece
       ↓
[500ms] Repository completa operación
      ↓
[520ms] Spinner desaparece
        UI estable
```

### Abrir Puerta

```
[0ms] Usuario toca Switch Puerta
      ↓
[50ms] Spinner aparece (MUCHO más visible)
       ↓
[2500-3500ms] ESPERA LARGA (motor)
      ↓
[3550ms] Puerta se abre (Roja)
         Spinner desaparece
```

---

## 🎨 Paleta de Colores

```
Luz Encendida:        #FFF59D (Amarillo claro)
Luz Apagada:          #FAFAFA (Gris muy claro)

AC Ícono:             #0288D1 (Azul)
AC Slider:            #0288D1

Ventilador Ícono ON:  #FF6F00 (Naranja)
Ventilador OFF:       #CCCCCC (Gris)

Puerta Abierta Fondo: #FFCDD2 (Rojo claro)
Puerta Abierta Ícono: #C62828 (Rojo oscuro)

Puerta Cerrada Fondo: #E8F5E9 (Verde claro)
Puerta Cerrada Ícono: #2E7D32 (Verde oscuro)

Primary Color:        Material Blue (AppBar)
```

---

## 🔄 Estados Posibles de Cada Dispositivo

### Luz Sala / Luz Cocina

```
STATE 1: OFF (No está cargando)
┌──────────────────┐
│ 💡 Luz Sala      │
│ Fondo: Gris      │
│ [OFF] ← Switch   │

STATE 2: CARGANDO (OFF → ON)
┌──────────────────┐
│ 💡 Luz Sala      │
│ Fondo: Gris      │
│ [⟳] ← Spinner    │

STATE 3: ON (No está cargando)
┌──────────────────┐
│ 💡 Luz Sala      │
│ Fondo: Amarillo  │
│ [ON] ← Switch    │

STATE 4: CARGANDO (ON → OFF)
┌──────────────────┐
│ 💡 Luz Sala      │
│ Fondo: Amarillo  │
│ [⟳] ← Spinner    │
```

### Aire Acondicionado

```
STATE: TEMP = 22°C (No cargando)
┌─────────────────────────────┐
│ 🌡️ Aire Acondicionado  22°C │
│ |=====●========| 16° ← → 30°│

STATE: TEMP = 22°C (CARGANDO)
┌─────────────────────────────┐
│ 🌡️ Aire Acondicionado  22°C │
│ [⟳]                         │
│ |=====●========| 16° ← → 30°│
```

---

## 🖱️ Gestos Soportados

```
TOQUE SIMPLE (tap)          → Activa Switch
                              Espera delay
                              Se actualiza estado

DESLIZAR (drag)             → En Slider de temperatura
                              Actualización inmediata en UI
                              Envía comando al ViewModel

MÚLTIPLES TOQUES            → Cada uno se procesa
                              Si está cargando: switch deshabilitado
                              No se puede hacer click mientras loading
```

---

## 🚨 Indicadores Especiales

### Spinner de Carga

```
Aparece cuando:
- User toca switch
- User mueve slider

Desaparece cuando:
- Operación completa
- Error (también se desactiva)

Estilo: CircularProgressIndicator
Color: Primary (Azul)
Duración: Variable según dispositivo
```

### Cambios de Color de Fondo

```
INSTANTÁNEO (sin delay):
- Cambios visuales de UI
- Colores de fondo de secciones
- Colores de ícono

CON DELAY:
- Cambio de estado (ON/OFF) 
- Valor de temperatura
- Estado de puerta
```

---

## 📱 Orientaciones Soportadas

```
PORTRAIT (Vertical)          LANDSCAPE (Horizontal)
┌──────────────┐            ┌────────────────────────┐
│  🏠 Mi Casa  │            │ 🏠 Mi Casa Inteligente │
│              │            │                        │
│  🛋️ SALA     │            │ 🛋️ SALA | 🍳 COCINA   │
│  ┌────────┐  │            │                        │
│  │ 💡     │  │            │ ❄️ CLIMA               │
│  └────────┘  │            │ 🚪 SEGURIDAD           │
│              │            │                        │
│  🍳 COCINA   │            │ [Refrescar]            │
│  ┌────────┐  │            └────────────────────────┘
│  │ 💡     │  │
│  └────────┘  │            ✅ ViewModel PERSISTE
│              │            ✅ Todos los datos SE GUARDAN
└──────────────┘            ✅ UI se recalcula
```

**Lo Importante: Girar la pantalla NO perderá el estado de la casa.**

---

## 🎬 Secuencias de Ejemplo

### Ejemplo 1: Encender Luz y Girar

```
[1] User toca Switch Luz Sala
    → Spinner aparece
    
[2] User ROTA la pantalla
    → Activity se recrea
    → ViewModel PERSISTE
    → Spinner sigue visible
    
[3] Operación completa
    → UI se recalcula
    → Spinner desaparece
    → Luz muestra ON (amarilla)
    
✅ GANADOR: El estado nunca se perdió
```

### Ejemplo 2: Ajustar Temperatura

```
[1] Slider a 20°C
    → Text: "20°C"
    → Spinner 500ms
    
[2] Slider a 25°C (mientras anterior carga)
    → Nuevo comando enviado
    → Spinner se reinicia 500ms
    
[3] Slider a 22°C (rápido)
    → Otro comando
    → Spinner 500ms más
    
✅ Cada movimiento = nuevo comando
```

### Ejemplo 3: Abrir Puerta Lenta

```
[1] User toca Puerta
    → Spinner GRANDE
    
[2] Espera... espera... espera...
    → 3 segundos completos
    
[3] ¡Puerta Abierta!
    → Fondo ROJO
    → Ícono ROJO
    → Spinner desaparece
    
✅ Más lento porque simula motor real
```

---

## 💡 Tips de Uso

1. **Prueba la rotación**: Enciendo luz, giro pantalla, ¡persiste!

2. **Rápida sucesión**: Toca múltiples switches rápido - cada uno se procesa

3. **Slider suave**: Mueve lentamente el slider para ver actualizaciones en vivo

4. **Observe el spinner**: Es donde ocurre la "magia" de los delays

5. **Botón Refrescar**: Simula sincronización con servidor (1seg delay)

---

## 🔧 Posibles Problemas y Soluciones

### Problema: Switch deshabilitado
```
✅ Normal mientras carga
✅ Se rehabilitará cuando termine
✅ Espera el spinner a desaparecer
```

### Problema: Temperatura no cambia
```
❌ Espera más - el delay es intencional
✅ Slider puede moverse antes de que se complete
✅ UI se actualizará después del delay
```

### Problema: Puerta tarda mucho
```
✅ CORRECTO - 2.5-3.5 segundos es intencional
✅ Simula motor mecánico real
✅ Mejor observar cómo el spinner rota
```

### Problema: Al girar, desaparecen cambios
```
❌ Esto NO debería pasar
✅ Si ocurre, asegúrate que ViewModel usa viewModelScope
✅ Verifica que StateFlow es coleccionado con collectAsState()
```

---

## 🎓 Qué Aprender Observando la UI

### 1. Reactividad
"Cuando cambio el estado, la UI se redibuja automáticamente"

### 2. Persistencia
"Giro la pantalla y los datos NO se pierden"

### 3. Async
"Las operaciones no bloquean la UI (spinners rotan suave)"

### 4. Separación
"Switch solo envía evento, no sabe nada de delays"

### 5. Indicadores
"Spinner muestra que algo está sucediendo en background"

---

**¡Ahora comprende visualmente cómo funciona MVVM + StateFlow + Compose!** 🚀

