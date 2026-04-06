# Arquitectura del Sistema de Notificaciones WebSocket

## 📐 Diagrama de Arquitectura

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                            CLIENTE (Navegador)                               │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                               │
│  ┌──────────────────────────────────────────────────────────────────────┐   │
│  │ notificaciones-panel.html  (Panel de Prueba)                         │   │
│  │ ┌─────────────────────────────────────────────────────────────────┐ │   │
│  │ │ - Conectar a WebSocket                                          │ │   │
│  │ │ - Enviar notificaciones personalizadas                          │ │   │
│  │ │ - Ver logs en tiempo real                                       │ │   │
│  │ │ - Monitorear estado de conexiones                               │ │   │
│  │ └─────────────────────────────────────────────────────────────────┘ │   │
│  └──────────────────────────────────────────────────────────────────────┘   │
│                              ↑                                                │
│                              ↓ WebSocket                                      │
│  ┌──────────────────────────────────────────────────────────────────────┐   │
│  │ JavaScript Client (notificaciones-websocket.js)                     │   │
│  │ ┌─────────────────────────────────────────────────────────────────┐ │   │
│  │ │ class NotificacionesWebSocket                                   │ │   │
│  │ │                                                                  │ │   │
│  │ │ - conectar()              - Conectar a servidor               │ │   │
│  │ │ - desconectar()           - Cerrar conexión                   │ │   │
│  │ │ - on(evento, callback)    - Registrar manejadores             │ │   │
│  │ │ - procesarMensaje()       - Procesar notificaciones            │ │   │
│  │ │ - reproducirSonido()      - Sonido de alerta                  │ │   │
│  │ │ - mostrarNotificacion()   - Notificación visual               │ │   │
│  │ └─────────────────────────────────────────────────────────────────┘ │   │
│  └──────────────────────────────────────────────────────────────────────┘   │
│                                                                               │
└─────────────────────────────────────────────────────────────────────────────┘
                                    ↑
                                    ↓ WebSocket (ws://)
                                    ↓ HTTP (http://)
┌─────────────────────────────────────────────────────────────────────────────┐
│                       SPRING BOOT SERVER                                      │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                               │
│  WebSocket Endpoints Registry                                                │
│  ┌───────────────────────────────────────────────────────────────────────┐   │
│  │ /ws/autorizaciones-credito   (Solo notifc de crédito)               │   │
│  │ /ws/compras                  (Solo notificaciones de compra)         │   │
│  │ /ws/notificaciones           (Todas las notificaciones)             │   │
│  └───────────────────────────────────────────────────────────────────────┘   │
│                                ↑                                              │
│                                ↓ WebSocketConfig                             │
│  ┌───────────────────────────────────────────────────────────────────────┐   │
│  │ NotificacionesWebSocketHandler                                        │   │
│  │ ┌─────────────────────────────────────────────────────────────────┐   │   │
│  │ │ - sessions: CopyOnWriteArraySet<WebSocketSession>             │   │   │
│  │ │ - afterConnectionEstablished()    Agregar sesión              │   │   │
│  │ │ - afterConnectionClosed()         Remover sesión              │   │   │
│  │ │ - enviarNotificacion()            Enviar a todos             │   │   │
│  │ │ - enviarNotificacionPorTipo()     Enviar por tipo            │   │   │
│  │ │ - getClientesConectados()         Contar nuevas              │   │   │
│  │ └─────────────────────────────────────────────────────────────────┘   │   │
│  └───────────────────────────────────────────────────────────────────────┘   │
│                                                                               │
│  REST API                                                                     │
│  ┌───────────────────────────────────────────────────────────────────────┐   │
│  │ NotificacionController                                                │   │
│  │ ┌──────────────────────────────────────────────────────────────────┐   │   │
│  │ │ GET  /api/notificaciones/estado                                │   │   │
│  │ │ POST /api/notificaciones/prueba                                │   │   │
│  │ │ POST /api/notificaciones/credito/solicitud                    │   │   │
│  │ │ POST /api/notificaciones/credito/aprobada                     │   │   │
│  │ │ POST /api/notificaciones/credito/rechazada                    │   │   │
│  │ │ POST /api/notificaciones/compra/verificacion-pendiente        │   │   │
│  │ │ POST /api/notificaciones/compra/autorizada                    │   │   │
│  │ │ POST /api/notificaciones/compra/rechazada                     │   │   │
│  │ │ POST /api/notificaciones/compra/verificacion-completada       │   │   │
│  │ └──────────────────────────────────────────────────────────────────┘   │   │
│  └───────────────────────────────────────────────────────────────────────┘   │
│                                ↑                                              │
│                                ↓ inyecta NotificacionService                 │
│  ┌───────────────────────────────────────────────────────────────────────┐   │
│  │ NotificacionService (CAPA DE LÓGICA)                                 │   │
│  │ ┌──────────────────────────────────────────────────────────────────┐   │   │
│  │ │ Métodos específicos para cada tipo de evento:                  │   │   │
│  │ │                                                                  │   │   │
│  │ │ CRÉDITO:                                                         │   │   │
│  │ │  - notificarSolicitudAutorizacionCredito()                     │   │   │
│  │ │  - notificarAprobacionCredito()                                │   │   │
│  │ │  - notificarRechazoCredito()                                   │   │   │
│  │ │                                                                  │   │   │
│  │ │ COMPRA:                                                          │   │   │
│  │ │  - notificarVerificacionCompra()                               │   │   │
│  │ │  - notificarAutorizacionCompra()                               │   │   │
│  │ │  - notificarRechazoCompra()                                    │   │   │
│  │ │  - notificarVerificacionCompletada()                           │   │   │
│  │ │                                                                  │   │   │
│  │ │ GENERAL:                                                         │   │   │
│  │ │  - enviarNotificacionPersonalizada()                           │   │   │
│  │ │  - getClientesConectados()                                     │   │   │
│  │ └──────────────────────────────────────────────────────────────────┘   │   │
│  └───────────────────────────────────────────────────────────────────────┘   │
│                                ↑                                              │
│                                ↓ inyecta WebSocketHandler                    │
│  ┌───────────────────────────────────────────────────────────────────────┐   │
│  │ Controladores de Negocio                                              │   │
│  │ ┌──────────────────────────────────────────────────────────────────┐   │   │
│  │ │ AutorizacionLimiteCreditoController                             │   │   │
│  │ │  - POST /api/autorizaciones-limite-credito                      │   │   │
│  │ │    └─→ notificacionService.notificarSolicitud...()             │   │   │
│  │ │  - POST /api/autorizaciones-limite-credito/{id}/autorizar      │   │   │
│  │ │    └─→ notificacionService.notificarAprobacion...()            │   │   │
│  │ │                                                                  │   │   │
│  │ │ CompraController (o similar)                                    │   │   │
│  │ │  - POST /api/compras                                            │   │   │
│  │ │    └─→ notificacionService.notificarVerificacion...()           │   │   │
│  │ │  - POST /api/compras/{id}/autorizar                             │   │   │
│  │ │    └─→ notificacionService.notificarAutorizacion...()           │   │   │
│  │ └──────────────────────────────────────────────────────────────────┘   │   │
│  └───────────────────────────────────────────────────────────────────────┘   │
│                                                                               │
│  Data Transfer Objects (DTOs)                                                │
│  ┌───────────────────────────────────────────────────────────────────────┐   │
│  │ NotificacionDto                                                       │   │
│  │ ┌──────────────────────────────────────────────────────────────────┐   │   │
│  │ │ - tipo            (CREDITO_SOLICITADO, COMPRA_VERIFICADA, etc) │   │   │
│  │ │ - titulo          Título de la notificación                    │   │   │
│  │ │ - mensaje         Descripción detallada                        │   │   │
│  │ │ - idRelacionado   ID de la solicitud/compra                   │   │   │
│  │ │ - clienteId       ID del cliente                               │   │   │
│  │ │ - severidad       INFO, WARNING, ERROR, SUCCESS                │   │   │
│  │ │ - timestamp       Momento de creación                          │   │   │
│  │ │ - requiereAccion  Si necesita acción del usuario               │   │   │
│  │ │ - estado          PENDIENTE, APROBADO, RECHAZADO, etc         │   │   │
│  │ │ - monto           Cantidad involucrada                         │   │   │
│  │ │ - datos           Información adicional JSON                   │   │   │
│  │ └──────────────────────────────────────────────────────────────────┘   │   │
│  └───────────────────────────────────────────────────────────────────────┘   │
│                                                                               │
│ BASE DE DATOS (Opcional - para persistencia)                                 │
│ ┌───────────────────────────────────────────────────────────────────────┐   │
│  │ LOG_NOTIFICACIONES                                                   │   │
│  │ - id, tipo, titulo, mensaje, fecha, clienteId, estado...            │   │
│  └───────────────────────────────────────────────────────────────────────┘   │
│                                                                               │
└─────────────────────────────────────────────────────────────────────────────┘
```

## 🔄 Flujo de Datos

### Flujo 1: Solicitud de Autorización de Crédito

```
Cliente (Frontend)
    ↓
[Formulario: Solicitar Crédito]
    ↓
POST /api/autorizaciones-limite-credito
    ↓
AutorizacionLimiteCreditoController.crearSolicitud()
    ↓
NotificacionService.notificarSolicitudAutorizacionCredito()
    ↓
Construye NotificacionDto:
  {
    tipo: "CREDITO_SOLICITADO",
    titulo: "Nueva Solicitud de Autorización de Crédito",
    mensaje: "El cliente Juan Pérez solicita...",
    severidad: "WARNING",
    requiereAccion: true
  }
    ↓
NotificacionesWebSocketHandler.enviarNotificacionPorTipo("CREDITO", dto)
    ↓
Envía a todos los clientes conectados a /ws/autorizaciones-credito
    ↓
JavaScript Client recibe el mensaje
    ↓
procesarMensaje() → procesarPorTipo()
    ↓
Muestra notificación visual + sonido
```

### Flujo 2: Autorización de Compra

```
Usuario Autoriza
    ↓
POST /api/compras/{id}/autorizar
    ↓
CompraController.autorizarCompra()
    ↓
NotificacionService.notificarAutorizacionCompra()
    ↓
NotificacionDto con tipo "COMPRA_AUTORIZADA"
    ↓
Broadcast a /ws/compras
    ↓
Clientes reciben notificación
    ↓
UI se actualiza automáticamente
```

## 📊 Tipos de Notificaciones

### Crédito
| Tipo | Severidad | Acción | Descripción |
|------|-----------|--------|-------------|
| CREDITO_SOLICITADO | WARNING | Sí | Nueva solicitud pendiente |
| CREDITO_APROBADO | SUCCESS | No | Solicitud ha sido aprobada |
| CREDITO_RECHAZADO | ERROR | No | Solicitud ha sido rechazada |

### Compra
| Tipo | Severidad | Acción | Descripción |
|------|-----------|--------|-------------|
| COMPRA_PENDIENTE_VERIFICACION | WARNING | Sí | Compra en espera de verificación |
| COMPRA_AUTORIZADA | SUCCESS | No | Compra ha sido autorizada |
| COMPRA_RECHAZADA | ERROR | No | Compra ha sido rechazada |
| COMPRA_VERIFICADA_APROBADA | SUCCESS | No | Verificación completada (OK) |
| COMPRA_VERIFICADA_RECHAZADA | WARNING | No | Verificación completada (NO) |

## 🎯 Puntos de Integración

### 1. En Controladores
```
AutorizacionLimiteCreditoController → notificacionService.notificar...()
CompraController → notificacionService.notificar...()
```

### 2. En Services
```
AutorizacionLimiteCreditoService → notificacionService.notificar...()
CompraService → notificacionService.notificar...()
```

### 3. En Listeners/Eventos
```
ApplicationEvent listeners → notificacionService.notificar...()
```

### 4. En Schedulers
```
@Scheduled tasks → notificacionService.notificar...()
```

## 📈 Escalabilidad

### Configuración Actual (Simple)
- WebSocket en memoria
- Soporta ~100-500 conexiones simultáneas por instancia

### Para Producción (Distribuido)
- Redis para sesiones compartidas
- Message Broker (RabbitMQ/Kafka) para distribuir notificaciones
- Load Balancer con sticky sessions
- Múltiples instancias de Spring

## ✅ Características Implementadas

- ✅ Múltiples endpoints WebSocket
- ✅ Cliente JavaScript versátil
- ✅ Gestión automática de desconexiones/reconexiones
- ✅ Notificaciones visuales
- ✅ Notificaciones sonoras
- ✅ Panel de prueba interactivo
- ✅ REST API para enviar notificaciones
- ✅ Registro de clientes conectados
- ✅ Routing por tipo de notificación
- ✅ Documentación completa
- ✅ Ejemplos de integración

## 🚀 Próximos Pasos

1. [ ] Integrar en controladores existentes
2. [ ] Probar desde panel de notificaciones
3. [ ] Implementar persistencia de notificaciones
4. [ ] Añadir autenticación WebSocket
5. [ ] Optimizar para múltiples usuarios
6. [ ] Dashboard en tiempo real

