package com.farmacia.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.farmacia.dto.ProductoDto;
import com.farmacia.services.ProductoService;

import java.util.List;

@RestController
@RequestMapping("/productos")
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    @PostMapping
    public void crearProducto(@RequestBody ProductoDto.Post productoDto) {
        productoService.crearProducto(productoDto);
    }

    @GetMapping
    public List<ProductoDto.Get> obtenerProductos() {
        return productoService.obtenerProductos();
    }
}