package com.inventario.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.inventario.dto.SolicitudDetalleDto;
import com.inventario.dto.SolicitudDto;
import com.inventario.exception.HttpException;
import com.inventario.services.SolicitudDetalleService;
import com.inventario.services.SolicitudService;

@RestController
@RequestMapping("/solicitudes")
public class SolicitudController {

    private final SolicitudService solicitudService;

    private final SolicitudDetalleService solicitudDetalleService;

    @Autowired
    public SolicitudController(SolicitudService solicitudService, SolicitudDetalleService solicitudDetalleService) {
        this.solicitudService = solicitudService;
        this.solicitudDetalleService = solicitudDetalleService;
    }
    @PostMapping
    public void crearSolicitud(@Valid @RequestBody SolicitudDto.POST solicitudDto) throws HttpException {
        this.solicitudService.crearSolicitud(solicitudDto);
    }

    @PostMapping("/{id}/enviar")
    public void enviarSolicitud(@PathVariable Long id, @Valid @RequestBody SolicitudDto.ENVIAR solicitudDto)
            throws HttpException {
        this.solicitudService.enviarSolicitud(id, solicitudDto.getLotes());
    }

    @GetMapping("/{id}")
    public SolicitudDto.GET obtenerSolicitud(@PathVariable Long id) throws HttpException {
        return new SolicitudDto.GET(this.solicitudService.obtenerSolicitud(id));
    }

    @GetMapping
    public Iterable<SolicitudDto.GET> obtenerSolicitudes() {
        return this.solicitudService.obtenerSolicitudes().stream().map(SolicitudDto.GET::new).toList();
    }

    @GetMapping("/{id}/detalles")
    public Iterable<SolicitudDetalleDto.Get> obtenerDetalles(@PathVariable Long id) {
        return this.solicitudDetalleService.obtenerDetalles(id).stream().map(SolicitudDetalleDto.Get::new).toList();
    }

}
