package com.farmacia.dto;

import com.farmacia.models.Producto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class ProductoDto {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Post{
        private String nombre;
        private double precio;
        private String descripcion;
        private int stock;
        private String categoriaId;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Get{
        private Long id;
        private String nombre;
        private double precio;
        private String descripcion;
        private int stock;
        private String categoria;
        private String image;


        public Get(Producto producto){
            this.id = producto.getId();
            this.nombre = producto.getNombre();
            this.precio = producto.getPrecio();
            this.descripcion = producto.getDescripcion();
            this.stock = producto.getStock();
            this.categoria = producto.getCategoria();
            this.image = producto.getImage();
        }
    }




}
