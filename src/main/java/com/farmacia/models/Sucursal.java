package com.farmacia.models;

import com.farmacia.dto.SucursalDto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public record Sucursal( @Id Long id, @Column String nombre)  {
    public Sucursal(SucursalDto.Post sucursal) {
        this(null, sucursal.getNombre());
    }

}
