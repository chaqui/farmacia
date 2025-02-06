package com.farmacia.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.farmacia.models.Lote;

@Repository
public interface LoteRepository extends JpaRepository<Lote, Long> {


    public List<Lote> findByProductoId(Long idProducto);

    @Query("SELECT l FROM Lote l WHERE l.producto.id = :idProducto AND l.lote = :lote")
    public Lote findByProductoIdAndLote(Integer idProducto, String lote);

}
