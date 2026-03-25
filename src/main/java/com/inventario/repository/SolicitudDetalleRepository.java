package com.inventario.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.inventario.models.DetalleSolicitud;

@Repository
public interface SolicitudDetalleRepository extends JpaRepository<DetalleSolicitud, Long> {
    List<DetalleSolicitud> findBySolicitudId(Long solicitudId);

    @Query("SELECT ds FROM DetalleSolicitud ds WHERE ds.solicitud.id = ?1 AND ds.producto.id = ?2")
    DetalleSolicitud findBySolicitudIdAndProductoId(Long solicitudId, Long productoId);

}
