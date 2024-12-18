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
        protected String nombre;
        protected double precio;
        protected String descripcion;
        protected int stock;
        protected String categoria;
        protected Long idProveedor;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Get extends Post{ 
        private Long id;
        private String image;

        public Get(Producto producto, Integer stock){
            this.id = producto.getId();
            this.nombre = producto.getNombre();
            this.precio = producto.getPrecio();
            this.descripcion = producto.getDescripcion();
            this.stock = stock;
            this.categoria = producto.getCategoria();
            this.image = producto.getImage();
        }
    }




}
