package com.inventario.controller;

import com.inventario.dto.NotificacionDto;
import com.inventario.services.NotificacionService;
import com.inventario.services.SseNotificacionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.HashMap;
import java.util.Map;

/**
 * Controlador REST para gestionar notificaciones SSE
 * Proporciona endpoints para SSE y envío de notificaciones
 */
@Slf4j
@RestController
@RequestMapping("/api/notificaciones")
public class NotificacionController {

    private final NotificacionService notificacionService;
    private final SseNotificacionService sseService;

    public NotificacionController(NotificacionService notificacionService,
            SseNotificacionService sseService) {
        this.notificacionService = notificacionService;
        this.sseService = sseService;
    }

    // ==================== SSE ENDPOINTS ====================

    /**
     * Conecta un cliente a SSE para recibir todas las notificaciones
     */
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    // Ejemplo de autorización:
    // @ValidateToken(roles = {SystemRoles.VENDEDOR, SystemRoles.CAJA, SystemRoles.COMPRAS, SystemRoles.ADMINISTRADOR})
    public SseEmitter streamNotificaciones() {
        return sseService.subscribe();
    }

    /**
     * Conecta un cliente a SSE para recibir solo notificaciones de crédito
     */
    @GetMapping(value = "/stream/credito", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    // Ejemplo de autorización:
    // @ValidateToken(roles = {SystemRoles.CAJA, SystemRoles.ADMINISTRADOR})
    public SseEmitter streamCreditoNotificaciones() {
        return sseService.subscribeCredito();
    }

    /**
     * Conecta un cliente a SSE para recibir solo notificaciones de venta
     */
    @GetMapping(value = "/stream/venta", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    // Ejemplo de autorización:
    // @ValidateToken(roles = {SystemRoles.VENDEDOR, SystemRoles.ADMINISTRADOR})
    public SseEmitter streamVentaNotificaciones() {
        return sseService.subscribeVenta();
    }

    // ==================== ESTADO Y MONITOREO ====================

    /**
     * Obtiene el estado de las conexiones SSE
     */
    @GetMapping("/estado")
    // Ejemplo de autorización:
    // @ValidateToken(roles = {SystemRoles.ADMINISTRADOR})
    public ResponseEntity<Map<String, Object>> obtenerEstado() {
        Map<String, Object> estado = new HashMap<>();
        estado.put("sse", new HashMap<String, Integer>() {
            {
                put("clientesGenerales", sseService.getClientesConectados());
                put("clientesCreditoConectados", sseService.getClientesCreditoConectados());
                put("clientesVentaConectados", sseService.getClientesVentaConectados());
            }
        });
        estado.put("timestamp", System.currentTimeMillis());
        estado.put("estado", "ACTIVO");

        return ResponseEntity.ok(estado);
    }

    /**
     * Envía una notificación de prueba mediante SSE
     */
    @PostMapping("/prueba")
    // Ejemplo de autorización:
    // @ValidateToken(roles = {SystemRoles.ADMINISTRADOR})
    public ResponseEntity<Map<String, String>> enviarNotificacionPrueba() {
        NotificacionDto notificacion = NotificacionDto.builder()
                .tipo("PRUEBA")
                .titulo("Notificación de Prueba")
                .mensaje("Sistema de notificaciones SSE funcionando correctamente")
                .timestamp(System.currentTimeMillis())
                .severidad("INFO")
                .requiereAccion(false)
                .build();

        notificacionService.enviarNotificacionPersonalizada(notificacion);

        Map<String, String> respuesta = new HashMap<>();
        respuesta.put("mensaje", "Notificación de prueba enviada");
        respuesta.put("clientesNotificados", String.valueOf(sseService.getClientesConectados()));

        return ResponseEntity.ok(respuesta);
    }

    /**
     * Envía notificación de solicitud de autorización de crédito
     */
    @PostMapping("/credito/solicitud")
    // Ejemplo de autorización:
    // @ValidateToken(roles = {SystemRoles.ADMINISTRADOR})
    public ResponseEntity<Map<String, String>> notificarSolicitudCredito(
            @RequestParam Integer solicitudId,
            @RequestParam Integer clienteId,
            @RequestParam Double monto,
            @RequestParam String nombreCliente) {

        notificacionService.notificarSolicitudAutorizacionCredito(solicitudId, clienteId, monto, nombreCliente);

        Map<String, String> respuesta = new HashMap<>();
        respuesta.put("mensaje", "Notificación de solicitud enviada");
        respuesta.put("solicitudId", solicitudId.toString());

        return ResponseEntity.ok(respuesta);
    }

    /**
     * Envía notificación de aprobación de crédito
     */
    @PostMapping("/credito/aprobada")
    // Ejemplo de autorización:
    // @ValidateToken(roles = {SystemRoles.ADMINISTRADOR})
    public ResponseEntity<Map<String, String>> notificarAprobacionCredito(
            @RequestParam Integer solicitudId,
            @RequestParam Integer clienteId,
            @RequestParam Double monto,
            @RequestParam String nombreCliente) {

        notificacionService.notificarAprobacionCredito(solicitudId, clienteId, monto, nombreCliente);

        Map<String, String> respuesta = new HashMap<>();
        respuesta.put("mensaje", "Notificación de aprobación enviada");
        respuesta.put("solicitudId", solicitudId.toString());

        return ResponseEntity.ok(respuesta);
    }

    /**
     * Envía notificación de rechazo de crédito
     */
    @PostMapping("/credito/rechazada")
    // Ejemplo de autorización:
    // @ValidateToken(roles = {SystemRoles.ADMINISTRADOR})
    public ResponseEntity<Map<String, String>> notificarRechazoCredito(
            @RequestParam Integer solicitudId,
            @RequestParam Integer clienteId,
            @RequestParam String motivo) {

        notificacionService.notificarRechazoCredito(solicitudId, clienteId, motivo);

        Map<String, String> respuesta = new HashMap<>();
        respuesta.put("mensaje", "Notificación de rechazo enviada");
        respuesta.put("solicitudId", solicitudId.toString());

        return ResponseEntity.ok(respuesta);
    }

    /**
     * Envía notificación de venta en espera de verificación
     */
    @PostMapping("/venta/verificacion-pendiente")
    // Ejemplo de autorización:
    // @ValidateToken(roles = {SystemRoles.VENDEDOR, SystemRoles.CAJA, SystemRoles.ADMINISTRADOR})
    public ResponseEntity<Map<String, String>> notificarVerificacionVenta(
            @RequestParam Integer ventaId,
            @RequestParam Integer clienteId,
            @RequestParam Double monto,
            @RequestParam String nombreCliente) {

        notificacionService.notificarVerificacionVenta(ventaId, clienteId, monto, nombreCliente);

        Map<String, String> respuesta = new HashMap<>();
        respuesta.put("mensaje", "Notificación de verificación pendiente enviada");
        respuesta.put("ventaId", ventaId.toString());

        return ResponseEntity.ok(respuesta);
    }

    /**
     * Envía notificación de autorización de venta
     */
    @PostMapping("/venta/autorizada")
    // Ejemplo de autorización:
    // @ValidateToken(roles = {SystemRoles.CAJA, SystemRoles.ADMINISTRADOR})
    public ResponseEntity<Map<String, String>> notificarAutorizacionVenta(
            @RequestParam Integer ventaId,
            @RequestParam Integer clienteId,
            @RequestParam Double monto,
            @RequestParam String nombreCliente) {

        notificacionService.notificarAutorizacionVenta(ventaId, clienteId, monto, nombreCliente);

        Map<String, String> respuesta = new HashMap<>();
        respuesta.put("mensaje", "Notificación de autorización enviada");
        respuesta.put("ventaId", ventaId.toString());

        return ResponseEntity.ok(respuesta);
    }

    /**
     * Envía notificación de rechazo de venta
     */
    @PostMapping("/venta/rechazada")
    // Ejemplo de autorización:
    // @ValidateToken(roles = {SystemRoles.CAJA, SystemRoles.ADMINISTRADOR})
    public ResponseEntity<Map<String, String>> notificarRechazoVenta(
            @RequestParam Integer ventaId,
            @RequestParam Integer clienteId,
            @RequestParam String motivo) {

        notificacionService.notificarRechazoVenta(ventaId, clienteId, motivo);

        Map<String, String> respuesta = new HashMap<>();
        respuesta.put("mensaje", "Notificación de rechazo enviada");
        respuesta.put("ventaId", ventaId.toString());

        return ResponseEntity.ok(respuesta);
    }
}
