package com.farmacia.models;

import java.util.Date;

import com.farmacia.dto.LoteDto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
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
    private Integer cantidad;

    @Column
    private Float precio;

    @Column
    private Date fechaVencimiento;

    @ManyToOne
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    public Lote(LoteDto.POST lote, Producto producto) {
        this.lote = lote.getLote();
        this.cantidad = lote.getCantidad();
        this.precio = lote.getPrecio();
        this.fechaVencimiento = lote.getFechaVencimiento();
        this.producto = producto;
    }
}
