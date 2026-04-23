package com.inventario.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = false)
public class DetalleDevolucionVenta extends Detalle {

    @ManyToOne
    @JoinColumn(name = "devolucion_id", nullable = false)
    private DevolucionVenta devolucion;

    @ManyToOne
    @JoinColumn(name = "detalle_venta_id", nullable = false)
    private DetalleVenta detalleVentaOriginal;

    @Column(nullable = true, length = 500)
    private String razonDevolucion;

    public Float getSubtotal() {
        return this.detalleVentaOriginal.getLote().getPrecioVenta() * this.cantidad;
    }
}
