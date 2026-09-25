package com.inventario.models;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.boot.autoconfigure.integration.IntegrationProperties.RSocket.Client;

import com.inventario.constants.EstadoVenta;
import com.inventario.constants.TipoPago;
import com.inventario.dto.VentaDto;

import java.util.ArrayList;

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
import jakarta.persistence.CascadeType;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "ventas")
@NoArgsConstructor
public class Venta {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Integer id;

        @Column(nullable = false, unique = true)
        private String codigoVenta;

        @Column(nullable = false)
        private LocalDate fecha;

        @Column(nullable= true, name = "cliente_nombre")
        private String nombreCliente;

        @Enumerated(EnumType.STRING)
        @Column(nullable = false)
        EstadoVenta estado = EstadoVenta.CREADA;

        @Column(nullable = true)
        Boolean esCredito;

        @Column(nullable = true)
        Float montoCredito;

        @Enumerated(EnumType.STRING)
        @Column(name = "tipo_pago", nullable = false)
        TipoPago tipoPago;

        @ManyToOne
        @JoinColumn(name = "sucursal_id", nullable = true)
        Sucursal sucursal;

        @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL, orphanRemoval = true)
        List<DetalleVenta> detalleVentas = new ArrayList<>();

        @OneToMany(mappedBy = "venta")
        List<Credito> creditos = new ArrayList<>();

        @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL, orphanRemoval = true)
        List<DevolucionVenta> devoluciones = new ArrayList<>();

        @ManyToOne 
        @JoinColumn(name = "cliente_id", nullable = true)
        Cliente cliente;

        public Float getTotal() {
                Float total = 0f;
                if (detalleVentas == null || detalleVentas.isEmpty()) return total;
                for (DetalleVenta detalle : detalleVentas) {
                        total += detalle.getSubtotal();
                }
                return total;
        }

        public Venta(VentaDto.Post ventaDto, Sucursal sucursal, Cliente cliente) {
                this(ventaDto, cliente);
                this.sucursal = sucursal;
        }

        public Venta(VentaDto.Post ventaDto, Cliente cliente) {
                this.codigoVenta = generarCodigoVenta();
                this.fecha = ventaDto.getFecha();
                this.cliente = cliente;
                this.nombreCliente = cliente != null ? cliente.getNombre() : ventaDto.getNombreCliente();
                this.tipoPago = ventaDto.getTipoPago();
                this.estado = EstadoVenta.CREADA;
        }

        private static String generarCodigoVenta() {
                // Generar código: VTA-YYYYMMDD-XXXXX (5 dígitos aleatorios)
                java.time.LocalDate hoy = java.time.LocalDate.now();
                String fecha = hoy.getYear() + String.format("%02d", hoy.getMonthValue()) 
                        + String.format("%02d", hoy.getDayOfMonth());
                String random = String.format("%05d", (int)(Math.random() * 100000));
                return "VTA-" + fecha + "-" + random;
        }

        public void addDetalle(DetalleVenta detalle) {
                detalle.setVenta(this);
                this.detalleVentas.add(detalle);
        }

        public void cambiarAVerificada(Boolean esCredito) {
                if (this.estado != EstadoVenta.CREADA) {
                        throw new IllegalStateException("La venta debe estar en estado CREADA para pasar a VERIFICADA");
                }
                this.esCredito = esCredito;
                // Si es crédito, montoCredito = total; si no es crédito, montoCredito = 0
                this.montoCredito = (esCredito != null && esCredito) ? this.getTotal() : 0f;
                this.estado = EstadoVenta.VERIFICADA;
        }

        public void cambiarAVerificada(Float montoCredito) {
                if (this.estado != EstadoVenta.CREADA) {
                        throw new IllegalStateException("La venta debe estar en estado CREADA para pasar a VERIFICADA");
                }
                this.esCredito = montoCredito != null && montoCredito > 0;
                this.montoCredito = this.esCredito ? montoCredito : 0f;
                this.estado = EstadoVenta.VERIFICADA;
        }

        public void cambiarAAutorizada() {
                if (this.estado != EstadoVenta.VERIFICADA) {
                        throw new IllegalStateException("La venta debe estar en estado VERIFICADA para pasar a AUTORIZADA");
                }
                this.estado = EstadoVenta.AUTORIZADA;
        }

        public void cambiarACancelada() {
                if (this.estado == EstadoVenta.CANCELADA) {
                        throw new IllegalStateException("La venta ya está cancelada");
                }
                if (this.estado == EstadoVenta.AUTORIZADA) {
                        throw new IllegalStateException("No se puede cancelar una venta ya autorizada");
                }
                this.estado = EstadoVenta.CANCELADA;
        }

        public boolean puedeSerVerificada() {
                return this.estado == EstadoVenta.CREADA && this.detalleVentas != null && !this.detalleVentas.isEmpty();
        }

        public boolean puedeSerAutorizada() {
                return this.estado == EstadoVenta.VERIFICADA;
        }

        public boolean puedeSercancelada() {
                return this.estado == EstadoVenta.CREADA || this.estado == EstadoVenta.VERIFICADA;
        }

}
