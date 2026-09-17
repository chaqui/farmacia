package com.inventario.models;


import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class DetalleCompra extends Detalle {


    @ManyToOne
    @JoinColumn(name = "compra_id", nullable = false)
    private Compra compra;

    @ManyToOne
    @JoinColumn(name = "lote_id", nullable = false)
    private Lote lote;



    public DetalleCompra(Compra compra, Lote lote, Long cantidad) {
        this.compra = compra;
        this.lote = lote;
        this.cantidad = cantidad;
    }

    public Float getSubtotal() {
        return lote.getPrecio() * cantidad;
    }

    public String nombreProveedor(){
        return this.getCompra().getProveedor().getNombre();
        
    }

    public Producto getProducto(){
        return this.lote.getProducto();
    }
}
