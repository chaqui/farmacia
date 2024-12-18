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
        private Date fechaVencimiento;
        private int cantidad;
        private Float precio;
        private String lote;
    }

}
