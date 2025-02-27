package com.farmacia.models;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public record Venta(

        @Id @GeneratedValue(strategy = GenerationType.IDENTITY) Integer id,

        @Column Date fecha,

        @ManyToOne @JoinColumn(name = "cliente_id", nullable = false) Cliente cliente,
        Long total) {
}
