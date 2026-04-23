package com.inventario.dto;

import com.inventario.models.PagoCredito;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

public class PagoCreditoDto {

    private PagoCreditoDto() {}

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Post {
        protected Float monto;
    }

    @Getter
    public static class Get extends Post {
        private Long id;
        private LocalDate fecha;

        public Get(PagoCredito p) {
            this.id = p.getId();
            this.monto = p.getMonto();
            this.fecha = p.getFecha();
        }
    }

}
