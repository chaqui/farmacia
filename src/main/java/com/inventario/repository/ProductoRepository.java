package com.inventario.repository;

import com.inventario.models.Producto;
import com.inventario.models.Categoria;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    public List<Producto> findByNombreContainingIgnoreCase(String nombre);

    public List<Producto> findByNombreContainingIgnoreCaseOrDescripcionContainingIgnoreCase(String nombre, String descripcion);

    public java.util.List<Producto> findDistinctByCategoriasIn(java.util.Collection<Categoria> categorias);

}
