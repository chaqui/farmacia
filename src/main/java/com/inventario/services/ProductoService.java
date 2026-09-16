package com.inventario.services;

import java.util.List;
import java.util.Set;
import java.util.LinkedHashSet;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;


import com.inventario.dto.ProductoDto;
import com.inventario.exception.HttpException;
import com.inventario.models.Fotografia;
import com.inventario.models.Lote;
import com.inventario.models.Producto;
import com.inventario.models.Marca;
import com.inventario.repository.ProductoRepository;
import com.inventario.repository.MarcaRepository;
import com.inventario.models.Categoria;

import jakarta.transaction.Transactional;

@Service
public class ProductoService {

    private ProductoRepository productoRepository;
    private CategoriaService categoriaService;
    private MarcaRepository marcaRepository;

    public ProductoService(ProductoRepository productoRepository, CategoriaService categoriaService, MarcaRepository marcaRepository) {
        this.productoRepository = productoRepository;
        this.categoriaService = categoriaService;
        this.marcaRepository = marcaRepository;
    }

    @Transactional
    public void crearProducto(ProductoDto.Post productoDto) throws HttpException {
        // validar porcentajes si se enviaron
        Float pd = productoDto.getPorcentajeDescuento();
        Float pg = productoDto.getPorcentajeGanancia();

        this.validarPorcentajes(pd, pg);
        Producto producto = new Producto(productoDto);
        
        // Establecer marca si se proporciona
        if (productoDto.getMarcaId() != null) {
            Marca marca = marcaRepository.findById(productoDto.getMarcaId())
                    .orElseThrow(() -> new HttpException("Marca no encontrada"));
            producto.setMarca(marca);
        }
        
        productoRepository.save(producto);

        if (productoDto.getFotografias() != null) {
            productoDto.getFotografias().forEach(url -> {
                producto.agregarFotografia(new Fotografia(url, producto));
            });
        }
        if (productoDto.getRelacionadosIds() != null) {
            productoDto.getRelacionadosIds().forEach(id -> {
                Producto relacionado = productoRepository.findById(id).orElse(null);
                if (relacionado != null) {
                    producto.agregarRelacionado(relacionado);
                }
            });
        }

        List<Categoria> categorias = new java.util.ArrayList<>();

        // ids preferidos
        if (productoDto.getCategoriaIds() != null) {
            productoDto.getCategoriaIds().forEach(id -> {
                Categoria categoria = categoriaService.obtenerCategoria(id);
                if (categoria != null)
                    categorias.add(categoria);
            });
        }

        // nombres: crear si no existen
        if (productoDto.getCategoriaNombres() != null) {
            productoDto.getCategoriaNombres().forEach(nombre -> {
                if (nombre == null || nombre.isBlank())
                    return;
                Categoria categoria = categoriaService.crearSiNoExiste(nombre);
                if (categoria != null)
                    categorias.add(categoria);
            });
        }

        if (!categorias.isEmpty()) {
            categorias.forEach(producto::agregarCategoria);
        }

        productoRepository.save(producto);
    }

    private void validarPorcentajes(Float pd, Float pg) throws HttpException {
        if (pd != null && (pd < 0f || pd > 100f))
            throw new HttpException("porcentajeDescuento debe estar entre 0 y 100", 400);
        if (pg != null && (pg < 0f || pg > 100f))
            throw new HttpException("porcentajeGanancia debe estar entre 0 y 100", 400);
        if (pd != null && pg != null && pd > pg)
            throw new HttpException("El porcentaje de descuento no puede ser mayor al de ganancia", 400);
    }

    @Transactional
    public List<ProductoDto.Get> obtenerProductos() {
        return productoRepository.findAll().stream().map(ProductoDto.Get::new).collect(Collectors.toList());
    }

    public Producto obtenerProducto(Long id) throws HttpException {
        Producto producto = productoRepository.findById(id).orElse(null);
        if (producto == null) {
            throw new HttpException("Producto no encontrado", 404);
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
        return productoRepository
                .findByNombreContainingIgnoreCaseOrDescripcionContainingIgnoreCaseOrCodigoContainingIgnoreCase(query,
                        query, query)
                .stream().map(ProductoDto.Get::new).toList();
    }

    public List<ProductoDto.Get> buscarProductosRelacionados(Long id) throws HttpException {
        Producto producto = this.obtenerProducto(id);

        Set<Producto> resultado = new LinkedHashSet<>();

        // agregar relacionados explícitos
        if (producto.getRelacionados() != null) {
            resultado.addAll(producto.getRelacionados());
        }

        // agregar todos los de las mismas categorías
        if (producto.getCategorias() != null && !producto.getCategorias().isEmpty()) {
            List<Producto> mismos = productoRepository.findDistinctByCategoriasIn(producto.getCategorias());
            if (mismos != null) {
                resultado.addAll(mismos);
            }
        }

        // agregar todos los de la misma marca
        if (producto.getMarca() != null) {
            List<Producto> productosMarca = productoRepository.findByMarcaId(producto.getMarca().getId());
            if (productosMarca != null) {
                resultado.addAll(productosMarca);
            }
        }

        // quitar el propio producto si está presente
        resultado.removeIf(p -> p.getId().equals(producto.getId()));

        return resultado.stream().map(ProductoDto.Get::new).toList();
    }

    public List<ProductoDto.Get> obtenerProductosPorMarca(Integer marcaId) throws HttpException {
        if (!marcaRepository.existsById(marcaId)) {
            throw new HttpException("Marca no encontrada");
        }
        return productoRepository.findByMarcaId(marcaId).stream()
                .map(ProductoDto.Get::new).toList();
    }

    @Transactional
    public void actualizarUbicacion(Long productoId, ProductoDto.UbicacionDto ubicacion) throws HttpException {
        Producto producto = this.obtenerProducto(productoId);
        producto.setBodega(ubicacion.getBodega());
        producto.setEstanteria(ubicacion.getEstanteria());
        producto.setNivel(ubicacion.getNivel());
        productoRepository.save(producto);
    }

}
