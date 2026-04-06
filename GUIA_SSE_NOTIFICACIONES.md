# Guía de Server-Sent Events (SSE) para Notificaciones

## 📡 ¿Qué es SSE?

**Server-Sent Events (SSE)** es un estándar HTML5 que permite que un servidor envíe notificaciones push a los clientes a través de una conexión HTTP persistente. Es unidireccional: **solo el servidor envía, el cliente solo recibe**.

### Comparación: SSE vs WebSocket

| Característica | SSE | WebSocket |
|---|---|---|
| **Dirección** | Servidor → Cliente | Bidireccional |
| **Protocolo** | HTTP estándar | WebSocket especial |
| **Reconexión** | Automática | Manual |
| **Overhead** | Bajo | Medio |
| **Complejidad** | Simple | Media |
| **Caso de uso ideal** | Notificaciones | Chat, juegos |

---

## 🔌 Endpoints SSE

```
GET /api/notificaciones/stream              # Todas las notificaciones
GET /api/notificaciones/stream/credito      # Solo crédito
GET /api/notificaciones/stream/compra       # Solo compra
```

**Importante:** Son GET, no POST. La conexión se mantiene abierta.

---

## 💻 Backend (Java/Spring Boot)

### SseNotificacionService.java

Servicio que gestiona los emitters SSE:

```java
@Service
public class SseNotificacionService {
    
    private final CopyOnWriteArrayList<SseEmitter> emitters = new CopyOnWriteArrayList<>();
    
    // Crear un emitter para un cliente
    public SseEmitter subscribe() {
        SseEmitter emitter = new SseEmitter(300000L); // 5 min timeout
        emitters.add(emitter);
        
        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));
        emitter.onError(throwable -> emitters.remove(emitter));
        
        return emitter;
    }
    
    // Enviar a todos los clientes
    public void broadcast(NotificacionDto notificacion) {
        String json = objectMapper.writeValueAsString(notificacion);
        emitters.forEach(emitter -> enviarJson(emitter, json));
    }
}
```

### NotificacionController.java

Endpoints para conectar y enviar:

```java
@GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
public SseEmitter streamNotificaciones() {
    return sseService.subscribe();
}
```

---

## 🌐 Cliente JavaScript

### Conexión básica

```javascript
const eventSource = new EventSource('/api/notificaciones/stream');

// Evento cuando se abre la conexión
eventSource.onopen = () => {
    console.log('Conectado a SSE');
};

// Evento para recibir mensajes
eventSource.onmessage = (event) => {
    const notificacion = JSON.parse(event.data);
    console.log('Notificación:', notificacion);
};

// Evento de error
eventSource.onerror = (event) => {
    if (eventSource.readyState === EventSource.CLOSED) {
        console.log('Desconectado');
    }
};
```

### Usar la clase `NotificacionesSse`

```javascript
// Crear cliente
const sse = new NotificacionesSse('credito');

// Registrar handlers
sse.on('connect', () => console.log('Conectado'));
sse.on('notificacion', (notif) => console.log('Nueva notificación:', notif));
sse.on('disconnect', () => console.log('Desconectado'));
sse.on('error', (err) => console.error('Error:', err));

// Conectar
sse.conectar();

// Verificar estado
console.log(sse.estaConectado()); // true/false
console.log(sse.getEstadoConexion()); // 'CONECTADO', 'DESCONECTADO', etc.

// Desconectar
sse.desconectar();
```

---

## 📨 Integración con Notificaciones

### Flujo de una notificación SSE

```
1. Ocurre un evento (solicitud de crédito, compra, etc.)
   ↓
2. Controlador llama a notificacionService.notificar...()
   ↓
3. NotificacionService construye NotificacionDto
   ↓
4. NotificacionService llama a sseService.broadcast()
   ↓
5. SseService envía a todos los SseEmitter conectados
   ↓
6. Cliente JavaScript recibe en eventSource.onmessage
   ↓
7. Cliente procesa y muestra notificación
```

### Ejemplo: Integración en Controlador

```java
@PostMapping
public ResponseEntity<?> crearSolicitud(@Valid @RequestBody AutorizacionLimiteCreditoDto.Create dto) {
    
    // Crear solicitud
    AutorizacionLimiteCredito solicitud = autorizacionService.crear(dto);
    
    // Enviar notificación por SSE
    notificacionService.notificarSolicitudAutorizacionCredito(
        solicitud.getId(),
        solicitud.getCliente().getId(),
        solicitud.getMontoSolicitado(),
        solicitud.getCliente().getNombre()
    );
    
    return ResponseEntity.status(HttpStatus.CREATED).body(...);
}
```

---

## 🎯 Eventos SSE Disponibles

### Crédito
- `CREDITO_SOLICITADO` - Nueva solicitud
- `CREDITO_APROBADO` - Solicitud aprobada
- `CREDITO_RECHAZADO` - Solicitud rechazada

### Compra
- `COMPRA_PENDIENTE_VERIFICACION` - En espera de verificación
- `COMPRA_AUTORIZADA` - Compra autorizada
- `COMPRA_RECHAZADA` - Compra rechazada
- `COMPRA_VERIFICADA_APROBADA` - Verificación completada (OK)
- `COMPRA_VERIFICADA_RECHAZADA` - Verificación completada (NO)

