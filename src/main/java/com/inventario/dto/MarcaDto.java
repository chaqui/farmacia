package com.inventario.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class MarcaDto {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Post {
        private String nombre;
        private String descripcion;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Get {
        private Integer id;
        private String nombre;
        private String descripcion;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Put {
        private String nombre;
        private String descripcion;
    }
}
