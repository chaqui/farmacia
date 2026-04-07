package com.inventario.models;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "creditos")
@Data
@NoArgsConstructor
public class Credito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private Float monto;

    @Column(nullable = false)
    private LocalDate fecha;

    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @ManyToOne
    @JoinColumn(name = "venta_id", nullable = true)
    private Venta venta;

    @OneToMany(mappedBy = "credito", cascade = CascadeType.ALL)
    private java.util.List<PagoCredito> pagos = new java.util.ArrayList<>();

    public Float getSaldoPendiente() {
        float totalPagos = 0f;
        for (PagoCredito p : pagos) totalPagos += p.getMonto();
        return this.monto - totalPagos;
    }

    public Credito(Float monto, LocalDate fecha, Cliente cliente) {
        this.monto = monto;
        this.fecha = fecha;
        this.cliente = cliente;
    }

    public Credito(Float monto, LocalDate fecha, Cliente cliente, Venta venta) {
        this.monto = monto;
        this.fecha = fecha;
        this.cliente = cliente;
        this.venta = venta;
    }

}
