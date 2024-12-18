package com.farmacia.models;



import java.util.Date;

import org.hibernate.annotations.ForeignKey;

import com.farmacia.dto.ProductoDto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
    private Date fechaVencimiento;

    @ManyToOne
    @JoinColumn(name = "proveedor_id", nullable = false)
    private Proveedor proveedor;

    public Producto(ProductoDto.Post dto, Proveedor proveedor) {
        this.nombre = dto.getNombre();
        this.precio = dto.getPrecio();
        this.descripcion = dto.getDescripcion();
        this.categoria = dto.getCategoria();
        this.proveedor = proveedor;
    }
}
