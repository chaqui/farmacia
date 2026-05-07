package com.inventario.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.inventario.dto.ProductoDto;
import com.inventario.dto.ProveedorDto;
import com.inventario.dto.MarcaDto;
import com.inventario.exception.HttpException;
import com.inventario.services.ProveedorService;

import jakarta.validation.Valid;
import lombok.extern.java.Log;

@RestController
@RequestMapping("/proveedores")
@Log
public class ProveedorController {

    @Autowired
    private ProveedorService proveedorService;

    @PostMapping
    // Ejemplo de autorización:
    // @ValidateToken(roles = {SystemRoles.COMPRAS, SystemRoles.ADMINISTRADOR})
    public ResponseEntity<Void> crearProveedor(@Valid @RequestBody ProveedorDto.POST proveedorDto) 
            throws HttpException {
        proveedorService.crearProveedor(proveedorDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping
    // Ejemplo de autorización:
    // @ValidateToken(roles = {SystemRoles.COMPRAS, SystemRoles.ADMINISTRADOR})
    public List<ProveedorDto.GET> obtenerProveedores() {
        return proveedorService.obtenerProveedores().stream().map(ProveedorDto.GET::new).toList();
    }

    @GetMapping
    @RequestMapping("/{id}")
    // Ejemplo de autorización:
    // @ValidateToken(roles = {SystemRoles.COMPRAS, SystemRoles.ADMINISTRADOR})
    public ProveedorDto.GET obtenerProveedor(@PathVariable Long id) throws HttpException {
        return new ProveedorDto.GET(proveedorService.obtenerProveedor(id));
    }

    @PostMapping("/{id}/productos")
    // Ejemplo de autorización:
    // @ValidateToken(roles = {SystemRoles.COMPRAS, SystemRoles.ADMINISTRADOR})
    public void agregarProducto(@PathVariable Long id, @Valid @RequestBody ProductoDto.Post productoDto) throws HttpException {
        proveedorService.agregarProducto(id, productoDto);
    }

    @GetMapping("/{id}/productos")
    // Ejemplo de autorización:
    // @ValidateToken(roles = {SystemRoles.COMPRAS, SystemRoles.ADMINISTRADOR})
    public List<ProductoDto.Get> obtenerProductos(@PathVariable Long id) throws HttpException {
        return proveedorService.obtenerProductos(id);

    }

    @GetMapping("/{id}/marcas")
    // Ejemplo de autorización:
    // @ValidateToken(roles = {SystemRoles.COMPRAS, SystemRoles.ADMINISTRADOR})
    public List<MarcaDto.Get> obtenerMarcas(@PathVariable Long id) throws HttpException {
        return proveedorService.obtenerMarcas(id);
    }

    @PostMapping("/{proveedorId}/marcas/{marcaId}")
    // Ejemplo de autorización:
    // @ValidateToken(roles = {SystemRoles.ADMINISTRADOR})
    public ResponseEntity<Void> asignarMarcaAProveedor(@PathVariable Long proveedorId,
            @PathVariable Integer marcaId) throws HttpException {
        proveedorService.asignarMarcaAProveedor(proveedorId, marcaId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{proveedorId}/marcas/{marcaId}")
    // Ejemplo de autorización:
    // @ValidateToken(roles = {SystemRoles.ADMINISTRADOR})
    public ResponseEntity<Void> removerMarcaDeProveedor(@PathVariable Long proveedorId,
            @PathVariable Integer marcaId) throws HttpException {
        proveedorService.removerMarcaDeProveedor(proveedorId, marcaId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    // Ejemplo de autorización:
    // @ValidateToken(roles = {SystemRoles.ADMINISTRADOR})
    public void eliminarProveedor(@PathVariable Long id) {
        log.info(id.toString());
        proveedorService.eliminarProveedor(id);
    }

}
