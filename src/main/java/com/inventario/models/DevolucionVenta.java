package com.inventario.models;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.inventario.constants.EstadoVenta;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "devoluciones_venta")
@NoArgsConstructor
public class DevolucionVenta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String codigoDevolucion;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(nullable = true, length = 500)
    private String motivo;

    @ManyToOne
    @JoinColumn(name = "venta_id", nullable = false)
    private Venta venta;

    @OneToMany(mappedBy = "devolucion", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleDevolucionVenta> detalles = new ArrayList<>();

    @Column(nullable = false)
    private Boolean procesada = false;

    public Float getTotal() {
        Float total = 0f;
        if (detalles == null || detalles.isEmpty()) return total;
        for (DetalleDevolucionVenta detalle : detalles) {
            total += detalle.getSubtotal();
        }
        return total;
    }

    public void addDetalle(DetalleDevolucionVenta detalle) {
        detalle.setDevolucion(this);
        this.detalles.add(detalle);
    }
}