---

## 🎨 Formatos de Mensaje

### Estructura NotificacionDto

```json
{
  "tipo": "CREDITO_SOLICITADO",
  "titulo": "Nueva Solicitud de Autorización de Crédito",
  "mensaje": "El cliente Juan Pérez solicita una autorización de crédito por $10000.00",
  "idRelacionado": 1,
  "clienteId": 5,
  "severidad": "WARNING",
  "timestamp": 1712345678000,
  "requiereAccion": true,
  "estado": "PENDIENTE",
  "monto": 10000.00
}
```

---

## 🧪 Panel de Prueba

Acceder a:
```
http://localhost:8080/sse-panel.html
```

Features:
- Conectar a diferentes streams SSE
- Enviar notificaciones de prueba
- Ver logs en tiempo real
- Monitorear estado de conexiones

---

## ⚙️ Características de SSE Implementadas

✅ **Reconexión automática** - Si la conexión cae, EventSource reconecta automáticamente

✅ **Gestión de eventos con nombre** - `addEventListener('notificacion', ...)`

✅ **Control de timeout** - 5 minutos por defecto

✅ **Limpieza de recursos** - Se remove el emitter cuando se desconecta

✅ **Multiple streams** - Diferentes endpoints para diferentes tipos

✅ **Notificaciones visuales** - Notificaciones del navegador

✅ **Sonidos** - Alerts sonoros personalizados

✅ **Logging** - Registro detallado de eventos

---

## 🔒 Seguridad SSE

### En Producción

1. **Autenticación**
```java
@GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
public SseEmitter streamNotificaciones(HttpSession session) {
    // Verificar que el usuario esté autenticado
    if (session.getAttribute("userId") == null) {
        throw new UnauthorizedException();
    }
    
    return sseService.subscribe();
}
```

2. **Filtrar por usuario**
```java
public void broadcastParaCliente(Integer clienteId, NotificacionDto notif) {
    // Solo enviar al cliente específico
    sseService.broadcastParaCliente(clienteId, notif);
}
```

3. **CORS restringido**
```java
// En application.properties
server.servlet.cors.allowed-origins=https://dominio.com
```

---

## 📊 Monitoreo

### Ver estado actual

```http
GET /api/notificaciones/estado
```

Respuesta:
```json
{
  "websocket": {
    "clientesConectados": 5
  },
  "sse": {
    "clientesGenerales": 12,
    "clientesCreditoConectados": 8,
    "clientesCompraConectados": 6
  },
  "timestamp": 1712345678000,
  "estado": "ACTIVO"
}
```

---

## 🚀 Ventajas de SSE para Notificaciones

1. **Simple**: Una línea de código para conectar
2. **Eficiente**: HTTP estándar, menos overhead que WebSocket
3. **Automático**: Reconexión incorporada
4. **Confiable**: Garantía de entrega en orden
5. **Escalable**: Funciona bien con load balancers
6. **Estándar**: Soportado en todos los navegadores modernos

---

## ⚠️ Limitaciones de SSE

- **Unidireccional**: Solo servidor → cliente
- **HTTP**: No funciona bien con proxies que comprimen
- **Máximo 6 conexiones**: Por dominio en navegadores (EventSource fijo)
- **No funciona en IE**: Requiere polyfill

---

## 🔄 SSE + WebSocket Híbrido

**Recomendación:** Usar ambos:
- **SSE** para notificaciones pasivas (lectura)
- **WebSocket** si necesitas confirmaciones del cliente

Así se logra lo mejor de ambos mundos:

```javascript
// SSE para recibir notificaciones
const sse = new NotificacionesSse('credito');
sse.conectar();

// WebSocket para enviar confirmaciones (opcional)
const ws = new NotificacionesWebSocket('autorizaciones-credito');
ws.conectar();

// Cuando recibe notificación
sse.on('notificacion', (notif) => {
    // Mostrar notificación
    mostrar(notif);
    
    // Confirmar al servidor si es necesario
    ws.enviarConfirmacion(notif.id);
});
```

---

## 📚 Archivos Creados

```
src/main/java/com/inventario/
├── services/
│   └── SseNotificacionService.java
└── controller/
    └── NotificacionController.java (actualizado)

src/main/resources/static/
├── js/
│   └── notificaciones-sse.js
└── sse-panel.html
```

---

## 🎓 Próximos Pasos

1. [x] Implementar SseNotificacionService
2. [x] Crear endpoints SSE en controlador
3. [x] Implementar cliente JavaScript SSE
4. [x] Crear panel de prueba
5. [ ] Integrar autenticación
6. [ ] Dashboard en tiempo real
7. [ ] Persistencia de notificaciones
8. [ ] Filtrado por usuario

---

## 📞 Recursos Útiles

- [MDN - Server-Sent Events](https://developer.mozilla.org/en-US/docs/Web/API/Server-sent_events)
- [Spring & SSE](https://spring.io/guides/gs/messaging-sse/)
- [EventSource API](https://html.spec.whatwg.org/multipage/server-sent-events.html)
