package com.farmacia.dto;

import java.time.LocalDate;
import java.util.Date;

import com.farmacia.models.Lote;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class LoteDto {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class POST {
        protected LocalDate fechaVencimiento;
        protected Long cantidad;
        protected Float precio;
        protected Float precioVenta;
        protected String lote;
        protected Long idProducto;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class GET extends POST {

        private Float ganancia;

        public GET(Lote lote) {
            this.fechaVencimiento = lote.getFechaVencimiento();
            this.cantidad = lote.getCantidad();
            this.precio = lote.getPrecio();
            this.lote = lote.getLote();
            this.idProducto = lote.getProducto().getId();
            this.precioVenta = lote.getPrecioVenta();
            this.ganancia = lote.getGanancia();
        }


    }

    public static class GETLoteProducto{
        private String lote;
        private Long cantidad;
        private LocalDate fechaVencimiento;

        public GETLoteProducto(Lote lote){
            this.lote = lote.getLote();
            this.cantidad = lote.getCantidad();
            this.fechaVencimiento = lote.getFechaVencimiento();
        }

        public String getLote() {
            return lote;
        }

        public void setLote(String lote) {
            this.lote = lote;
        }

        public Long getCantidad() {
            return cantidad;
        }

        public void setCantidad(Long cantidad) {
            this.cantidad = cantidad;
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
