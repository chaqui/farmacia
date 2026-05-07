package com.inventario.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.inventario.dto.MarcaDto;
import com.inventario.dto.ProductoDto;
import com.inventario.dto.ProveedorDto;
import com.inventario.exception.HttpException;
import com.inventario.services.MarcaService;

import java.util.List;

@RestController
@RequestMapping("/marcas")
public class MarcaController {

    private final MarcaService marcaService;

    public MarcaController(MarcaService marcaService) {
        this.marcaService = marcaService;
    }

    /**
     * GET /marcas - Obtiene todas las marcas
     */
    @GetMapping
    // Ejemplo de autorización:
    // @ValidateToken(roles = {SystemRoles.VENDEDOR, SystemRoles.CAJA, SystemRoles.COMPRAS, SystemRoles.ADMINISTRADOR})
    public ResponseEntity<List<MarcaDto.Get>> obtenerTodas() {
        List<MarcaDto.Get> marcas = marcaService.obtenerTodas();
        return ResponseEntity.ok(marcas);
    }

    /**
     * GET /marcas/{id} - Obtiene una marca por ID
     */
    @GetMapping("/{id}")
    // Ejemplo de autorización:
    // @ValidateToken(roles = {SystemRoles.VENDEDOR, SystemRoles.CAJA, SystemRoles.COMPRAS, SystemRoles.ADMINISTRADOR})
    public ResponseEntity<MarcaDto.Get> obtenerPorId(@PathVariable Integer id) throws HttpException {
        MarcaDto.Get marca = marcaService.obtenerPorId(id);
        return ResponseEntity.ok(marca);
    }

    /**
     * POST /marcas - Crea una nueva marca
     */
    @PostMapping
    // Ejemplo de autorización:
    // @ValidateToken(roles = {SystemRoles.ADMINISTRADOR})
    public ResponseEntity<MarcaDto.Get> crear(@RequestBody MarcaDto.Post dto) throws HttpException {
        MarcaDto.Get marca = marcaService.crear(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(marca);
    }

    /**
     * PUT /marcas/{id} - Actualiza una marca
     */
    @PutMapping("/{id}")
    // Ejemplo de autorización:
    // @ValidateToken(roles = {SystemRoles.ADMINISTRADOR})
    public ResponseEntity<MarcaDto.Get> actualizar(@PathVariable Integer id,
            @RequestBody MarcaDto.Put dto) throws HttpException {
        MarcaDto.Get marca = marcaService.actualizar(id, dto);
        return ResponseEntity.ok(marca);
    }

    /**
     * DELETE /marcas/{id} - Elimina una marca
     */
    @DeleteMapping("/{id}")
    // Ejemplo de autorización:
    // @ValidateToken(roles = {SystemRoles.ADMINISTRADOR})
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) throws HttpException {
        marcaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * GET /marcas/{marcaId}/productos - Obtiene todos los productos de una marca
     */
    @GetMapping("/{marcaId}/productos")
    // Ejemplo de autorización:
    // @ValidateToken(roles = {SystemRoles.VENDEDOR, SystemRoles.CAJA, SystemRoles.COMPRAS, SystemRoles.ADMINISTRADOR})
    public ResponseEntity<List<ProductoDto.Get>> obtenerProductosPorMarca(@PathVariable Integer marcaId) 
            throws HttpException {
        List<ProductoDto.Get> productos = marcaService.obtenerProductosPorMarca(marcaId);
        return ResponseEntity.ok(productos);
    }

    /**
     * GET /marcas/{marcaId}/proveedores - Obtiene todos los proveedores de una marca
     */
    @GetMapping("/{marcaId}/proveedores")
    // Ejemplo de autorización:
    // @ValidateToken(roles = {SystemRoles.COMPRAS, SystemRoles.ADMINISTRADOR})
    public ResponseEntity<List<ProveedorDto.GET>> obtenerProveedoresPorMarca(@PathVariable Integer marcaId) 
            throws HttpException {
        List<ProveedorDto.GET> proveedores = marcaService.obtenerProveedoresPorMarca(marcaId);
        return ResponseEntity.ok(proveedores);
    }

    /**
     * POST /marcas/{marcaId}/proveedores/{proveedorId} - Asigna un proveedor a una marca
     */
    @PostMapping("/{marcaId}/proveedores/{proveedorId}")
    // Ejemplo de autorización:
    // @ValidateToken(roles = {SystemRoles.ADMINISTRADOR})
    public ResponseEntity<Void> asignarProveedorAMarca(@PathVariable Integer marcaId,
            @PathVariable Long proveedorId) throws HttpException {
        marcaService.asignarProveedorAMarca(marcaId, proveedorId);
        return ResponseEntity.noContent().build();
    }

    /**
     * DELETE /marcas/{marcaId}/proveedores/{proveedorId} - Remueve un proveedor de una marca
     */
    @DeleteMapping("/{marcaId}/proveedores/{proveedorId}")
    // Ejemplo de autorización:
    // @ValidateToken(roles = {SystemRoles.ADMINISTRADOR})
    public ResponseEntity<Void> removerProveedorDeMarca(@PathVariable Integer marcaId,
            @PathVariable Long proveedorId) throws HttpException {
        marcaService.removerProveedorDeMarca(marcaId, proveedorId);
        return ResponseEntity.noContent().build();
    }
}
