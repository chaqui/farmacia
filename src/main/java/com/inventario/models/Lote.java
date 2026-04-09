package com.inventario.models;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import com.inventario.dto.LoteDto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Lote {

    @Id
    private String lote;

    @Column
    private Float precio;

    @Column
    private Float precioVenta;

    @Column
    private Float precioDescuento;

    @Column
    private LocalDate fechaVencimiento;

    @Column
    // estanteria and nivel moved to Producto

    @ManyToOne
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @OneToMany(mappedBy = "lote")
    private List<DetalleVenta> detallesVenta;

    @OneToMany(mappedBy = "lote")
    private List<DetalleCompra> detallesCompra;

    public Lote(LoteDto.POST lote, Producto producto) {
        this.lote = lote.getLote();
        this.precio = lote.getPrecio();
        this.fechaVencimiento = lote.getFechaVencimiento();
        this.producto = producto;
        this.precioVenta = lote.getPrecioVenta();
        this.precioDescuento = lote.getPrecioDescuento();
    }

    public Float getGanancia() {

        return this.precioVenta == null ? this.precio : this.precioVenta - this.precio;
    }

    public Long getCantidad() {
        Long totalComprado = detallesCompra.stream()
                .mapToLong(DetalleCompra::getCantidad)
                .sum();

        Long totalVendido = detallesVenta.stream()
                .mapToLong(DetalleVenta::getCantidad)
                .sum();

        return totalComprado - totalVendido;
    }
}
