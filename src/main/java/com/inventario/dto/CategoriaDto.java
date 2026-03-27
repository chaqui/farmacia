package com.inventario.dto;

import com.inventario.models.Categoria;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class CategoriaDto {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class POST {
        @NotBlank
        @Size(max = 100)
        private String nombre;

        @Size(max = 500)
        private String descripcion;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GET {
        private Long id;
        private String nombre;
        private String descripcion;

        public GET(Categoria categoria) {
            this.id = categoria.getId();
            this.nombre = categoria.getNombre();
            this.descripcion = categoria.getDescripcion();
        }
    }

}
