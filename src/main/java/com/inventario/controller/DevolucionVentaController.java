package com.inventario.controller;

import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.inventario.dto.DevolucionVentaDto;
import com.inventario.exception.HttpException;
import com.inventario.models.DevolucionVenta;
import com.inventario.services.DevolucionVentaService;

import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import com.inventario.security.ValidateToken;
import com.inventario.security.SystemRoles;

@RestController
@RequestMapping("/devoluciones")
@Log4j2
public class DevolucionVentaController {

    private final DevolucionVentaService devolucionVentaService;

    public DevolucionVentaController(DevolucionVentaService devolucionVentaService) {
        this.devolucionVentaService = devolucionVentaService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ValidateToken(roles = {SystemRoles.VENDEDOR, SystemRoles.CAJA, SystemRoles.ADMINISTRADOR})
    public DevolucionVentaDto.Get crearDevolucion(@Valid @RequestBody DevolucionVentaDto.Post devolucionDto)
            throws HttpException {
        DevolucionVenta devolucion = devolucionVentaService.crearDevolucion(devolucionDto);
        return new DevolucionVentaDto.Get(devolucion);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @ValidateToken(roles = {SystemRoles.VENDEDOR, SystemRoles.CAJA, SystemRoles.ADMINISTRADOR})
    public List<DevolucionVentaDto.Get> obtenerDevoluciones() {
        return devolucionVentaService.obtenerDevoluciones();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @ValidateToken(roles = {SystemRoles.VENDEDOR, SystemRoles.CAJA, SystemRoles.ADMINISTRADOR})
    public DevolucionVentaDto.Get obtenerDevolucionPorId(@PathVariable Integer id) throws HttpException {
        DevolucionVenta devolucion = devolucionVentaService.obtenerDevolucionPorId(id);
        return new DevolucionVentaDto.Get(devolucion);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ValidateToken(roles = {SystemRoles.ADMINISTRADOR})
    public void cancelarDevolucion(@PathVariable Integer id) throws HttpException {
        devolucionVentaService.cancelarDevolucion(id);
    }
}
