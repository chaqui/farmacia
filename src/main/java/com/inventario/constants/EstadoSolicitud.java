package com.inventario.constants;

import lombok.Getter;

@Getter
public enum EstadoSolicitud {

    CREADA("Creada"),
    ENVIADA("Enviada"),
    RECIBIDA("Recibida"),
    RECHAZADA("Rechazada");

    private final String estado;

    EstadoSolicitud(String estado) {
        this.estado = estado;
    }
}
