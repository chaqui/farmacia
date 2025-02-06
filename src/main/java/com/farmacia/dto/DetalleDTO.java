package com.farmacia.dto;

import com.farmacia.models.DetalleCompra;

public class DetalleDTO {

    public static class GET{
        public Long id;
        public String producto;
        public int cantidad;
        public Float precio;
        public Float subtotal;

        public GET(DetalleCompra detalleCompra){
            this.id = detalleCompra.getId();
            this.producto = detalleCompra.getLote().getProducto().getNombre();
            this.cantidad = detalleCompra.getCantidad();
            this.precio = detalleCompra.getLote().getPrecio();
            this.subtotal = detalleCompra.getSubtotal();
        }
       
    }

}
