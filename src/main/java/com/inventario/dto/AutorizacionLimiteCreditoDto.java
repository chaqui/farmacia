package com.inventario.dto;

import com.inventario.models.AutorizacionLimiteCredito;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

public class AutorizacionLimiteCreditoDto {

    private AutorizacionLimiteCreditoDto() {}

    @Getter
    @Setter
    @NoArgsConstructor
    public static class RechazoRequest {
        protected String razonRechazo;
    }

    @Getter
    public static class Get {
        private Integer id;
        private Integer clienteId;
        private String clienteNombre;
        private Float limiteCredito;
        private String estado;
        private LocalDateTime fechaSolicitud;
        private LocalDateTime fechaAutorizacion;
        private String razonRechazo;

        public Get(AutorizacionLimiteCredito a) {
            this.id = a.getId();
            this.clienteId = a.getCliente().getId();
            this.clienteNombre = a.getCliente().getNombre();
            this.limiteCredito = a.getLimiteCredito();
            this.estado = a.getEstado();
            this.fechaSolicitud = a.getFechaSolicitud();
            this.fechaAutorizacion = a.getFechaAutorizacion();
            this.razonRechazo = a.getRazonRechazo();
        }
    }

}
