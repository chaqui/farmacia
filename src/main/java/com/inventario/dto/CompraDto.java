package com.inventario.dto;

import java.util.Date;
import java.util.List;

import com.inventario.models.Compra;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class CompraDto {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class POST {
        protected Long idProveedor;
        protected Date fecha;
        private List<LoteDto.POST> lotes;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class GET extends POST {
        private Long id;
        private String proveedor;
        private Float total;

        public GET(Compra compra) {
            this.id = compra.getId();
            this.proveedor = compra.getProveedor().getNombre();
            this.fecha = compra.getFecha();
            this.total = compra.getTotal();
            this.idProveedor = compra.getProveedor().getId();
        }
    }

}
