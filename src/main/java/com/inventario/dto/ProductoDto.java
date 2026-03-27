package com.inventario.dto;

import java.util.List;

import com.inventario.models.Producto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class ProductoDto {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Post {
        @NotBlank
        @Size(max = 200)
        protected String nombre;

        @Size(max = 1000)
        protected String descripcion;

        protected List<@Positive Long> categoriaIds;

        protected List<@NotBlank @Size(max = 200) String> categoriaNombres;

        @Positive
        protected Long idProveedor;

        private List<@NotBlank @Size(max = 1000) String> fotografias;

        private List<@Positive Long> relacionadosIds;

    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Get extends Post {
        private Long id;
        private String image;
        private Long stock;
        private String proveedor;
        private java.util.List<String> categorias;

        public Get(Producto producto) {
            this.id = producto.getId();
            this.nombre = producto.getNombre();
            this.descripcion = producto.getDescripcion();
            this.stock = producto.getCantidad();
            this.categorias = producto.getCategorias() != null ? producto.getCategorias().stream().map(c -> c.getNombre()).toList() : null;
            this.image = producto.getImage();
            this.proveedor = producto.getProveedor().getNombre();
        }
    }

}
