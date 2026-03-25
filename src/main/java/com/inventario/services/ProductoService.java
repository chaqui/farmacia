package com.inventario.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.inventario.dto.ProductoDto;
import com.inventario.exception.HttpException;
import com.inventario.models.Lote;
import com.inventario.models.Producto;
import com.inventario.models.Proveedor;
import com.inventario.repository.ProductoRepository;

@Service
public class ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    public void crearProducto(ProductoDto.Post productoDto, Proveedor proveedor) {
        productoRepository.save(new Producto(productoDto, proveedor));
    }

    public List<ProductoDto.Get> obtenerProductos() {
        return productoRepository.findAll().stream().map(ProductoDto.Get::new).collect(Collectors.toList());
    }

    public Producto obtenerProducto(Long id) throws HttpException {
        Producto producto = productoRepository.findById(id).orElse(null);
        if (producto == null) {
            throw new HttpException("Producto no encontrado",404);
        }
        return producto;
    }

    public List<Lote> obtenerLotes(Long id) throws HttpException {
        Producto producto = this.obtenerProducto(id);
        return producto.getLotes();
    }

    public List<ProductoDto.Get> buscarProductosPorNombre(String nombre) {
        return productoRepository.findByNombreContainingIgnoreCase(nombre)
                .stream()
                .map(ProductoDto.Get::new).toList();
    }

    public List<ProductoDto.Get> buscarProductos(String query) {
        return productoRepository.findByNombreContainingIgnoreCaseOrDescripcionContainingIgnoreCase(query, query)
                .stream().map(ProductoDto.Get::new).toList();
    }
}
