package com.inventario.dto;

import java.time.LocalDate;
import java.util.List;

import com.inventario.constants.EstadoVenta;
import com.inventario.models.Venta;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
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


        @NotNull(message = "La fecha es obligatoria")
        @PastOrPresent(message = "La fecha no puede ser futura")
        protected LocalDate fecha;


        private List<VentaDetalleDto.Post> detalles;

        protected Integer clienteId;

        protected String nombreCliente;

        /**
         * Constructor para Ventas Sin Sucursal
         * 
         * @param clienteId  id del cliente
         * @param fecha      fecha de la venta
         * @param idProducto id del producto
         * @param cantidad   cantidad vendida
         * @param loteId     id del lote
         */
        public Post(Integer cliente, LocalDate fecha) {
            this.clienteId = cliente;
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
        private EstadoVenta estado;
        private Boolean esCredito;
        private Float montoCredito;

        public GetConSucursal(Venta venta) {
            super(venta.getCliente().getId(), venta.getFecha());
            this.id = venta.getId();
            this.sucursal = venta.getSucursal().getNombre();
            this.total = venta.getTotal();
            this.estado = venta.getEstado();
            this.esCredito = venta.getEsCredito();
            this.montoCredito = venta.getMontoCredito();
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
        private EstadoVenta estado;
        private Boolean esCredito;
        private Float montoCredito;

        public GetSinSucursal(Venta venta) {
            super(venta.getCliente() != null ? venta.getCliente().getId() : null, venta.getFecha());
            this.id = venta.getId();
            this.cliente = venta.getCliente() != null ? venta.getCliente().getNombre() : venta.getNombreCliente();
            this.total = venta.getTotal();
            this.estado = venta.getEstado();
            this.esCredito = venta.getEsCredito();
            this.montoCredito = venta.getMontoCredito();
            
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Verificar {
        private Boolean esCredito;
        private Float montoCredito;
    }
}
