package com.farmacia.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.farmacia.dto.ProductoDto;
import com.farmacia.dto.ProveedorDto;
import com.farmacia.exception.HttpException;
import com.farmacia.services.ProveedorService;

import lombok.extern.java.Log;

@RestController
@RequestMapping("/proveedores")
@Log
public class ProveedorController {

    @Autowired
    private ProveedorService proveedorService;

    @PostMapping
    public void crearProveedor(@RequestBody ProveedorDto.POST proveedorDto) {
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
    public void agregarProducto(@PathVariable Long id, @RequestBody ProductoDto.Post productoDto) throws HttpException {
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
