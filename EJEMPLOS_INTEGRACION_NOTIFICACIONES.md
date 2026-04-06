# Ejemplos de Integración con Controladores Existentes

## 1. Integración con AutorizacionLimiteCreditoController

```java
package com.inventario.controller;

import com.inventario.dto.AutorizacionLimiteCreditoDto;
import com.inventario.exception.HttpException;
import com.inventario.models.AutorizacionLimiteCredito;
import com.inventario.services.AutorizacionLimiteCreditoService;
import com.inventario.services.NotificacionService; // ← Inyectar
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/autorizaciones-limite-credito")
public class AutorizacionLimiteCreditoController {

    private final AutorizacionLimiteCreditoService autorizacionService;
    private final NotificacionService notificacionService; // ← Inyectar

    public AutorizacionLimiteCreditoController(
            AutorizacionLimiteCreditoService autorizacionService,
            NotificacionService notificacionService) { // ← Inyectar
        this.autorizacionService = autorizacionService;
        this.notificacionService = notificacionService;
    }

    /**
     * Crear solicitud de autorización
     * Genera notificación: CREDITO_SOLICITADO
     */
    @PostMapping
    public ResponseEntity<?> crearSolicitud(@Valid @RequestBody AutorizacionLimiteCreditoDto.Create dto) 
            throws HttpException {
        
        // Crear la solicitud
        AutorizacionLimiteCredito solicitud = autorizacionService.crear(dto);
        
        // ← ENVIAR NOTIFICACION
        notificacionService.notificarSolicitudAutorizacionCredito(
            solicitud.getId(),
            solicitud.getCliente().getId(),
            solicitud.getMontoSolicitado(),
            solicitud.getCliente().getNombre()
        );
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new AutorizacionLimiteCreditoDto.Get(solicitud));
    }

    /**
     * Aprobar solicitud de autorización
     * Genera notificación: CREDITO_APROBADO
     */
    @PostMapping("/{id}/aprobar")
    public ResponseEntity<?> aprobarSolicitud(@PathVariable Integer id) throws HttpException {
        
        AutorizacionLimiteCredito solicitud = autorizacionService.obtenerPorId(id);
        
        // Aprobar
        solicitud = autorizacionService.aprobar(id);
        
        // ← ENVIAR NOTIFICACION
        notificacionService.notificarAprobacionCredito(
            solicitud.getId(),
            solicitud.getCliente().getId(),
            solicitud.getMontoAprobado(),
            solicitud.getCliente().getNombre()
        );
        
        return ResponseEntity.ok(new AutorizacionLimiteCreditoDto.Get(solicitud));
    }

    /**
     * Rechazar solicitud de autorización
     * Genera notificación: CREDITO_RECHAZADO
     */
    @PostMapping("/{id}/rechazar")
    public ResponseEntity<?> rechazarSolicitud(
            @PathVariable Integer id,
            @RequestParam String motivo) throws HttpException {
        
        AutorizacionLimiteCredito solicitud = autorizacionService.rechazar(id, motivo);
        
        // ← ENVIAR NOTIFICACION
        notificacionService.notificarRechazoCredito(
            solicitud.getId(),
            solicitud.getCliente().getId(),
            motivo
        );
        
        return ResponseEntity.ok(new AutorizacionLimiteCreditoDto.Get(solicitud));
    }

    // ... resto de métodos
}
```

---

## 2. Integración con CompraController (o similar)

