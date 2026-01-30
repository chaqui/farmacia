package com.farmacia.models;

import com.farmacia.dto.VentaDetalleDto;

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
public class DetalleVenta extends Detalle {

    @ManyToOne
    @JoinColumn(name = "venta_id", nullable = false)
    private Venta venta;

    @ManyToOne
    @JoinColumn(name = "lote_id", nullable = false)
    private Lote lote;

    public Float getSubtotal() {
        return this.lote.getPrecioVenta() * this.cantidad;
    }

    public DetalleVenta(VentaDetalleDto.Post detalleVenta, Lote lote) {
        this.cantidad = detalleVenta.getCantidad();
        this.lote = lote;
    }

}
