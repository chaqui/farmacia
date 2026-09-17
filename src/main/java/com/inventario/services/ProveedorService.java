package com.inventario.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.inventario.dto.ProductoDto;
import com.inventario.dto.ProveedorDto;
import com.inventario.dto.MarcaDto;
import com.inventario.dto.ProductoDto.Post;
import com.inventario.exception.HttpException;
import com.inventario.models.Proveedor;
import com.inventario.models.Compra;
import com.inventario.models.DetalleCompra;
import com.inventario.models.Marca;
import com.inventario.repository.ProveedorRepository;
import com.inventario.repository.MarcaRepository;

@Service
public class ProveedorService {

    @Autowired
    private ProveedorRepository proveedorRepository;

    @Autowired
    private ProductoService productoService;

    @Autowired
    private MarcaRepository marcaRepository;

    /**
     * Crea un proveedor
     * 
     * @param proveedorDto datos del proveedor
     * @throws HttpException si alguna marca no existe
     */
    public void crearProveedor(ProveedorDto.POST proveedorDto) throws HttpException {
        Proveedor proveedor = new Proveedor(proveedorDto);

        // Asignar marcas si se proporcionan
        if (proveedorDto.getMarcaIds() != null && !proveedorDto.getMarcaIds().isEmpty()) {
            for (Integer marcaId : proveedorDto.getMarcaIds()) {
                Marca marca = marcaRepository.findById(marcaId)
                        .orElseThrow(() -> new HttpException("Marca con ID " + marcaId + " no encontrada"));
                proveedor.getMarcas().add(marca);
            }
        }

        proveedorRepository.save(proveedor);
    }

    /**
     * Actualiza un proveedor
     * 
     * @param id           id del proveedor
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
     * 
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
     * 
     * @return lista de proveedores
     */
    public List<Proveedor> obtenerProveedores() {
        return proveedorRepository.findAll();
    }

    /**
     * Elimina un proveedor
     * 
     * @param id id del proveedor
     */
    public void eliminarProveedor(Long id) {
        proveedorRepository.deleteById(id);
    }

    /**
     * Obtiene los productos de un proveedor
     * 
     * @param id id del proveedor
     * @return lista de productos
     * @throws HttpException si el proveedor no existe
     */
    public List<ProductoDto.Get> obtenerProductos(Long id) throws HttpException {
        Proveedor proveedor = this.obtenerProveedor(id);
        List<Compra> compras = proveedor.getCompras();
        return compras.stream()
                .flatMap(compra -> compra.productos().stream())
                .distinct()
                .map(ProductoDto.Get::new)
                .toList();
    }

    /**
     * Obtiene las marcas de un proveedor
     * 
     * @param id id del proveedor
     * @return lista de marcas
     * @throws HttpException si el proveedor no existe
     */
    public List<MarcaDto.Get> obtenerMarcas(Long id) throws HttpException {
        Proveedor proveedor = this.obtenerProveedor(id);
        return proveedor.getMarcas().stream()
                .map(marca -> {
                    MarcaDto.Get dto = new MarcaDto.Get();
                    dto.setId(marca.getId());
                    dto.setNombre(marca.getNombre());
                    dto.setDescripcion(marca.getDescripcion());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    /**
     * Asigna una marca a un proveedor
     * 
     * @param proveedorId id del proveedor
     * @param marcaId     id de la marca
     * @throws HttpException si el proveedor o la marca no existen
     */
    public void asignarMarcaAProveedor(Long proveedorId, Integer marcaId) throws HttpException {
        Proveedor proveedor = this.obtenerProveedor(proveedorId);
        Marca marca = marcaRepository.findById(marcaId)
                .orElseThrow(() -> new HttpException("Marca no encontrada"));

        if (!proveedor.getMarcas().contains(marca)) {
            proveedor.getMarcas().add(marca);
            proveedorRepository.save(proveedor);
        }
    }

    /**
     * Remueve una marca de un proveedor
     * 
     * @param proveedorId id del proveedor
     * @param marcaId     id de la marca
     * @throws HttpException si el proveedor o la marca no existen
     */
    public void removerMarcaDeProveedor(Long proveedorId, Integer marcaId) throws HttpException {
        Proveedor proveedor = this.obtenerProveedor(proveedorId);
        Marca marca = marcaRepository.findById(marcaId)
                .orElseThrow(() -> new HttpException("Marca no encontrada"));

        if (proveedor.getMarcas().contains(marca)) {
            proveedor.getMarcas().remove(marca);
            proveedorRepository.save(proveedor);
        }
    }

}
