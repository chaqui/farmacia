package com.inventario.repository;

import com.inventario.models.DetalleDevolucionVenta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DetalleDevolucionVentaRepository extends JpaRepository<DetalleDevolucionVenta, Integer> {
    List<DetalleDevolucionVenta> findByDevolucionId(Integer devolucionId);
}
