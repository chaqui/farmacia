package com.inventario.services;

import com.inventario.dto.NotificacionDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Servicio centralizado para enviar notificaciones a través de SSE (Server-Sent Events)
 * Proporciona métodos específicos para diferentes tipos de notificaciones
 */
@Slf4j
@Service
public class NotificacionService {

    private final SseNotificacionService sseService;

    public NotificacionService(SseNotificacionService sseService) {
        this.sseService = sseService;
    }

   

    /**
     * Envía notificación de solicitud de autorización de límite de crédito
     */
    public void notificarSolicitudAutorizacionCredito(Integer solicitudId, Integer clienteId, 
                                                       Double montoSolicitado, String nombreCliente) {
        NotificacionDto notificacion = NotificacionDto.builder()
                .tipo("CREDITO_SOLICITADO")
                .titulo("Nueva Solicitud de Autorización de Crédito")
                .mensaje(String.format("El cliente %s solicita una autorización de crédito por $%.2f", 
                         nombreCliente, montoSolicitado))
                .idRelacionado(solicitudId)
                .clienteId(clienteId)
                .monto(montoSolicitado)
                .timestamp(System.currentTimeMillis())
                .severidad("WARNING")
                .requiereAccion(true)
                .estado("PENDIENTE")
                .build();
        
        log.info("Notificando solicitud de crédito: {} - Cliente: {} - Monto: ${}", 
                 solicitudId, clienteId, montoSolicitado);
        
        sseService.broadcastCredito(notificacion);
    }

    /**
     * Envía notificación de aprobación de límite de crédito
     */
    public void notificarAprobacionCredito(Integer solicitudId, Integer clienteId, 
                                          Double montoAprobado, String nombreCliente) {
        NotificacionDto notificacion = NotificacionDto.builder()
                .tipo("CREDITO_APROBADO")
                .titulo("Solicitud de Crédito Aprobada")
                .mensaje(String.format("La solicitud de crédito para %s ha sido aprobada. Monto: $%.2f", 
                         nombreCliente, montoAprobado))
                .idRelacionado(solicitudId)
                .clienteId(clienteId)
                .monto(montoAprobado)
                .timestamp(System.currentTimeMillis())
                .severidad("SUCCESS")
                .requiereAccion(false)
                .estado("APROBADO")
                .build();
        
        log.info("Notificando aprobación de crédito: {} - Cliente: {} - Monto: ${}", 
                 solicitudId, clienteId, montoAprobado);
        
        sseService.broadcastCredito(notificacion);
    }

    /**
     * Envía notificación de rechazo de límite de crédito
     */
    public void notificarRechazoCredito(Integer solicitudId, Integer clienteId, String motivo) {
        NotificacionDto notificacion = NotificacionDto.builder()
                .tipo("CREDITO_RECHAZADO")
                .titulo("Solicitud de Crédito Rechazada")
                .mensaje(String.format("La solicitud de crédito ha sido rechazada. Motivo: %s", motivo))
                .idRelacionado(solicitudId)
                .clienteId(clienteId)
                .timestamp(System.currentTimeMillis())
                .severidad("ERROR")
                .requiereAccion(false)
                .estado("RECHAZADO")
                .build();
        
        log.info("Notificando rechazo de crédito: {} - Cliente: {} - Motivo: {}", 
                 solicitudId, clienteId, motivo);
        
        sseService.broadcastCredito(notificacion);
    }

    /**
     * Envía notificación de verificación de venta pendiente
     */
    public void notificarVerificacionVenta(Integer ventaId, Integer clienteId, 
                                          Double montoVenta, String nombreCliente) {
        NotificacionDto notificacion = NotificacionDto.builder()
                .tipo("VENTA_PENDIENTE_VERIFICACION")
                .titulo("Venta en Espera de Verificación")
                .mensaje(String.format("Se requiere verificar la venta del cliente %s por $%.2f", 
                         nombreCliente, montoVenta))
                .idRelacionado(ventaId)
                .clienteId(clienteId)
                .monto(montoVenta)
                .timestamp(System.currentTimeMillis())
                .severidad("WARNING")
                .requiereAccion(true)
                .estado("PENDIENTE_VERIFICACION")
                .build();
        
        log.info("Notificando verificación pendiente de venta: {} - Cliente: {} - Monto: ${}", 
                 ventaId, clienteId, montoVenta);
        
        sseService.broadcastVenta(notificacion);
    }

    /**
     * Envía notificación de autorización de venta
     */
    public void notificarAutorizacionVenta(Integer ventaId, Integer clienteId, 
                                          Double montoVenta, String nombreCliente) {
        NotificacionDto notificacion = NotificacionDto.builder()
                .tipo("VENTA_AUTORIZADA")
                .titulo("Venta por Autorizar")
                .mensaje(String.format("La venta del cliente %s por $%.2f debe de ser autorizada", 
                         nombreCliente, montoVenta))
                .idRelacionado(ventaId)
                .clienteId(clienteId)
                .monto(montoVenta)
                .timestamp(System.currentTimeMillis())
                .severidad("SUCCESS")
                .requiereAccion(false)
                .estado("AUTORIZADO")
                .build();
        
        log.info("Notificando autorización de venta: {} - Cliente: {} - Monto: ${}", 
                 ventaId, clienteId, montoVenta);
        
        sseService.broadcastVenta(notificacion);
    }

    /**
     * Envía notificación de rechazo de venta
     */
    public void notificarRechazoVenta(Integer ventaId, Integer clienteId, String motivo) {
        NotificacionDto notificacion = NotificacionDto.builder()
                .tipo("VENTA_RECHAZADA")
                .titulo("Venta Rechazada")
                .mensaje(String.format("La venta ha sido rechazada. Motivo: %s", motivo))
                .idRelacionado(ventaId)
                .clienteId(clienteId)
                .timestamp(System.currentTimeMillis())
                .severidad("ERROR")
                .requiereAccion(false)
                .estado("RECHAZADO")
                .build();
        
        log.info("Notificando rechazo de venta: {} - Cliente: {}", ventaId, clienteId);
        
        sseService.broadcastVenta(notificacion);
    }

    /**
     * Envía notificación de una nueva devolución de venta
     */
    public void notificarDevolucion(Integer devolucionId, Integer ventaId) {
        NotificacionDto notificacion = NotificacionDto.builder()
                .tipo("DEVOLUCION_REGISTRADA")
                .titulo("Nueva Devolución de Venta")
                .mensaje(String.format("Se ha registrado una devolución para la venta ID: %d", ventaId))
                .idRelacionado(devolucionId)
                .timestamp(System.currentTimeMillis())
                .severidad("INFO")
                .requiereAccion(true)
                .estado("PENDIENTE_PROCESAMIENTO")
                .build();
        
        log.info("Notificando nueva devolución: {} - Venta: {}", devolucionId, ventaId);
        
        sseService.broadcastVenta(notificacion);
    }

    /**
     * Envía una notificación personalizada
     */
    public void enviarNotificacionPersonalizada(NotificacionDto notificacion) {
        log.info("Enviando notificación personalizada: {} - {}", 
                 notificacion.getTipo(), notificacion.getTitulo());
        
        sseService.broadcast(notificacion);
    }

    /**
     * Retorna el número de clientes SSE conectados (general)
     */
    public int getClientesConectados() {
        return sseService.getClientesConectados();
    }
}
