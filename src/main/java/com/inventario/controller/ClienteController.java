package com.inventario.controller;

import com.inventario.dto.AutorizacionLimiteCreditoDto;
import com.inventario.dto.ClienteDto;
import com.inventario.dto.CreditoDto;
import com.inventario.exception.HttpException;
import com.inventario.models.Cliente;
import com.inventario.services.ClienteService;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.inventario.security.ValidateToken;
import com.inventario.security.SystemRoles;



@RestController
@RequestMapping("/clientes")
@Log4j2
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @GetMapping
    @ValidateToken(roles = {SystemRoles.VENDEDOR, SystemRoles.CAJA, SystemRoles.COMPRAS, SystemRoles.ADMINISTRADOR})
    public List<ClienteDto.Get> list() {
        return clienteService.obtenerTodos().stream().map(ClienteDto.Get::new).toList();
    }

    @PostMapping
    @ValidateToken(roles = {SystemRoles.VENDEDOR, SystemRoles.ADMINISTRADOR})
    public ResponseEntity<ClienteDto.Get> create(
            @Valid @RequestBody ClienteDto.Post dto) throws HttpException {
        // Crear cliente y solicitar autorización si se proporciona límite
        Cliente saved = clienteService.crearCliente(dto);
        return ResponseEntity.ok(new ClienteDto.Get(saved));
    }

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    @ValidateToken(roles = {SystemRoles.VENDEDOR, SystemRoles.CAJA, SystemRoles.ADMINISTRADOR})
    public List<ClienteDto.Get> searchByName(@RequestParam(name = "q", required = false) String q) {
        log.info("Buscando clientes por nombre: " + q);
        return clienteService.obtenerClientesPorNombre(q)
                .stream()
                .map(ClienteDto.Get::new)
                .toList();
    }
    @GetMapping("/{id}")
    @ValidateToken(roles = {SystemRoles.VENDEDOR, SystemRoles.CAJA, SystemRoles.ADMINISTRADOR})
    public ResponseEntity<ClienteDto.Get> obtenerCliente(@PathVariable Integer id) throws HttpException {
        Cliente cliente = clienteService.obtenerClientePorId(id);
        return ResponseEntity.ok(new ClienteDto.Get(cliente));
    }

    @PutMapping("/{id}")
    @ValidateToken(roles = {SystemRoles.VENDEDOR, SystemRoles.ADMINISTRADOR})
    public ResponseEntity<ClienteDto.Get> actualizar(
            @PathVariable Integer id,
            @Valid @RequestBody ClienteDto.Put dto) throws HttpException {
        // Actualizar cliente y solicitar autorización si el límite cambió
        Cliente cliente = clienteService.actualizarCliente(id, dto);
        return ResponseEntity.ok(new ClienteDto.Get(cliente));
    }

    @PutMapping("/{id}/limite")
    @ValidateToken(roles = {SystemRoles.ADMINISTRADOR})
    public ResponseEntity<ClienteDto.Get> cambiarLimite(
            @PathVariable Integer id,
            @RequestParam Float nuevoLimite) throws HttpException {
        ClienteDto.Put dto = new ClienteDto.Put();
        dto.setTipoCliente(null); // No cambiar tipo
        dto.setLimiteCredito(nuevoLimite);
        // Solicitar autorización sin guardar el límite aún
        Cliente actualizado = clienteService.actualizarCliente(id, dto);
        return ResponseEntity.ok(new ClienteDto.Get(actualizado));
    }

    @DeleteMapping("/{id}")
    @ValidateToken(roles = {SystemRoles.ADMINISTRADOR})
    public ResponseEntity<Void> eliminarCliente(@PathVariable Integer id) throws HttpException {
        clienteService.eliminarCliente(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/creditos")
    @ValidateToken(roles = {SystemRoles.VENDEDOR, SystemRoles.CAJA, SystemRoles.ADMINISTRADOR})
    public List<CreditoDto.Get> obtenerCreditos(@PathVariable Integer id) throws HttpException {
        List<CreditoDto.Get> creditos = clienteService.obtenerCreditos(id)
                .stream()
                .map(CreditoDto.Get::new)
                .toList();
        return creditos;
    }

    @GetMapping("/{id}/autorizaciones")
    @ValidateToken(roles = {SystemRoles.ADMINISTRADOR})
    public List<AutorizacionLimiteCreditoDto.Get> obtenerAutorizaciones(@PathVariable Integer id) throws HttpException {
        return clienteService.obtenerAutorizaciones(id)
                .stream()
                .map(AutorizacionLimiteCreditoDto.Get::new)
                .toList();
    }
    

    


    

}
