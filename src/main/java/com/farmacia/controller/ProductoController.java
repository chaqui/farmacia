package com.farmacia.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.farmacia.dto.LoteDto;
import com.farmacia.dto.ProductoDto;
import com.farmacia.exception.HttpException;
import com.farmacia.services.ProductoService;

import lombok.extern.log4j.Log4j2;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/productos")
@Log4j2
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    @GetMapping
    public List<ProductoDto.Get> obtenerProductos() {
        return productoService.obtenerProductos();
    }

    @GetMapping("/{id}")
    public ProductoDto.Get obtenerProducto(@PathVariable  Long id) throws HttpException {
        return new ProductoDto.Get(productoService.obtenerProducto(id));
    }

    @GetMapping("/{id}/lotes")
    public List<LoteDto.GET> obtenerLotes( @PathVariable Long id) throws HttpException {
        log.info("Obteniendo lotes del producto con id: " + id);
        return this.productoService.obtenerLotes(id).stream().map(lote -> new LoteDto.GET(lote))
                .collect(Collectors.toList());
    }

}