package com.inventario.repository;

import com.inventario.constants.EstadoVenta;
import com.inventario.models.Venta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VentaRepository extends JpaRepository<Venta, Integer> {
    List<Venta> findByEstado(EstadoVenta estado);
}

