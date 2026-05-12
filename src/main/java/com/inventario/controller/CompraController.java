package com.inventario.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestMapping;
import com.inventario.security.ValidateToken;
import com.inventario.security.SystemRoles;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.inventario.dto.CompraDto;
import com.inventario.dto.DetalleDTO;
import com.inventario.exception.HttpException;
import com.inventario.services.CompraService;

import org.springframework.beans.factory.annotation.Autowired;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/compras")
@Slf4j
public class CompraController {

    @Autowired
    private CompraService compraService;

    /**
     * Crea una compra
     * 
     * @param compraDto datos de la compra
     * @throws HttpException si existe un error al crear la compra
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ValidateToken(roles = {SystemRoles.COMPRAS, SystemRoles.ADMINISTRADOR})
    public void crearCompra(@Valid @RequestBody CompraDto.POST compraDto) throws HttpException {
        this.compraService.crearCompra(compraDto);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @ValidateToken(roles = {SystemRoles.COMPRAS, SystemRoles.ADMINISTRADOR})
    public List<CompraDto.GET> obtenerCompras() {
        log.info("Obteniendo compras");
        return this.compraService.obtenerCompras().stream().map(CompraDto.GET::new).toList();
    }

    @GetMapping("/{id}/detalles")
    @ResponseStatus(HttpStatus.OK)
    @ValidateToken(roles = {SystemRoles.COMPRAS, SystemRoles.ADMINISTRADOR})
    public List<DetalleDTO.GET> obtenerDetalle(@PathVariable Long id) {
        return compraService.obtenerDetalle(id).stream().map(DetalleDTO.GET::new).toList();
    }

}
