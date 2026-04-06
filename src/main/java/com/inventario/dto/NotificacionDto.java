package com.inventario.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * DTO para notificaciones WebSocket
 * Contiene la información a enviar a través del socket
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificacionDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Tipo de notificación: CREDITO_SOLICITADO, CREDITO_APROBADO, CREDITO_RECHAZADO,
     * COMPRA_VERIFICADA, COMPRA_AUTORIZADA, COMPRA_RECHAZADA, etc.
     */
    private String tipo;

    /**
     * Título de la notificación
     */
    private String titulo;

    /**
     * Mensaje descriptivo de la notificación
     */
    private String mensaje;

    /**
     * ID de la solicitud o compra relacionada
     */
    private Integer idRelacionado;

    /**
     * ID del cliente relacionado
     */
    private Integer clienteId;

    /**
     * Datos adicionales en formato JSON (opcional)
     */
    private String datos;

    /**
     * Nivel de severidad: INFO, WARNING, ERROR, SUCCESS
     */
    private String severidad;

    /**
     * Timestamp de cuando se generó la notificación
     */
    private Long timestamp;

    /**
     * Indica si la notificación requiere acción del usuario
     */
    private Boolean requiereAccion;

    /**
     * Estado de la solicitud/compra
     */
    private String estado;

    /**
     * Monto relacionado (para solicitudes de crédito o compras)
     */
    private Double monto;

    /**
     * Constructor simplificado para notificaciones básicas
     */
    public static NotificacionDto crear(String tipo, String titulo, String mensaje) {
        return NotificacionDto.builder()
                .tipo(tipo)
                .titulo(titulo)
                .mensaje(mensaje)
                .timestamp(System.currentTimeMillis())
                .severidad("INFO")
                .requiereAccion(false)
                .build();
    }

    /**
     * Constructor para notificaciones con acción requerida
     */
    public static NotificacionDto crearConAccion(String tipo, String titulo, String mensaje, 
                                                   Integer idRelacionado, Double monto) {
        return NotificacionDto.builder()
                .tipo(tipo)
                .titulo(titulo)
                .mensaje(mensaje)
                .idRelacionado(idRelacionado)
                .monto(monto)
                .timestamp(System.currentTimeMillis())
                .severidad("WARNING")
                .requiereAccion(true)
                .build();
    }
}
