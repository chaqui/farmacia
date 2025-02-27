package com.farmacia.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.farmacia.models.Sucursal;

@Repository
public interface SucursalRepository extends JpaRepository<Sucursal, Long> {
    

}
