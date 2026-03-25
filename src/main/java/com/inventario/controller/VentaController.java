package com.inventario.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.inventario.dto.VentaDto;
import com.inventario.exception.HttpException;
import com.inventario.services.VentaService;

import jakarta.validation.Valid;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/ventas")
public class VentaController {

    private final VentaService ventaService;

    public VentaController(VentaService ventaService) {
        this.ventaService = ventaService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void crearVenta(@Valid @RequestBody VentaDto.Post ventaDto) throws HttpException {
        this.ventaService.crearVenta(ventaDto);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<VentaDto.GetSinSucursal> obtenerVentas() {
        return this.ventaService.obtenerVentas();
    }

}
