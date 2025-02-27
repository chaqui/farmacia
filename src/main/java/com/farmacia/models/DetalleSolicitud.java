package com.farmacia.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;

import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class DetalleSolicitud extends Detalle {

    @ManyToOne
    @JoinColumn(name = "solicitud_id", nullable = false)
    private Solicitud solicitud;

    @ManyToOne
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @Column
    private Long cantidadEnvida;

    public DetalleSolicitud(Solicitud solicitud, Producto producto, Long cantidad) {
        this.solicitud = solicitud;
        this.producto = producto;
        this.cantidad = cantidad;
    }

    public Long getFaltante() {
        return cantidad - cantidadEnvida;
    }

}
