# WebSocket vs SSE: Comparación y Migración

## 📊 Tabla Comparativa Detallada

### 1. Protocolo y Comunicación

| Aspecto | WebSocket | SSE |
|--------|-----------|-----|
| **Tipo de conexión** | Bidireccional full-duplex | Unidireccional (servidor → cliente) |
| **Protocolo base** | WebSocket especial | HTTP/1.1 o HTTP/2 |
| **Handshake** | Upgrade desde HTTP | HTTP estándar |
| **Puerto** | 80/443 (igual que HTTP) | 80/443 |
| **Envío servidor** | ✅ Sí | ✅ Sí |
| **Envío cliente** | ✅ Sí | ❌ No (solo HTTP POST) |

### 2. Performance y Recursos

| Aspecto | WebSocket | SSE |
|--------|-----------|-----|
| **Overhead inicial** | ~2KB | Mínimo (~100 bytes) |
| **Overhead por mensaje** | ~2 bytes | ~0 bytes (HTTP) |
| **Consumo memoria servidor** | Medio-Alto | Bajo |
| **Consumo CPU** | Medio | Bajo |
| **Conexiones simultáneas** | Depende | 6 por dominio |
| **Escalabilidad** | Requiere Redis/etc | Nativa |

### 3. Características

| Aspecto | WebSocket | SSE |
|--------|-----------|-----|
| **Reconexión automática** | ❌ No (manual) | ✅ Sí |
| **Event IDs** | ❌ No | ✅ Sí |
| **Named events** | ❌ No (custom) | ✅ Sí |
| **Mensaje comprimido** | ✅ Sí | ⚠️ Depende proxy |
| **Latencia** | Muy baja | Baja |
| **Compatibilidad** | >= IE10 | >= IE10 (con polyfill) |
| **Firewall friendly** | ⚠️ Algunos bloquean | ✅ Sí (HTTP) |

### 4. Casos de Uso Ideales

| WebSocket | SSE |
|-----------|-----|
| Chat en tiempo real | Notificaciones |
| Juegos multiplayer | Feeds en vivo (Twitter) |
| Colaboración (Docs) | Stock prices |
| Video streaming | Progress uploads |
| Comandos bidireccionales | Alertas de sistema |

---

## 🔄 Migración: WebSocket → SSE

### Situación Actual

```
├── WebSocket Endpoints
│   ├── /ws/autorizaciones-credito
│   ├── /ws/compras
│   └── /ws/notificaciones
│
├── NotificacionesWebSocketHandler
│   └── Gestiona sesiones WebSocket
│
├── Cliente JavaScript
│   └── notificaciones-websocket.js
```

### Después de Migración

```
├── SSE Endpoints (NUEVO)
│   ├── /api/notificaciones/stream
│   ├── /api/notificaciones/stream/credito
│   └── /api/notificaciones/stream/compra
│
├── SseNotificacionService (NUEVO)
│   └── Gestiona SseEmitter
│
├── Cliente JavaScript (NUEVO)
│   └── notificaciones-sse.js
│
├── WebSocket Endpoints (MANTENER)
│   └── Para compatibilidad/casos futuros
```

---

## 🛠️ Cómo Usar Ambos (Recomendado)

### Backend

```java
@Service
public class NotificacionService {
    
    private final NotificacionesWebSocketHandler webSocketHandler;
    private final SseNotificacionService sseService;

    // Enviar por ambos canales simultáneamente
    public void notificar(NotificacionDto notif) {
        // Canal 1: WebSocket (si alguien está conectado)
        webSocketHandler.enviarNotificacion(notif);
        
        // Canal 2: SSE (si alguien está conectado)
        sseService.broadcast(notif);
    }
}
```

### Frontend

```javascript
// SSE para notificaciones pasivas
const sse = new NotificacionesSse('credito');
sse.conectar();

// WebSocket para confirmaciones bilaterales (opcional)
const ws = new NotificacionesWebSocket('autorizaciones-credito');
ws.conectar();

// Recibir notificaciones
sse.on('notificacion', (notif) => {
    console.log('Nueva notificación:', notif);
    
    if (notif.requiereAccion) {
        // Marcar como visto
        fetch(`/api/notificaciones/${notif.id}/visto`, {
            method: 'POST'
        });
    }
});
```

---

## ✅ Ventajas Hibridas

### SSE + WebSocket = Mejor de ambos mundos

```
┌─────────────────────────────────────────────┐
│         Notificaciones Bidireccionales       │
├─────────────────────────────────────────────┤
│                                              │
│  SSE (Servidor → Cliente)                   │
│  ├─ Notificaciones en tiempo real           │
│  ├─ Alertas                                 │
│  ├─ Broadcasts                              │
│  └─ Eventos del sistema                     │
│                                              │
│  WebSocket (Cliente ↔ Servidor)             │
│  ├─ Confirmaciones de recepción            │
│  ├─ Acciones de usuario                     │
│  ├─ Confirmación de lectura                 │
│  └─ Comandos interactivos                   │
│                                              │
└─────────────────────────────────────────────┘
```

---

## 📊 Caso de Uso: Sistema de Inventario

