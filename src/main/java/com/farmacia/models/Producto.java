package com.farmacia.models;



import java.util.Date;

import com.farmacia.dto.ProductoDto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String nombre;

    @Column
    private String descripcion;

    @Column
    private double precio;

    @Column
    private String categoria;

    @Column
    private String image; 
    
    @Column
    private int stock;

    @Column
    private Date fechaVencimiento;

    public Producto(ProductoDto.Post dto){
        this.nombre = dto.getNombre();
        this.precio = dto.getPrecio();
        this.descripcion = dto.getDescripcion();
        this.stock = dto.getStock();
        this.categoria = dto.getCategoriaId();
    }
}
