package com.inventario.repository;

import com.inventario.models.DevolucionVenta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DevolucionVentaRepository extends JpaRepository<DevolucionVenta, Integer> {
    List<DevolucionVenta> findByVentaId(Integer ventaId);
}
