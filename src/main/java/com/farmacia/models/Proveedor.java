package com.farmacia.models;

import com.farmacia.dto.ProveedorDto;

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
public class Proveedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(unique = true)
    private String nombre;

    @Column
    private String direccion;

    @Column
    private String telefono;

    @Column
    private String email;

    @Column
    private String contacto;

    public Proveedor(ProveedorDto.POST proveedor){
        this.nombre = proveedor.nombre;
        this.direccion = proveedor.direccion;
        this.telefono = proveedor.telefono;
        this.email = proveedor.email;
        this.contacto = proveedor.contacto;
    }

    public void update(ProveedorDto.PUT proveedor){
        this.nombre = proveedor.nombre;
        this.direccion = proveedor.direccion;
        this.telefono = proveedor.telefono;
    }

}

