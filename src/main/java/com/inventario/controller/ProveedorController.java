package com.inventario.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.inventario.dto.ProductoDto;
import com.inventario.dto.ProveedorDto;
import com.inventario.exception.HttpException;
import com.inventario.services.ProveedorService;

import jakarta.validation.Valid;
import lombok.extern.java.Log;

@RestController
@RequestMapping("/proveedores")
@Log
public class ProveedorController {

    @Autowired
    private ProveedorService proveedorService;

    @PostMapping
    public void crearProveedor(@Valid @RequestBody ProveedorDto.POST proveedorDto) {
        proveedorService.crearProveedor(proveedorDto);
    }

    @GetMapping
    public List<ProveedorDto.GET> obtenerProveedores() {
        return proveedorService.obtenerProveedores().stream().map(ProveedorDto.GET::new).toList();
    }

    @GetMapping
    @RequestMapping("/{id}")
    public ProveedorDto.GET obtenerProveedor(@PathVariable Long id) throws HttpException {
        return new ProveedorDto.GET(proveedorService.obtenerProveedor(id));
    }

    @PostMapping("/{id}/productos")
    public void agregarProducto(@PathVariable Long id, @Valid @RequestBody ProductoDto.Post productoDto) throws HttpException {
        proveedorService.agregarProducto(id, productoDto);
    }

    @GetMapping("/{id}/productos")
    public List<ProductoDto.Get> obtenerProductos(@PathVariable Long id) throws HttpException {
        return proveedorService.obtenerProductos(id);

    }

    @DeleteMapping("/{id}")
    public void eliminarProveedor(@PathVariable Long id) {
        log.info(id.toString());
        proveedorService.eliminarProveedor(id);
    }

}
