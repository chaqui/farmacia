package com.farmacia.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

    @PostMapping("/{id}/productos")
    public void agregarProducto(Long id, ProductoDto.Post productoDto) throws HttpException {
        proveedorService.agregarProducto(id, productoDto);
    }

    @DeleteMapping("/{id}")
    public void eliminarProveedor(Long id) {
        proveedorService.eliminarProveedor(id);
    }

}
