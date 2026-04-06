package com.inventario.repository;

import com.inventario.models.AutorizacionLimiteCredito;
import com.inventario.models.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AutorizacionLimiteCreditoRepository extends JpaRepository<AutorizacionLimiteCredito, Integer> {
    
    List<AutorizacionLimiteCredito> findByClienteId(Integer clienteId);
    
    List<AutorizacionLimiteCredito> findByEstado(String estado);
    
    Optional<AutorizacionLimiteCredito> findFirstByClienteAndEstadoOrderByFechaSolicitudDesc(Cliente cliente, String estado);
    
}
