package com.inventario.dto;

import com.inventario.models.Cliente;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class ClienteDto {

    private ClienteDto() {
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Post extends Put {
        protected String nombre;

    }

    @Getter
    public static class Get extends Post {
        private Integer id;
        private Float saldoCredito;

        public Get(Cliente c) {
            this.id = c.getId();
            this.nombre = c.getNombre();
            this.tipoCliente = c.getTipoCliente();
            this.limiteCredito = c.getLimiteCredito();
            this.saldoCredito = c.getCreditoDisponible();
        }
    }

    @NoArgsConstructor
    @Getter
    @Setter
    public static class Put {
        protected Integer tipoCliente;
        protected Float limiteCredito;
    }

}
