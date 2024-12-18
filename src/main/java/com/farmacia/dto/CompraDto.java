package com.farmacia.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class CompraDto {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class POST extends LoteDto.POST{
        private Long idProducto;
        private Long idProveedor;
    
    }
}
