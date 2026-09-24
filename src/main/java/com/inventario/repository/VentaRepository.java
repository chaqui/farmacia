package com.inventario.repository;

import com.inventario.constants.EstadoVenta;
import com.inventario.models.Venta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface VentaRepository extends JpaRepository<Venta, Integer> {
    List<Venta> findByEstado(EstadoVenta estado);

    List<Venta> findByFechaBetweenOrderByFechaDescIdDesc(LocalDate desde, LocalDate hasta);

    List<Venta> findBySucursalIdAndFechaBetweenOrderByFechaDescIdDesc(
            Long sucursalId, LocalDate desde, LocalDate hasta);
}
