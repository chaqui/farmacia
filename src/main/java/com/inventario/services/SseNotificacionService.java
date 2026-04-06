package com.inventario.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inventario.dto.NotificacionDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Servicio para manejar notificaciones por Server-Sent Events (SSE)
 * Permite enviar notificaciones en tiempo real sin necesidad de que el cliente envíe datos
 */
@Slf4j
@Service
public class SseNotificacionService {

    private final CopyOnWriteArrayList<SseEmitter> emitters = new CopyOnWriteArrayList<>();
    private final CopyOnWriteArrayList<SseEmitter> creditoEmitters = new CopyOnWriteArrayList<>();
    private final CopyOnWriteArrayList<SseEmitter> ventaEmitters = new CopyOnWriteArrayList<>();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ExecutorService executor = Executors.newCachedThreadPool();

    /**
     * Registra un emitter genérico
     */
    public SseEmitter subscribe() {
        SseEmitter emitter = new SseEmitter(300000L); // 5 minutos de timeout
        emitters.add(emitter);
        
        // Configurar callbacks para limpiar
        emitter.onCompletion(() -> {
            emitters.remove(emitter);
            log.info("Cliente SSE desconectado (general) - Total: {}", emitters.size());
        });
        
        emitter.onTimeout(() -> {
            emitters.remove(emitter);
            log.warn("Timeout SSE (general) - Total: {}", emitters.size());
        });
        
        emitter.onError(throwable -> {
            emitters.remove(emitter);
            log.error("Error SSE (general)", throwable);
        });
        
        log.info("Nuevo cliente SSE conectado (general) - Total: {}", emitters.size());
        enviarMensaje(emitter, crearMensajeConexion("Conectado - Notificaciones Generales"));
        
        return emitter;
    }

    /**
     * Registra un emitter para notificaciones de crédito
     */
    public SseEmitter subscribeCredito() {
        SseEmitter emitter = new SseEmitter(300000L);
        creditoEmitters.add(emitter);
        
        emitter.onCompletion(() -> {
            creditoEmitters.remove(emitter);
            log.info("Cliente SSE desconectado (crédito) - Total: {}", creditoEmitters.size());
        });
        
        emitter.onTimeout(() -> {
            creditoEmitters.remove(emitter);
            log.warn("Timeout SSE (crédito) - Total: {}", creditoEmitters.size());
        });
        
        emitter.onError(throwable -> {
            creditoEmitters.remove(emitter);
            log.error("Error SSE (crédito)", throwable);
        });
        
        log.info("Nuevo cliente SSE conectado (crédito) - Total: {}", creditoEmitters.size());
        enviarMensaje(emitter, crearMensajeConexion("Conectado - Notificaciones de Crédito"));
        
        return emitter;
    }

    /**
     * Registra un emitter para notificaciones de venta
     */
    public SseEmitter subscribeVenta() {
        SseEmitter emitter = new SseEmitter(300000L);
        ventaEmitters.add(emitter);
        
        emitter.onCompletion(() -> {
            ventaEmitters.remove(emitter);
            log.info("Cliente SSE desconectado (venta) - Total: {}", ventaEmitters.size());
        });
        
        emitter.onTimeout(() -> {
            ventaEmitters.remove(emitter);
            log.warn("Timeout SSE (venta) - Total: {}", ventaEmitters.size());
        });
        
        emitter.onError(throwable -> {
            ventaEmitters.remove(emitter);
            log.error("Error SSE (venta)", throwable);
        });
        
        log.info("Nuevo cliente SSE conectado (venta) - Total: {}", ventaEmitters.size());
        enviarMensaje(emitter, crearMensajeConexion("Conectado - Notificaciones de Venta"));
        
        return emitter;
    }

    /**
     * Envía una notificación a todos los clientes conectados
     */
    public void broadcast(NotificacionDto notificacion) {
        String json = serializarNotificacion(notificacion);
        
        executor.execute(() -> {
            emitters.forEach(emitter -> enviarJson(emitter, json));
        });
        
        // También enviar a streams específicos según el tipo
        if (notificacion.getTipo().contains("CREDITO")) {
            executor.execute(() -> {
                creditoEmitters.forEach(emitter -> enviarJson(emitter, json));
            });
        } else if (notificacion.getTipo().contains("VENTA")) {
            executor.execute(() -> {
                ventaEmitters.forEach(emitter -> enviarJson(emitter, json));
            });
        }
    }

    /**
     * Envía notificación solo a clientes de crédito
     */
    public void broadcastCredito(NotificacionDto notificacion) {
        String json = serializarNotificacion(notificacion);
        
        executor.execute(() -> {
            creditoEmitters.forEach(emitter -> enviarJson(emitter, json));
            // También a general
            emitters.forEach(emitter -> enviarJson(emitter, json));
        });
    }

    /**
     * Envía notificación solo a clientes de venta
     */
    public void broadcastVenta(NotificacionDto notificacion) {
        String json = serializarNotificacion(notificacion);
        
        executor.execute(() -> {
            ventaEmitters.forEach(emitter -> enviarJson(emitter, json));
            // También a general
            emitters.forEach(emitter -> enviarJson(emitter, json));
        });
    }

    /**
     * Envía un JSON a un emitter específico
     */
    private void enviarJson(SseEmitter emitter, String json) {
        try {
            SseEmitter.SseEventBuilder event = SseEmitter.event()
                    .data(json)
                    .id(System.currentTimeMillis() + "")
                    .name("notificacion");
            
            emitter.send(event);
            log.debug("Notificación enviada a cliente SSE");
        } catch (IOException e) {
            log.error("Error al enviar notificación SSE", e);
            // El emitter será limpiado automáticamente por onError
        }
    }

    /**
     * Envía un mensaje a un emitter específico
     */
    private void enviarMensaje(SseEmitter emitter, NotificacionDto mensaje) {
        executor.execute(() -> {
            try {
                SseEmitter.SseEventBuilder event = SseEmitter.event()
                        .data(objectMapper.writeValueAsString(mensaje))
                        .id(System.currentTimeMillis() + "")
                        .name("notificacion");
                
                emitter.send(event);
            } catch (IOException e) {
                log.error("Error al enviar mensaje de conexión", e);
            }
        });
    }

    /**
     * Serializa una notificación a JSON
     */
    private String serializarNotificacion(NotificacionDto notificacion) {
        try {
            return objectMapper.writeValueAsString(notificacion);
        } catch (Exception e) {
            log.error("Error al serializar notificación", e);
            return "{}";
        }
    }

    /**
     * Crea un mensaje de conexión
     */
    private NotificacionDto crearMensajeConexion(String mensaje) {
        return NotificacionDto.builder()
                .tipo("CONEXION_ESTABLECIDA")
                .titulo("Conectado")
                .mensaje(mensaje)
                .timestamp(System.currentTimeMillis())
                .severidad("INFO")
                .requiereAccion(false)
                .build();
    }

    /**
     * Retorna número de clientes conectados
     */
    public int getClientesConectados() {
        return emitters.size();
    }

    public int getClientesCreditoConectados() {
        return creditoEmitters.size();
    }

    public int getClientesVentaConectados() {
        return ventaEmitters.size();
    }
}
