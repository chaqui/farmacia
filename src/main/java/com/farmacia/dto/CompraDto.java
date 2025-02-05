package com.farmacia.dto;


import java.util.Date;
import java.util.List;

import com.farmacia.models.Compra;

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
        private Long idProveedor;
        private Date fecha;
        private List<LoteDto.POST> lotes;
    
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class GET{
        private Long id;
        private String proveedor;
        private Date fecha;

        public GET(Compra compra){
            this.id = compra.getId();
            this.proveedor = compra.getProveedor().getNombre();
            this.fecha = compra.getFecha();
          
        }
    }


}
