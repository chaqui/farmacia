package com.farmacia.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.farmacia.models.Lote;

@Repository
public interface LoteRepository extends JpaRepository<Lote, Long> {


    public List<Lote> findByProductoId(Long idProducto);

    public Lote findByProductoIdAndLote(Integer idProducto, String lote);

}
