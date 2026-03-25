package com.inventario.models;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.inventario.dto.VentaDto;

import java.util.ArrayList;

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
@Data
@Table(name = "ventas")
@NoArgsConstructor
public class Venta {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        Integer id;

        @Column(nullable = false)
        LocalDate fecha;

        @Column(nullable = false)
        String clienteNombre;

        @ManyToOne
        @JoinColumn(name = "sucursal_id", nullable = true)
        Sucursal sucursal;

        @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL, orphanRemoval = true)
        List<DetalleVenta> detalleVentas = new ArrayList<>();

        public Float getTotal() {
                Float total = 0f;
                if (detalleVentas == null || detalleVentas.isEmpty()) return total;
                for (DetalleVenta detalle : detalleVentas) {
                        total += detalle.getSubtotal();
                }
                return total;
        }

        public Venta(VentaDto.Post ventaDto, Sucursal sucursal) {
                this.fecha = ventaDto.getFecha();
                this.clienteNombre = ventaDto.getCliente();
                this.sucursal = sucursal;
        }

        public Venta(VentaDto.Post ventaDto) {
                this.fecha = ventaDto.getFecha();
                this.clienteNombre = ventaDto.getCliente();
        }

        public void addDetalle(DetalleVenta detalle) {
                detalle.setVenta(this);
                this.detalleVentas.add(detalle);
        }

}
