package com.farmacia.dto;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class LoteDto {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class POST{
        protected Date fechaVencimiento;
        protected int cantidad;
        protected Float precio;
        protected String lote;
        protected Long idProducto;
    }
    
    @Getter
    @Setter
    @NoArgsConstructor
    public static class GET extends POST{
        private Long id;
    }
    

}