```java
package com.inventario.controller;

import com.inventario.dto.CompraDto;
import com.inventario.exception.HttpException;
import com.inventario.models.Compra;
import com.inventario.services.CompraService;
import com.inventario.services.NotificacionService; // ← Inyectar
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/compras")
public class CompraController {

    private final CompraService compraService;
    private final NotificacionService notificacionService; // ← Inyectar

    public CompraController(
            CompraService compraService,
            NotificacionService notificacionService) { // ← Inyectar
        this.compraService = compraService;
        this.notificacionService = notificacionService;
    }

    /**
     * Crear compra
     * Genera notificación: COMPRA_PENDIENTE_VERIFICACION
     */
    @PostMapping
    public ResponseEntity<?> crearCompra(@Valid @RequestBody CompraDto.Create dto) 
            throws HttpException {
        
        // Crear compra
        Compra compra = compraService.crear(dto);
        
        // ← ENVIAR NOTIFICACION
        notificacionService.notificarVerificacionCompra(
            compra.getId(),
            compra.getCliente().getId(),
            compra.getMonto(),
            compra.getProducto().getNombre()
        );
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new CompraDto.Get(compra));
    }

    /**
     * Verificar compra
     * Genera notificación: COMPRA_VERIFICADA_APROBADA o COMPRA_VERIFICADA_RECHAZADA
     */
    @PostMapping("/{id}/verificar")
    public ResponseEntity<?> verificarCompra(
            @PathVariable Integer id,
            @RequestParam Boolean aprobada) throws HttpException {
        
        Compra compra = compraService.verificar(id, aprobada);
        
        // ← ENVIAR NOTIFICACION
        notificacionService.notificarVerificacionCompletada(id, aprobada);
        
        return ResponseEntity.ok(new CompraDto.Get(compra));
    }

    /**
     * Autorizar compra
     * Genera notificación: COMPRA_AUTORIZADA
     */
    @PostMapping("/{id}/autorizar")
    public ResponseEntity<?> autorizarCompra(@PathVariable Integer id) throws HttpException {
        
        Compra compra = compraService.autorizar(id);
        
        // ← ENVIAR NOTIFICACION
        notificacionService.notificarAutorizacionCompra(
            compra.getId(),
            compra.getCliente().getId(),
            compra.getMonto(),
            compra.getProducto().getNombre()
        );
        
        return ResponseEntity.ok(new CompraDto.Get(compra));
    }

    /**
     * Rechazar compra
     * Genera notificación: COMPRA_RECHAZADA
     */
    @PostMapping("/{id}/rechazar")
    public ResponseEntity<?> rechazarCompra(
            @PathVariable Integer id,
            @RequestParam String motivo) throws HttpException {
        
        Compra compra = compraService.rechazar(id, motivo);
        
        // ← ENVIAR NOTIFICACION
        notificacionService.notificarRechazoCompra(
            compra.getId(),
            compra.getCliente().getId(),
            motivo
        );
        
        return ResponseEntity.ok(new CompraDto.Get(compra));
    }

    // ... resto de métodos
}
```

---

## 3. Integración en Services

También puedes notificar desde el service layer:

```java
package com.inventario.services;

import com.inventario.models.AutorizacionLimiteCredito;
import com.inventario.repository.AutorizacionLimiteCreditoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AutorizacionLimiteCreditoService {

    private final AutorizacionLimiteCreditoRepository repository;
    private final NotificacionService notificacionService; // ← Inyectar

    public AutorizacionLimiteCreditoService(
            AutorizacionLimiteCreditoRepository repository,
            NotificacionService notificacionService) { // ← Inyectar
        this.repository = repository;
        this.notificacionService = notificacionService;
    }

    @Transactional
    public AutorizacionLimiteCredito crear(AutorizacionLimiteCreditoDto.Create dto) {
        AutorizacionLimiteCredito solicitud = new AutorizacionLimiteCredito();
        // ... llenar datos
        
        solicitud = repository.save(solicitud);
        
        // ← ENVIAR NOTIFICACION DESDE SERVICE
        notificacionService.notificarSolicitudAutorizacionCredito(
            solicitud.getId(),
            solicitud.getCliente().getId(),
            solicitud.getMontoSolicitado(),
            solicitud.getCliente().getNombre()
        );
        
        return solicitud;
    }

    @Transactional
    public AutorizacionLimiteCredito aprobar(Integer id) throws HttpException {
        AutorizacionLimiteCredito solicitud = obtenerPorId(id);
        solicitud.setEstado(EstadoSolicitud.APROBADO);
        solicitud = repository.save(solicitud);
        
        // ← ENVIAR NOTIFICACION DESDE SERVICE
        notificacionService.notificarAprobacionCredito(
            solicitud.getId(),
            solicitud.getCliente().getId(),
            solicitud.getMontoAprobado(),
            solicitud.getCliente().getNombre()
        );
        
        return solicitud;
    }

    // ... más métodos
}
```

