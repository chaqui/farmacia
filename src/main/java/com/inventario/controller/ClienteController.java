package com.inventario.controller;

import com.inventario.dto.ClienteDto;
import com.inventario.exception.HttpException;
import com.inventario.models.Cliente;
import com.inventario.services.ClienteService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @GetMapping
    public List<ClienteDto.Get> list() {
        return clienteService.obtenerTodos().stream().map(ClienteDto.Get::new).collect(Collectors.toList());
    }

    @PostMapping
    public ResponseEntity<ClienteDto.Get> create(
            @Valid @RequestBody ClienteDto.Post dto) throws HttpException {
        // Crear cliente y solicitar autorización si se proporciona límite
        Cliente saved = clienteService.crearCliente(dto);
        return ResponseEntity.ok(new ClienteDto.Get(saved));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClienteDto.Get> obtenerCliente(@PathVariable Integer id) throws HttpException {
        Cliente cliente = clienteService.obtenerClientePorId(id);
        return ResponseEntity.ok(new ClienteDto.Get(cliente));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClienteDto.Get> actualizar(
            @PathVariable Integer id,
            @Valid @RequestBody ClienteDto.Put dto) throws HttpException {
        // Actualizar cliente y solicitar autorización si el límite cambió
        Cliente cliente = clienteService.actualizarCliente(id, dto);
        return ResponseEntity.ok(new ClienteDto.Get(cliente));
    }

    @PutMapping("/{id}/limite")
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
    public ResponseEntity<Void> eliminarCliente(@PathVariable Integer id) throws HttpException {
        clienteService.eliminarCliente(id);
        return ResponseEntity.noContent().build();
    }

}
