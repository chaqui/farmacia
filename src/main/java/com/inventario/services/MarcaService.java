package com.inventario.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.inventario.dto.MarcaDto;
import com.inventario.dto.ProductoDto;
import com.inventario.dto.ProveedorDto;
import com.inventario.exception.HttpException;
import com.inventario.models.Marca;
import com.inventario.models.Producto;
import com.inventario.models.Proveedor;
import com.inventario.repository.MarcaRepository;
import com.inventario.repository.ProveedorRepository;

import java.util.List;
import java.util.stream.Collectors;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
@Transactional
public class MarcaService {

    private final MarcaRepository marcaRepository;
    private final ProveedorRepository proveedorRepository;

    public MarcaService(MarcaRepository marcaRepository, ProveedorRepository proveedorRepository) {
        this.marcaRepository = marcaRepository;
        this.proveedorRepository = proveedorRepository;
    }

    /**
     * Obtiene todas las marcas
     */
    @Transactional(readOnly = true)
    public List<MarcaDto.Get> obtenerTodas() {
        return marcaRepository.findAll().stream()
                .map(this::convertirAGet)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene una marca por ID
     */
    @Transactional(readOnly = true)
    public MarcaDto.Get obtenerPorId(Integer id) throws HttpException {
        Marca marca = marcaRepository.findById(id)
                .orElseThrow(() -> new HttpException("Marca no encontrada"));
        return convertirAGet(marca);
    }

    /**
     * Obtiene una marca por nombre
     */
    @Transactional(readOnly = true)
    public MarcaDto.Get obtenerPorNombre(String nombre) throws HttpException {
        Marca marca = marcaRepository.findByNombre(nombre)
                .orElseThrow(() -> new HttpException("Marca no encontrada"));
        return convertirAGet(marca);
    }

    /**
     * Crea una nueva marca
     */
    public MarcaDto.Get crear(MarcaDto.Post dto) throws HttpException {
        // Validar que el nombre no sea null o vacío
        if (dto.getNombre() == null || dto.getNombre().trim().isEmpty()) {
            throw new HttpException("El nombre de la marca es requerido");
        }

        // Validar que no exista otra marca con el mismo nombre
        if (marcaRepository.findByNombre(dto.getNombre()).isPresent()) {
            throw new HttpException("Ya existe una marca con el nombre: " + dto.getNombre());
        }

        Marca marca = new Marca();
        marca.setNombre(dto.getNombre());
        marca.setDescripcion(dto.getDescripcion());

        Marca marcaGuardada = marcaRepository.save(marca);
        log.info("Marca creada: " + marcaGuardada.getNombre());
        return convertirAGet(marcaGuardada);
    }

    /**
     * Actualiza una marca existente
     */
    public MarcaDto.Get actualizar(Integer id, MarcaDto.Put dto) throws HttpException {
        Marca marca = marcaRepository.findById(id)
                .orElseThrow(() -> new HttpException("Marca no encontrada"));

        if (dto.getNombre() != null && !dto.getNombre().trim().isEmpty()) {
            // Validar que no exista otra marca con el mismo nombre (diferente a la actual)
            if (marcaRepository.findByNombre(dto.getNombre())
                    .filter(m -> !m.getId().equals(id))
                    .isPresent()) {
                throw new HttpException("Ya existe otra marca con el nombre: " + dto.getNombre());
            }
            marca.setNombre(dto.getNombre());
        }

        if (dto.getDescripcion() != null) {
            marca.setDescripcion(dto.getDescripcion());
        }

        Marca marcaActualizada = marcaRepository.save(marca);
        log.info("Marca actualizada: " + marcaActualizada.getNombre());
        return convertirAGet(marcaActualizada);
    }

    /**
     * Elimina una marca
     */
    public void eliminar(Integer id) throws HttpException {
        Marca marca = marcaRepository.findById(id)
                .orElseThrow(() -> new HttpException("Marca no encontrada"));

        // Validar que no tenga productos asociados
        if (!marca.getProductos().isEmpty()) {
            throw new HttpException("No se puede eliminar una marca que tiene productos asociados");
        }

        marcaRepository.delete(marca);
        log.info("Marca eliminada: " + marca.getNombre());
    }

    /**
     * Obtiene los productos de una marca
     */
    @Transactional(readOnly = true)
    public List<ProductoDto.Get> obtenerProductosPorMarca(Integer marcaId) throws HttpException {
        Marca marca = marcaRepository.findById(marcaId)
                .orElseThrow(() -> new HttpException("Marca no encontrada"));
        return marca.getProductos().stream()
                .map(ProductoDto.Get::new)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene todos los proveedores de una marca
     */
    @Transactional(readOnly = true)
    public List<ProveedorDto.GET> obtenerProveedoresPorMarca(Integer marcaId) throws HttpException {
        Marca marca = marcaRepository.findById(marcaId)
                .orElseThrow(() -> new HttpException("Marca no encontrada"));
        return marca.getProveedores().stream()
                .map(ProveedorDto.GET::new)
                .collect(Collectors.toList());
    }

    /**
     * Asigna un proveedor a una marca
     */
    public void asignarProveedorAMarca(Integer marcaId, Long proveedorId) throws HttpException {
        Marca marca = marcaRepository.findById(marcaId)
                .orElseThrow(() -> new HttpException("Marca no encontrada"));
        Proveedor proveedor = proveedorRepository.findById(proveedorId)
                .orElseThrow(() -> new HttpException("Proveedor no encontrado"));

        if (!marca.getProveedores().contains(proveedor)) {
            marca.getProveedores().add(proveedor);
            marcaRepository.save(marca);
            log.info("Proveedor " + proveedor.getNombre() + " asignado a marca " + marca.getNombre());
        }
    }

    /**
     * Remueve un proveedor de una marca
     */
    public void removerProveedorDeMarca(Integer marcaId, Long proveedorId) throws HttpException {
        Marca marca = marcaRepository.findById(marcaId)
                .orElseThrow(() -> new HttpException("Marca no encontrada"));
        Proveedor proveedor = proveedorRepository.findById(proveedorId)
                .orElseThrow(() -> new HttpException("Proveedor no encontrado"));

        if (marca.getProveedores().contains(proveedor)) {
            marca.getProveedores().remove(proveedor);
            marcaRepository.save(marca);
            log.info("Proveedor " + proveedor.getNombre() + " removido de marca " + marca.getNombre());
        }
    }

    /**
     * Convierte una marca a DTO Get
     */
    private MarcaDto.Get convertirAGet(Marca marca) {
        MarcaDto.Get dto = new MarcaDto.Get();
        dto.setId(marca.getId());
        dto.setNombre(marca.getNombre());
        dto.setDescripcion(marca.getDescripcion());
        return dto;
    }
}
