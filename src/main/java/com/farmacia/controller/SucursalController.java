package com.farmacia.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.farmacia.dto.SolicitudDto;
import com.farmacia.dto.SucursalDto;
import com.farmacia.services.SucursalService;

@RestController
@RequestMapping("/sucursales")
public class SucursalController {

    private final SucursalService sucursalService;

    @Autowired
    public SucursalController(SucursalService sucursalService) {
        this.sucursalService = sucursalService;
    }

    @DeleteMapping("/{id}")
    public void eliminarSucursal(@PathVariable Long id) {
        this.sucursalService.eliminarSucursal(id);
    }

    @PostMapping
    public void crearSucursal(@RequestBody SucursalDto.Post sucursalDto) {
        this.sucursalService.crearSucursal(sucursalDto);
    }

    @GetMapping("/{id}")
    public SucursalDto.Get obtenerSucursal(@PathVariable Long id) {
        return new SucursalDto.Get(this.sucursalService.obtenerSucursal(id));
    }

    @PutMapping("/{id}")
    public void actualizarSucursal(@PathVariable Long id, @RequestBody SucursalDto.Post sucursalDto) {
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

}
