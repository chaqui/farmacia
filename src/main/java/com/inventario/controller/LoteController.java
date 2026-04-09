package com.inventario.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.inventario.dto.LoteDto;
import com.inventario.exception.HttpException;
import com.inventario.services.LoteService;

@RestController
@RequestMapping("lotes")
public class LoteController {

    private final LoteService loteService;

    public LoteController(LoteService loteService) {
        this.loteService = loteService;
    }

    @PutMapping("/{id}/ubicacion")
    @ResponseStatus(HttpStatus.OK)
    public void actualizarUbicacion(@PathVariable String id, @RequestBody LoteDto.UbicacionDto ubicacion)
            throws HttpException {
        this.loteService.actualizarUbicacionLote(id, ubicacion);
    }

}
