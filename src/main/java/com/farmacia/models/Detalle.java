package com.farmacia.models;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

@MappedSuperclass
@Getter
@Setter
public abstract class Detalle {

    @Id
    private Long id;

    @Column
    protected Long cantidad;

}
