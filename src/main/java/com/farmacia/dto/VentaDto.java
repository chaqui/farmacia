package com.farmacia.dto;

import java.time.LocalDate;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

import com.farmacia.models.Venta;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class VentaDto {

    private VentaDto() {
    }

    @AllArgsConstructor
    @Getter
    @NoArgsConstructor
    @Setter
    public static class Post {

        @NotBlank(message = "El cliente es obligatorio")
        private String cliente;

        @NotNull(message = "La fecha es obligatoria")
        @PastOrPresent(message = "La fecha no puede ser futura")
        protected LocalDate fecha;


        private List<VentaDetalleDto.Post> detalles;

        /**
         * Constructor para Ventas Sin Sucursal
         * 
         * @param clienteId  id del cliente
         * @param fecha      fecha de la venta
         * @param idProducto id del producto
         * @param cantidad   cantidad vendida
         * @param loteId     id del lote
         */
        public Post(String cliente, LocalDate fecha) {
            this.cliente = cliente;
            this.fecha = fecha;

        }
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Setter
    public static class GetConSucursal extends Post {
        private Integer id;

        private String sucursal;
        private Float total;

        public GetConSucursal(Venta venta) {
            super(venta.getClienteNombre(), venta.getFecha());
            this.id = venta.getId();
            this.sucursal = venta.getSucursal().getNombre();
            this.total = venta.getTotal();
        }
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Setter
    public static class GetSinSucursal extends Post {
        private Integer id;
        private String cliente;
        private Float total;

        public GetSinSucursal(Venta venta) {
            super(venta.getClienteNombre(), venta.getFecha());
            this.id = venta.getId();
            this.cliente = venta.getClienteNombre();
            this.total = venta.getTotal();
        }
    }
}
