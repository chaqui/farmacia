package com.farmacia.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.farmacia.dto.LoteDto;
import com.farmacia.dto.ProductoDto;
import com.farmacia.exception.HttpException;
import com.farmacia.models.Producto;
import com.farmacia.models.Proveedor;
import com.farmacia.repository.ProductoRepository;

@Service
public class ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private LoteService loteService;

    public void crearProducto(ProductoDto.Post productoDto, Proveedor proveedor) {
        productoRepository.save(new Producto(productoDto, proveedor));
    }

    public List<ProductoDto.Get> obtenerProductos() {
        return productoRepository.findAll().stream().map(ProductoDto.Get::new).collect(Collectors.toList());
    }

    public Producto obtenerProducto(Long id) throws HttpException {
        Producto producto = productoRepository.findById(id).orElse(null);
        if (producto == null) {
            throw new HttpException("Producto no encontrado");
        }
        return producto;
    }

    public void agregarNuevoLote(Long idProducto, LoteDto.POST loteDto) throws HttpException {
        Producto producto = this.obtenerProducto(idProducto);
        this.loteService.agregarNuevoLote(loteDto, producto);
    }
}
