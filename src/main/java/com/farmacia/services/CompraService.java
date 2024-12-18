package com.farmacia.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.farmacia.dto.CompraDto;
import com.farmacia.exception.HttpException;
import com.farmacia.models.Compra;
import com.farmacia.models.Proveedor;
import com.farmacia.repository.CompraRepository;

import jakarta.transaction.Transactional;

@Service
public class CompraService {

    @Autowired
    private CompraRepository compraRepository;

    @Autowired
    private ProductoService productoService;

    @Autowired
    private ProveedorService proveedorService;

    /**
     * Verifica si el proveedor y el producto existen, y crea una compra
     * @param compraDto datos de la compra
     * @throws HttpException si existe un error al crear la compra
     */
    @Transactional(rollbackOn = Exception.class)
    public void crearCompra(CompraDto.POST compraDto) throws HttpException {
        Proveedor proveedor = proveedorService.obtenerProveedor(compraDto.getIdProveedor());
        productoService.agregarNuevoLote(compraDto.getIdProducto(), compraDto);
        Compra compra = new Compra(compraDto, proveedor);
        compraRepository.save(compra);

    }

    /**
     * Obtiene todas las compras
     * @return lista de compras
     */
    public List<Compra> obtenerCompras() {
        return compraRepository.findAll();
    }

}
