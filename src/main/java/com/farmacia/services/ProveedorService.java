package com.farmacia.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.farmacia.dto.ProductoDto.Post;
import com.farmacia.dto.ProductoDto;
import com.farmacia.dto.ProveedorDto;
import com.farmacia.exception.HttpException;
import com.farmacia.models.Proveedor;
import com.farmacia.repository.ProveedorRepository;

@Service
public class ProveedorService {

    @Autowired
    private ProveedorRepository proveedorRepository;

    @Autowired
    private ProductoService productoService;

    /**
     * Crea un proveedor
     * @param proveedorDto datos del proveedor
     */
    public void crearProveedor(ProveedorDto.POST proveedorDto) {
        proveedorRepository.save(new Proveedor(proveedorDto));
    }

    /**
     * Actualiza un proveedor
     * @param id id del proveedor
     * @param proveedorDto datos del proveedor
     * @throws HttpException si el proveedor no existe
     */
    public void actualizarProveedor(Long id, ProveedorDto.PUT proveedorDto) throws HttpException {
        Proveedor proveedor = this.obtenerProveedor(id);

        proveedor.update(proveedorDto);
        proveedorRepository.save(proveedor);
    }

    /**
     * Obtiene un proveedor
     * @param id id del proveedor
     * @return proveedor
     * @throws HttpException si el proveedor no existe
     */
    public Proveedor obtenerProveedor(Long id) throws HttpException {
        Proveedor proveedor = proveedorRepository.findById(id).orElse(null);
        if (proveedor == null) {
            throw new HttpException("Proveedor no encontrado");
        }
        return proveedor;
    }

    /**
     * Obtiene todos los proveedores
     * @return lista de proveedores
     */
    public List<Proveedor> obtenerProveedores() {
        return proveedorRepository.findAll();
    }

    /**
     * Agrega un producto a un proveedor
     * @param id id del proveedor
     * @param productoDto datos del producto
     * @throws HttpException si el proveedor no existe
     */
    public void agregarProducto(Long id, Post productoDto) throws HttpException {
        Proveedor proveedor = this.obtenerProveedor(id);
        productoService.crearProducto(productoDto, proveedor);
    }

    /**
     * Elimina un proveedor
     * @param id id del proveedor
     */
    public void eliminarProveedor(Long id) {
        proveedorRepository.deleteById(id);
    }

    /**
     * Obtiene los productos de un proveedor
     * @param id id del proveedor
     * @return lista de productos
     * @throws HttpException si el proveedor no existe
     */
    public List<ProductoDto.Get> obtenerProductos(Long id) throws HttpException {
        Proveedor proveedor = this.obtenerProveedor(id);
        return productoService.agregarStock(proveedor.getProductos());
    }

}