### Antes (Solo WebSocket)

```javascript
// Cliente debe estar "escuchando"
ws.conectar();
ws.on('notificacion', (notif) => {
    // Procesar
    if (notif.requiereAccion) {
        // Usuario debe responder manualmente
        ws.enviar({
            tipo: 'ACK',
            notificacionId: notif.id
        });
    }
});
```

### Después (SSE + HTTP)

```javascript
// Recibir notificaciones
sse.conectar();
sse.on('notificacion', (notif) => {
    console.log('Notificación:', notif);
});

// Confirmar via HTTP REST estándar
async function confirmarNotificacion(id) {
    const res = await fetch(`/api/notificaciones/${id}/confirmar`, {
        method: 'POST'
    });
    return res.json();
}

// Marcar como visto
async function marcarComoVisto(id) {
    const res = await fetch(`/api/notificaciones/${id}/visto`, {
        method: 'POST'
    });
    return res.json();
}
```

---

## 🎯 Recomendaciones de Implementación

### Para el Sistema de Inventario

#### ✅ USAR SSE PARA:
- Solicitudes de autorización de crédito
- Alertas de compra
- Cambios de estado
- Notificaciones del sistema
- Recordatorios

#### ✅ USAR WebSocket PARA (Futuro):
- Chat entre usuarios
- Colaboración en tiempo real
- Actualizaciones de presencia
- Sincronización en vivo

---

## 🔌 Conectando Ambos Canales

### HTML de Integración

```html
<!DOCTYPE html>
<html>
<head>
    <script src="js/notificaciones-sse.js"></script>
    <script src="js/notificaciones-websocket.js"></script>
</head>
<body>
    <div id="notificaciones"></div>

    <script>
        // Iniciar ambos canales
        const sse = new NotificacionesSse('credito');
        const ws = new NotificacionesWebSocket('autorizaciones-credito');

        // Conectar ambos
        sse.conectar();
        ws.conectar();

        // Manejar notificaciones SSE
        sse.on('notificacion', (notif) => {
            mostrarNotificacion(notif);
            
            // Si requiere acción, enviar confirmación
            if (notif.requiereAccion) {
                confirmarNotificacion(notif.id);
            }
        });

        // Manejar notificaciones WebSocket (respaldo)
        ws.on('notificacion', (notif) => {
            // Procesar solo si SSE falla
            if (!sse.estaConectado()) {
                mostrarNotificacion(notif);
            }
        });

        async function confirmarNotificacion(id) {
            try {
                const res = await fetch(`/api/notificaciones/${id}/confirmar`, {
                    method: 'POST'
                });
                return res.json();
            } catch (error) {
                console.error('Error confirmando notificación:', error);
            }
        }

        function mostrarNotificacion(notif) {
            const div = document.createElement('div');
            div.innerHTML = `
                <div class="alert alert-${notif.severidad}">
                    <strong>${notif.titulo}</strong>
                    <p>${notif.mensaje}</p>
                </div>
            `;
            document.getElementById('notificaciones').appendChild(div);
        }
    </script>
</body>
</html>
```

---

## 📈 Comparación de Tráfico

### WebSocket
```
Conexión: [Upgrade HTTP] → [WebSocket Frame] → [Keep-alive]
Mensajes: [2 bytes overhead] × 100 notificaciones = 200 bytes
Total: ~200 bytes/notificación
```

### SSE
```
Conexión: [HTTP GET /stream] → [Keep-alive]
Mensajes: [0 bytes overhead] × 100 notificaciones = 0 bytes
Total: ~0 bytes overhead/notificación
```

### HTTP REST (sin conexión persistente)
```
Mensajes: [HTTP Headers] × 100 notificaciones = 50KB (estimado)
Total: ~500 bytes overhead/notificación
```

---

## 🎓 Conclusión

### Cuándo usar cada uno:

```
┌─────────────────────────────────────────┐
│  ¿Necesita bidireccionalidad?          │
├─────────────────────────────────────────┤
│                                         │
│  SÍ → WebSocket                        │
│       (Chat, colaboración, juegos)     │
│                                         │
│  NO → SSE                              │
│       (Notificaciones, alertas)        │
│                                         │
│  AMBOS → SSE + HTTP REST                │
│          (Para confirmaciones)         │
│                                         │
└─────────────────────────────────────────┘
```

### Para tu Sistema de Inventario:

**RECOMENDACIÓN: SSE + HTTP REST**

✅ Ventajas:
- Simpler que WebSocket
- Reconexión automática
- Mejor performance
- Más fácil de debuggear
- Compatible con proxies
- Escalable sin Redis

- Confirmaciones vía REST simple:
  ```javascript
  POST /api/notificaciones/{id}/confirmar
  POST /api/notificaciones/{id}/rechazar
  ```

---

## 📚 Archivos de Referencia

- `GUIA_WEBSOCKET_NOTIFICACIONES.md` - WebSocket
- `GUIA_SSE_NOTIFICACIONES.md` - SSE
- `ARQUITECTURA_WEBSOCKET.md` - Arquitectura WebSocket
- Cliente: `notificaciones-sse.js`
- Cliente: `notificaciones-websocket.js`
