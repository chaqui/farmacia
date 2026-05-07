package com.inventario.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;

import com.inventario.dto.CategoriaDto;
import com.inventario.dto.ProductoDto;
import com.inventario.models.Categoria;
import com.inventario.services.CategoriaService;
import org.springframework.web.bind.annotation.ResponseStatus;


@RestController
@RequestMapping("/categorias")
public class CategoriaController {

    @Autowired
    private CategoriaService categoriaService;

    @GetMapping
    // Ejemplo de autorización:
    // @ValidateToken(roles = {SystemRoles.VENDEDOR, SystemRoles.CAJA, SystemRoles.COMPRAS, SystemRoles.ADMINISTRADOR})
    public List<CategoriaDto.GET> listar() {
        return categoriaService.obtenerCategorias().stream().map(CategoriaDto.GET::new).toList();
    }

    @GetMapping("/{id}")
    // Ejemplo de autorización:
    // @ValidateToken(roles = {SystemRoles.VENDEDOR, SystemRoles.CAJA, SystemRoles.COMPRAS, SystemRoles.ADMINISTRADOR})
    public CategoriaDto.GET obtener(@PathVariable Long id) {
        return new CategoriaDto.GET(categoriaService.obtenerCategoria(id));
    }

    @PostMapping
    // Ejemplo de autorización:
    // @ValidateToken(roles = {SystemRoles.ADMINISTRADOR})
    public CategoriaDto.GET crear(@Valid @RequestBody CategoriaDto.POST dto) {
        Categoria c = new Categoria(dto.getNombre());
        c.setDescripcion(dto.getDescripcion());
        Categoria creado = categoriaService.crearCategoria(c);
        return new CategoriaDto.GET(creado);
    }

    @DeleteMapping("/{id}")
    // Ejemplo de autorización:
    // @ValidateToken(roles = {SystemRoles.ADMINISTRADOR})
    public void eliminar(@PathVariable Long id) {
        categoriaService.eliminarCategoria(id);
    }

    @GetMapping("/{id}/productos")
    @ResponseStatus(HttpStatus.OK)
    // Ejemplo de autorización:
    // @ValidateToken(roles = {SystemRoles.VENDEDOR, SystemRoles.CAJA, SystemRoles.COMPRAS, SystemRoles.ADMINISTRADOR})
    public List<ProductoDto.Get> obtenerProductos(@PathVariable Long id) {
        return categoriaService.obtenerProductos(id);
    }
    

}
