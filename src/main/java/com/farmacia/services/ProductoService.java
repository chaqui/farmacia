package com.farmacia.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.farmacia.dto.ProductoDto;
import com.farmacia.models.Producto;
import com.farmacia.repository.ProductoRepository;

@Service
public class ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    public void crearProducto(ProductoDto.Post productoDto) {
        productoRepository.save(new Producto(productoDto));
    }

    public List<ProductoDto.Get> obtenerProductos() {
        List<Producto> productos = productoRepository.findAll();
        return productos.stream().map(ProductoDto.Get::new).collect(Collectors.toList());
    }
}
