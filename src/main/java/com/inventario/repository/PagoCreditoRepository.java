package com.inventario.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.inventario.models.PagoCredito;

@Repository
public interface PagoCreditoRepository extends JpaRepository<PagoCredito, Long> {

}
