package com.inventario.controller;

import com.inventario.dto.CreditoDto;
import com.inventario.dto.PagoCreditoDto;
import com.inventario.exception.HttpException;

import com.inventario.models.PagoCredito;
import com.inventario.services.CreditoService;
import com.inventario.services.PagoCreditoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import com.inventario.security.ValidateToken;
import com.inventario.security.SystemRoles;

@RestController
@RequestMapping("creditos")
public class CreditoController {

    private final CreditoService creditoService;
    private final PagoCreditoService pagoCreditoService;

    public CreditoController(CreditoService creditoService, PagoCreditoService pagoCreditoService) {
        this.creditoService = creditoService;
        this.pagoCreditoService = pagoCreditoService;
    }

    @GetMapping
    @ValidateToken(roles = {SystemRoles.CAJA, SystemRoles.ADMINISTRADOR})
    public List<CreditoDto.Get> list() {
        return creditoService.listAll().stream().map(CreditoDto.Get::new).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @ValidateToken(roles = {SystemRoles.CAJA, SystemRoles.ADMINISTRADOR})
    public CreditoDto.Get get(@PathVariable Integer id) {
        return creditoService.getById(id).map(CreditoDto.Get::new).orElse(null);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ValidateToken(roles = {SystemRoles.ADMINISTRADOR})
    public void create(@Valid @RequestBody CreditoDto.Post dto) throws HttpException {

        creditoService.create(dto);

    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @ValidateToken(roles = {SystemRoles.ADMINISTRADOR})
    public void update(@PathVariable Integer id, @Valid @RequestBody CreditoDto.Post dto) throws HttpException {
        creditoService.update(id, dto);

    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ValidateToken(roles = {SystemRoles.ADMINISTRADOR})
    public void delete(@PathVariable Integer id) throws HttpException {
        creditoService.delete(id);
    }

    @PostMapping("/{id}/pago")
    @ResponseStatus(HttpStatus.CREATED)
    @ValidateToken(roles = {SystemRoles.CAJA, SystemRoles.ADMINISTRADOR})
    public void registrarPago(@PathVariable Integer id, @Valid @RequestBody PagoCreditoDto.Post dto)
            throws HttpException {
        pagoCreditoService.registrarPago(id, dto.getMonto());
    }

    @GetMapping("/{id}/pagos")
    @ValidateToken(roles = {SystemRoles.CAJA, SystemRoles.ADMINISTRADOR})
    public List<PagoCreditoDto.Get> obtenerPagos(@PathVariable Integer id) throws HttpException {
        List<PagoCredito> pagos = pagoCreditoService.obtenerPagosPorCredito(id);
        return pagos.stream()
                .map(PagoCreditoDto.Get::new)
                .collect(Collectors.toList());
    }

}
