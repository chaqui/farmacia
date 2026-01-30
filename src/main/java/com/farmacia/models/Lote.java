package com.farmacia.models;

import java.time.LocalDate;
import java.util.Date;

import com.farmacia.dto.LoteDto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Lote {

    @Id
    private String lote;

    @Column
    private Long cantidad;

    @Column
    private Float precio;

    @Column
    private Float precioVenta;

    @Column
    private LocalDate fechaVencimiento;

    @ManyToOne
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    public Lote(LoteDto.POST lote, Producto producto) {
        this.lote = lote.getLote();
        this.cantidad = lote.getCantidad();
        this.precio = lote.getPrecio();
        this.fechaVencimiento = lote.getFechaVencimiento();
        this.producto = producto;
        this.precioVenta = lote.getPrecioVenta();
    }

    public Float getGanancia() {

        return this.precioVenta == null ? this.precio : this.precioVenta - this.precio;
    }
}
