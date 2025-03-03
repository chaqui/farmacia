package com.farmacia.dto;

import com.farmacia.models.Sucursal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class SucursalDto {

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Post {
        protected String nombre;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Get extends Post {
        private Long id;

        public Get(Sucursal sucursal) {
            this.id = sucursal.getId();
            this.nombre = sucursal.getNombre();
        }
    }

}
