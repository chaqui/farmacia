package com.inventario.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.format.annotation.DateTimeFormat;

import com.inventario.dto.SolicitudDto;
import com.inventario.dto.SucursalDto;
import com.inventario.dto.VentaDto;
import com.inventario.exception.HttpException;
import com.inventario.services.SucursalService;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import com.inventario.security.ValidateToken;
import com.inventario.security.SystemRoles;

@RestController
@RequestMapping("/sucursales")
public class SucursalController {

    private final SucursalService sucursalService;

    public SucursalController(SucursalService sucursalService) {
        this.sucursalService = sucursalService;
    }

    @DeleteMapping("/{id}")
    @ValidateToken(roles = {SystemRoles.ADMINISTRADOR})
    public void eliminarSucursal(@PathVariable Long id) {
        this.sucursalService.eliminarSucursal(id);
    }

    @PostMapping
    @ValidateToken(roles = {SystemRoles.ADMINISTRADOR})
    public void crearSucursal(@Valid @RequestBody SucursalDto.Post sucursalDto) {
        this.sucursalService.crearSucursal(sucursalDto);
    }

    @GetMapping("/{id}")
    @ValidateToken(roles = {SystemRoles.VENDEDOR, SystemRoles.CAJA, SystemRoles.COMPRAS, SystemRoles.ADMINISTRADOR})
    public SucursalDto.Get obtenerSucursal(@PathVariable Long id) {
        return new SucursalDto.Get(this.sucursalService.obtenerSucursal(id));
    }

    @PutMapping("/{id}")
    @ValidateToken(roles = {SystemRoles.ADMINISTRADOR})
    public void actualizarSucursal(@PathVariable Long id, @Valid @RequestBody SucursalDto.Post sucursalDto) {
        this.sucursalService.actualizarSucursal(id, sucursalDto);
    }

    @GetMapping
    @ValidateToken(roles = {SystemRoles.VENDEDOR, SystemRoles.CAJA, SystemRoles.COMPRAS, SystemRoles.ADMINISTRADOR})
    public Iterable<SucursalDto.Get> obtenerSucursales() {
        return this.sucursalService.obtenerSucursales();
    }

    @GetMapping("/{id}/solicitudes")
    @ValidateToken(roles = {SystemRoles.COMPRAS, SystemRoles.ADMINISTRADOR})
    public List<SolicitudDto.GET> getSucursales(@PathVariable Long id) {
        return this.sucursalService.obtenerSolicitudes(id).stream().map(SolicitudDto.GET::new).toList();
    }

    @GetMapping("/{id}/ventas")
    @ValidateToken(roles = {SystemRoles.CAJA, SystemRoles.ADMINISTRADOR})
    public List<VentaDto.GetConSucursal> obtenerVentas(
            @PathVariable Long id,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        if (desde == null && hasta == null) {
            return this.sucursalService.obtenerVentas(id);
        }
        if (desde == null || hasta == null || desde.isAfter(hasta)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Los parámetros desde y hasta son obligatorios y deben formar un rango válido");
        }
        return this.sucursalService.obtenerVentas(id, desde, hasta);
    }

    @PostMapping("/{id}/ventas")
    @ResponseStatus(HttpStatus.CREATED)
    @ValidateToken(roles = {SystemRoles.VENDEDOR})
    public void crearVenta(@PathVariable Long id, @Valid @RequestBody VentaDto.Post ventaDto) throws HttpException {
        this.sucursalService.crearVenta(id, ventaDto);
    }

}
