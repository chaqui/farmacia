package com.farmacia.controller;

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

import com.farmacia.dto.SolicitudDto;
import com.farmacia.dto.SucursalDto;
import com.farmacia.dto.VentaDto;
import com.farmacia.services.SucursalService;
import com.farmacia.exception.HttpException;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/sucursales")
public class SucursalController {

    private final SucursalService sucursalService;

    public SucursalController(SucursalService sucursalService) {
        this.sucursalService = sucursalService;
    }

    @DeleteMapping("/{id}")
    public void eliminarSucursal(@PathVariable Long id) {
        this.sucursalService.eliminarSucursal(id);
    }

    @PostMapping
    public void crearSucursal(@Valid @RequestBody SucursalDto.Post sucursalDto) {
        this.sucursalService.crearSucursal(sucursalDto);
    }

    @GetMapping("/{id}")
    public SucursalDto.Get obtenerSucursal(@PathVariable Long id) {
        return new SucursalDto.Get(this.sucursalService.obtenerSucursal(id));
    }

    @PutMapping("/{id}")
    public void actualizarSucursal(@PathVariable Long id, @Valid @RequestBody SucursalDto.Post sucursalDto) {
        this.sucursalService.actualizarSucursal(id, sucursalDto);
    }

    @GetMapping
    public Iterable<SucursalDto.Get> obtenerSucursales() {
        return this.sucursalService.obtenerSucursales();
    }

    @GetMapping("/{id}/solicitudes")
    public List<SolicitudDto.GET> getSucursales(@PathVariable Long id) {
        return this.sucursalService.obtenerSolicitudes(id).stream().map(SolicitudDto.GET::new).toList();
    }

    @GetMapping("/{id}/ventas")
    public List<com.farmacia.dto.VentaDto.GetConSucursal> obtenerVentas(@PathVariable Long id) {
        return this.sucursalService.obtenerVentas(id);
    }

    @PostMapping("/{id}/ventas")
    @ResponseStatus(HttpStatus.CREATED)
    public void crearVenta(@PathVariable Long id, @Valid @RequestBody VentaDto.Post ventaDto) throws HttpException {
        this.sucursalService.crearVenta(id, ventaDto);
    }

}
