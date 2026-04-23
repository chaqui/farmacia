package com.inventario.dto;

import java.time.LocalDate;

import com.inventario.models.Lote;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class LoteDto {

    private LoteDto() {
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class POST  {
        protected LocalDate fechaVencimiento;
        protected Float precio;
        protected Float precioVenta;
        protected Float precioDescuento;
        protected String lote;
        protected Long idProducto;
        protected Long cantidad;

    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class GET extends POST {

        private Float ganancia;
        private Float precioDescuento;

        public GET(Lote lote) {
            this.fechaVencimiento = lote.getFechaVencimiento();
            this.cantidad = lote.getCantidad();
            this.precio = lote.getPrecio();
            this.lote = lote.getLote();
            this.idProducto = lote.getProducto().getId();
            this.precioVenta = lote.getPrecioVenta();
            this.precioDescuento = lote.getPrecioDescuento();
            this.ganancia = lote.getGanancia();
   
        }

    }

    @Getter
    public static class GETLoteProducto  {

        private String cliente;
        private Long id;
        private Float total;
        private String estado;
        private Boolean esCredito;
        private String lote;
        private Long cantidad;
        private LocalDate fechaVencimiento;

        public GETLoteProducto(Lote lote) {
            this.lote = lote.getLote();
            this.cantidad = lote.getCantidad();
            this.fechaVencimiento = lote.getFechaVencimiento();
 

        }

    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class ENVIAR {

        private String lote;
        private Long cantidad;
    }


}
