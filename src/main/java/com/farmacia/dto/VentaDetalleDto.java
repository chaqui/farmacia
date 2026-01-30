package com.farmacia.dto;

import com.farmacia.models.DetalleVenta;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class VentaDetalleDto {

    private VentaDetalleDto() {
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    public static class Post {
        protected Integer idProducto;
        protected Long cantidad;
        protected String lote;
    }

    @Getter
    @NoArgsConstructor
    public static class Get {
        protected Long idDetalleVenta;
        protected String nombreProducto;
        protected Long cantidad;
        protected Float precioUnitario;
        protected Float subtotal;

        public Get(DetalleVenta detalleVenta) {
            this.idDetalleVenta = detalleVenta.getId();
            this.nombreProducto = detalleVenta.getLote().getProducto().getNombre();
            this.cantidad = detalleVenta.getCantidad();
            this.precioUnitario = detalleVenta.getLote().getPrecioVenta();
            this.subtotal = detalleVenta.getSubtotal();
        }
    }
}
