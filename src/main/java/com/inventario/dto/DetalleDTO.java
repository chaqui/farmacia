package com.inventario.dto;

import com.inventario.models.DetalleCompra;

import lombok.Getter;
import lombok.Setter;

public class DetalleDTO {

    @Getter
    @Setter
    public static class GET{
        private Long id;
        private String producto;
        private Long cantidad;
        private Float precio;
        private Float subtotal;
        private String lote;

        public GET(DetalleCompra detalleCompra){
            this.id = detalleCompra.getId();
            this.producto = detalleCompra.getLote().getProducto().getNombre();
            this.cantidad = detalleCompra.getCantidad();
            this.precio = detalleCompra.getLote().getPrecio();
            this.subtotal = detalleCompra.getSubtotal();
            this.lote = detalleCompra.getLote().getLote();
        }
       
    }

}
