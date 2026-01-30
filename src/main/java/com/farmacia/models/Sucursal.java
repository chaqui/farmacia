package com.farmacia.models;

import java.util.List;

import com.farmacia.dto.SucursalDto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Sucursal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column
    String nombre;

    @Column
    Boolean activo;

    @OneToMany(mappedBy = "sucursal")
    List<Solicitud> solicitudes;

    @OneToMany(mappedBy = "sucursal")
    List<Venta> ventas;
    
    
    public Sucursal(SucursalDto.Post sucursal) {
        this.nombre = sucursal.getNombre();
    }


}