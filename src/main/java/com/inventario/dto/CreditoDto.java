package com.inventario.dto;

import com.inventario.models.Credito;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

public class CreditoDto {

    private CreditoDto() {}

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Post {
        protected Float monto;
        protected LocalDate fecha;
        protected Integer clienteId;
        protected Integer ventaId;
    }

    @Getter
    public static class Get extends Post {
        private Integer id;
        private Float saldoPendiente;
        private Integer pagosCount;

        public Get(Credito c) {
            this.id = c.getId();
            this.monto = c.getMonto();
            this.fecha = c.getFecha();
            this.clienteId = c.getCliente().getId();
            this.ventaId = c.getVenta() != null ? c.getVenta().getId() : null;
            this.saldoPendiente = c.getSaldoPendiente();
            this.pagosCount = c.getPagos() != null ? c.getPagos().size() : 0;
        }
    }

}
