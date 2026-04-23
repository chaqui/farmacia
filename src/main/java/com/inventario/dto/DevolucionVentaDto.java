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
import com.inventario.dto.DetalleDevolucionDto;

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
        private List<DetalleDevolucionDto.Post> detalles;
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
        private List<DetalleDevolucionDto.Get> detalles;

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
}
