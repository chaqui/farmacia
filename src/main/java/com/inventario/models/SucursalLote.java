package com.inventario.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Entity
@Data
public class SucursalLote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column
    Long cantidad;

    @ManyToOne
    @JoinColumn(name = "lote_id", nullable = false)
    Lote lote;

    @ManyToOne
    @JoinColumn(name = "sucursal_id", nullable = false)
    Sucursal sucursal;

    public SucursalLote(Long cantidad, Lote lote, Sucursal sucursal) {
        this.cantidad = cantidad;
        this.lote = lote;
        this.sucursal = sucursal;
    }

}