---

## 4. Integración en Validadores (AOP)

Para casos donde quieras notificar automáticamente validaciones:

```java
package com.inventario.validation;

import com.inventario.services.NotificacionService;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class CompraValidationAspect {

    private final NotificacionService notificacionService;

    public CompraValidationAspect(NotificacionService notificacionService) {
        this.notificacionService = notificacionService;
    }

    /**
     * Notificar cuando una compra falla validación
     */
    @After("@annotation(RequiereVerificacion)")
    public void notificarVerificacionRequerida() {
        // Enviar notificación genérica
        notificacionService.notificarVerificacionCompra(0, 0, 0, "");
    }
}
```

---

## 5. Integración con Scheduler

Para notificaciones programadas:

```java
package com.inventario.services;

import com.inventario.dto.NotificacionDto;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class NotificacionScheduler {

    private final NotificacionService notificacionService;
    private final CompraService compraService;

    public NotificacionScheduler(
            NotificacionService notificacionService,
            CompraService compraService) {
        this.notificacionService = notificacionService;
        this.compraService = compraService;
    }

    /**
     * Notificar cada 5 minutos sobre compras pendientes de verificación
     */
    @Scheduled(fixedDelay = 300000) // 5 minutos
    public void notificarComprasPendientes() {
        // Obtener compras pendientes
        var comprasPendientes = compraService.obtenerPendientes();
        
        for (Compra compra : comprasPendientes) {
            NotificacionDto notificacion = NotificacionDto.crearConAccion(
                "COMPRA_PENDIENTE_VERIFICACION",
                "Recuerdo: Compra pendiente de verificación",
                "La compra #" + compra.getId() + " está esperando verificación",
                compra.getId(),
                compra.getMonto()
            );
            
            notificacionService.enviarNotificacionPersonalizada(notificacion);
        }
    }

    /**
     * Notificar sobre solicitudes de crédito que expiran pronto
     */
    @Scheduled(fixedDelay = 600000) // 10 minutos
    public void notificarSolicitudesProximas() {
        // Obtener solicitudes próximas a expirar
        var solicitudes = autorizacionService.obtenerProximasAExpirar();
        
        for (AutorizacionLimiteCredito solicitud : solicitudes) {
            NotificacionDto notificacion = NotificacionDto.builder()
                .tipo("CREDITO_PROXIMO_VENCIMIENTO")
                .titulo("Solicitud próxima a vencer")
                .mensaje("La solicitud #" + solicitud.getId() + " vence en 24 horas")
                .idRelacionado(solicitud.getId())
                .clienteId(solicitud.getCliente().getId())
                .severidad("WARNING")
                .requiereAccion(true)
                .timestamp(System.currentTimeMillis())
                .build();
            
            notificacionService.enviarNotificacionPersonalizada(notificacion);
        }
    }
}
```

---

## 6. Notificaciones en Batch

Para procesar múltiples notificaciones:

```java
public void notificarEnLote(List<CompraVerificacionEvent> eventos) {
    eventos.forEach(evento -> {
        if (evento.isAprobada()) {
            notificacionService.notificarVerificacionCompletada(
                evento.getCompraId(),
                true
            );
        } else {
            notificacionService.notificarRechazoCompra(
                evento.getCompraId(),
                evento.getClienteId(),
                evento.getMotivo()
            );
        }
    });
}
```

---

## Checklist de Integración

- [ ] Inyectar `NotificacionService` en los controladores/services
- [ ] Agregar llamadas a `notificacionService` en métodos apropiados
- [ ] Probar la conexión WebSocket desde `notificaciones-panel.html`
- [ ] Validar que las notificaciones se reciben en clientes conectados
- [ ] Integrar el cliente JavaScript en tu frontend
- [ ] Configurar manejo de desconexiones y reconexiones
- [ ] Añadir sonidos/notificaciones visuales según sea necesario
- [ ] Implementar persistencia de notificaciones si es necesario
- [ ] Añadir seguridad (autenticación) en los endpoints WebSocket
- [ ] Monitorear rendimiento con muchos clientes conectados
