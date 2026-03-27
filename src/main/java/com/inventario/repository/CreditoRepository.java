package com.inventario.repository;

import com.inventario.models.Credito;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CreditoRepository extends JpaRepository<Credito, Integer> {

	java.util.List<Credito> findByClienteId(Integer clienteId);

}

