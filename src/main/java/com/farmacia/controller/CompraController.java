package com.farmacia.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.farmacia.dto.CompraDto;
import com.farmacia.exception.HttpException;
import com.farmacia.services.CompraService;

@RequestMapping("/compras")
public class CompraController {

    @Autowired
    private CompraService compraService;


    /**
     * Crea una compra
     * @param compraDto datos de la compra
     * @throws HttpException si existe un error al crear la compra
     */
    @PostMapping
    public void crearCompra(CompraDto.POST compraDto) throws HttpException {
        this.compraService.crearCompra(compraDto);
    }



}
