package com.farmacia.repository;

import com.farmacia.models.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

    

}
