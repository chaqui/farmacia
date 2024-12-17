package com.farmacia.models;

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

    @Column
    private String estado;
}

