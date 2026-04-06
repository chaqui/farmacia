package com.inventario.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "autorizaciones_limite_credito")
@Data
@NoArgsConstructor
public class AutorizacionLimiteCredito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @Column(nullable = false)
    private Float limiteCredito;

    @Column(nullable = false)
    private String estado; // PENDIENTE, APROBADO, RECHAZADO

    @Column(nullable = false)
    private LocalDateTime fechaSolicitud;

    @Column
    private LocalDateTime fechaAutorizacion;

    @Column
    private String razonRechazo;

    public AutorizacionLimiteCredito(Cliente cliente, Float limiteCredito) {
        this.cliente = cliente;
        this.limiteCredito = limiteCredito;
        this.estado = "PENDIENTE";
        this.fechaSolicitud = LocalDateTime.now();
    }

    public void aprobar() {
        this.estado = "APROBADO";
        this.fechaAutorizacion = LocalDateTime.now();
    }

    public void rechazar(String razonRechazo) {
        this.estado = "RECHAZADO";
        this.fechaAutorizacion = LocalDateTime.now();
        this.razonRechazo = razonRechazo;
    }

    public boolean estaPendiente() {
        return "PENDIENTE".equals(this.estado);
    }

    public boolean estaAprobado() {
        return "APROBADO".equals(this.estado);
    }

    public boolean estaRechazado() {
        return "RECHAZADO".equals(this.estado);
    }

}
