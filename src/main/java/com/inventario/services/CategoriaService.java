package com.inventario.services;

import org.springframework.stereotype.Service;

import com.inventario.models.Categoria;
import com.inventario.repository.CategoriaRepository;
import java.util.List;
import java.util.Optional;

@Service
public class CategoriaService {

    private CategoriaRepository categoriaRepository;

    public CategoriaService(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    public Categoria obtenerCategoria(Long id) {
        return categoriaRepository.findById(id).orElseThrow(() -> new RuntimeException("Categoría no encontrada"));
    }

    public List<Categoria> obtenerCategorias() {
        return categoriaRepository.findAll();
    }

    public Categoria crearSiNoExiste(String nombre) {
        if (nombre == null || nombre.isBlank()) throw new IllegalArgumentException("Nombre de categoría vacío");
        Optional<Categoria> opt = categoriaRepository.findByNombreIgnoreCase(nombre);
        if (opt.isPresent()) return opt.get();
        Categoria cat = new Categoria(nombre);
        return categoriaRepository.save(cat);
    }

    public Categoria crearCategoria(Categoria categoria) {
        return categoriaRepository.save(categoria);
    }

    public void eliminarCategoria(Long id) {
        categoriaRepository.deleteById(id);
    }
}
