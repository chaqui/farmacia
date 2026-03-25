package com.inventario.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.inventario.models.Compra;

@Repository
public interface CompraRepository extends JpaRepository<Compra, Long> {

}
