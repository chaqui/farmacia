package com.inventario.models;

import java.util.ArrayList;
import java.util.List;

import com.inventario.dto.ProveedorDto;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Proveedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
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

    @OneToMany(mappedBy = "proveedor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Compra> compras;

    @ManyToMany
    @JoinTable(
        name = "proveedor_marca",
        joinColumns = @JoinColumn(name = "proveedor_id"),
        inverseJoinColumns = @JoinColumn(name = "marca_id")
    )
    private List<Marca> marcas = new ArrayList<>();

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

