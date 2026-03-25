package com.inventario.dto;

import com.inventario.models.DetalleSolicitud;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class SolicitudDetalleDto {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Post {
        protected Long idProducto;
        protected Long cantidad;

   
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Get extends Post {
        private Long id;
        private String producto;

        public Get(DetalleSolicitud detalleSolicitud) {
            this.id = detalleSolicitud.getId();
            this.producto = detalleSolicitud.getProducto().getNombre();
            this.idProducto = detalleSolicitud.getProducto().getId();
            this.cantidad = detalleSolicitud.getCantidad();
        }
    }

}
