package com.inventario.models;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "pagos_credito")
@Data
@NoArgsConstructor
public class PagoCredito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "credito_id", nullable = false)
    private Credito credito;

    @Column(nullable = false)
    private Float monto;

    @Column(nullable = false)
    private LocalDate fecha;

    public PagoCredito(Credito credito, Float monto, LocalDate fecha) {
        this.credito = credito;
        this.monto = monto;
        this.fecha = fecha;
    }
}
