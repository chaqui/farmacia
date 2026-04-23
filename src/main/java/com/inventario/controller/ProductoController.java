package com.inventario.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.inventario.dto.LoteDto;
import com.inventario.dto.ProductoDto;
import com.inventario.exception.HttpException;
import com.inventario.services.ProductoService;

import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

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

    @GetMapping("/buscar")
    public List<ProductoDto.Get> getProductoPorNombre(@RequestParam(name = "q", required = false) String query,
            @RequestParam(name = "nombre", required = false) String nombre) {
        String q = query != null ? query : nombre;
        log.info("Buscando productos con query: " + q);
        if (q == null || q.isBlank()) {
            return productoService.obtenerProductos();
        }
        return productoService.buscarProductos(q);

    }

    @GetMapping("/search")
    public List<ProductoDto.Get> buscarProductos(@RequestParam(name = "q", required = false) String q) {
        log.info("Busqueda general productos q: " + q);
        if (q == null || q.isBlank()) {
            return new ArrayList<>();
        }
        return productoService.buscarProductos(q);
    }

    @GetMapping("/{id}")
    public ProductoDto.Get obtenerProducto(@PathVariable Long id) throws HttpException {
        return new ProductoDto.Get(productoService.obtenerProducto(id));
    }

    @GetMapping("/{id}/lotes")
    public List<LoteDto.GET> obtenerLotes(@PathVariable Long id) throws HttpException {
        log.info("Obteniendo lotes del producto con id: " + id);
        return this.productoService.obtenerLotes(id).stream().map(lote -> new LoteDto.GET(lote))
                .toList();
    }

    @GetMapping("/{id}/relacionados")
    public List<ProductoDto.Get> obtenerRelacionados(@PathVariable Long id) throws HttpException {
        return this.productoService.buscarProductosRelacionados(id);
    }

    @GetMapping("/marca/{marcaId}")
    public List<ProductoDto.Get> obtenerProductosPorMarca(@PathVariable Integer marcaId) throws HttpException {
        return this.productoService.obtenerProductosPorMarca(marcaId);
    }

    @PutMapping("/{id}/ubicacion")
    @ResponseStatus(HttpStatus.OK)
    public void actualizarUbicacion(@PathVariable Long id, @RequestBody ProductoDto.UbicacionDto ubicacion)
            throws HttpException {
        this.productoService.actualizarUbicacion(id, ubicacion);
    }


}