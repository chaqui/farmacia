package com.inventario.dto;

import java.time.LocalDate;
import java.util.List;

import com.inventario.models.DevolucionVenta;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class DevolucionVentaDto {

    private DevolucionVentaDto() {
    }

    @AllArgsConstructor
    @Getter
    @NoArgsConstructor
    @Setter
    public static class Post {
        @NotNull(message = "El ID de la venta es obligatorio")
        private Integer ventaId;

        @NotNull(message = "La fecha es obligatoria")
        @PastOrPresent(message = "La fecha no puede ser futura")
        private LocalDate fecha;

        private String motivo;

        @NotNull(message = "Los detalles son obligatorios")
        private List<DetallePost> detalles;

        @AllArgsConstructor
        @Getter
        @NoArgsConstructor
        @Setter
        public static class DetallePost {
            @NotNull(message = "El ID del detalle de venta es obligatorio")
            private Integer detalleVentaId;

            @NotNull(message = "La cantidad es obligatoria")
            private Long cantidad;

            private String razonDevolucion;
        }
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Setter
    public static class Get {
        private Integer id;
        private String codigoDevolucion;
        private LocalDate fecha;
        private Integer ventaId;
        private String nombreCliente;
        private String motivo;
        private Float total;
        private Boolean procesada;
        private List<DetalleGet> detalles;

        public Get(DevolucionVenta devolucion) {
            this.id = devolucion.getId();
            this.codigoDevolucion = devolucion.getCodigoDevolucion();
            this.fecha = devolucion.getFecha();
            this.ventaId = devolucion.getVenta().getId();
            this.nombreCliente = devolucion.getVenta().getCliente() != null 
                ? devolucion.getVenta().getCliente().getNombre() 
                : devolucion.getVenta().getNombreCliente();
            this.motivo = devolucion.getMotivo();
            this.total = devolucion.getTotal();
            this.procesada = devolucion.getProcesada();
        }
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Setter
    public static class DetalleGet {
        private Integer id;
        private String nombreProducto;
        private Long cantidad;
        private Float precioUnitario;
        private Float subtotal;
        private String razonDevolucion;
    }
}
