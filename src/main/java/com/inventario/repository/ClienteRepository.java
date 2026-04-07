package com.inventario.repository;

import com.inventario.models.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ClienteRepository extends JpaRepository<Cliente, Integer> {

	List<Cliente> findByNombreContainingIgnoreCase(String nombre);

}
