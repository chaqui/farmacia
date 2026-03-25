package com.inventario.models;

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
public class Fotografia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String url;

    private Integer orden;

    @ManyToOne
    @JoinColumn(name = "producto_id")
    private Producto producto;

    public Fotografia(String url, Integer orden, Producto producto) {
        this.url = url;
        this.orden = orden;
        this.producto = producto;
    }

}
