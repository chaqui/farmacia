package com.farmacia.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.farmacia.models.SucursalLote;

@Repository
public interface SucursalLoteRepository extends JpaRepository<SucursalLote, Long> {

    @Query("SELECT sl FROM SucursalLote sl WHERE sl.sucursal.id = ?1 AND sl.lote.lote = ?2")
    SucursalLote findBySucursalIdAndLoteId(Long idSucursal, String lote);

}
