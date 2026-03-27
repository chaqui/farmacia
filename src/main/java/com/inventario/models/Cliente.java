package com.inventario.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "clientes")
@Data
@NoArgsConstructor
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String nombre;

    @Column
    private Integer tipoCliente;

    @Column
    private Float limiteCredito;

    @OneToMany(mappedBy = "cliente")
    List<Credito> creditos = new ArrayList<>();

    public Cliente(String nombre) {
        this.nombre = nombre;
    }

    public void cambiarLimiteCredito(Float nuevoLimite) {
        this.limiteCredito = nuevoLimite;
    }

    public Float getSaldoCredito() {
        if (creditos == null || creditos.isEmpty()) return 0f;
        float total = 0f;
        for (Credito c : creditos) {
            if (c != null && c.getMonto() != null) total += c.getMonto();
        }
        return total;
    }

}
