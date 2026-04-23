package com.inventario.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.validation.constraints.NotNull;

public class DetalleDevolucionDto {

    private DetalleDevolucionDto() {
    }

    @AllArgsConstructor
    @Getter
    @NoArgsConstructor
    @Setter
    public static class Post {
        @NotNull(message = "El ID del detalle de venta es obligatorio")
        private Integer detalleVentaId;

        @NotNull(message = "La cantidad es obligatoria")
        private Long cantidad;

        private String razonDevolucion;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Setter
    public static class Get {
        private Integer id;
        private String nombreProducto;
        private Long cantidad;
        private Float precioUnitario;
        private Float subtotal;
        private String razonDevolucion;
    }
}
