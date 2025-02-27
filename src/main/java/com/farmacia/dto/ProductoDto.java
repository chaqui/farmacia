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
        protected String descripcion;
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
        private Long stock;
        private String proveedor;
        public Get(Producto producto){
            this.id = producto.getId();
            this.nombre = producto.getNombre();
            this.descripcion = producto.getDescripcion();
            this.stock =producto.getCantidad();
            this.categoria = producto.getCategoria();
            this.image = producto.getImage();
            this.proveedor = producto.getProveedor().getNombre();
        }
    }




}
