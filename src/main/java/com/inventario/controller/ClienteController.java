package com.inventario.controller;

import com.inventario.dto.ClienteDto;
import com.inventario.models.Cliente;
import com.inventario.repository.ClienteRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    private final ClienteRepository clienteRepository;

    public ClienteController(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    @GetMapping
    public List<ClienteDto.Get> list() {
        return clienteRepository.findAll().stream().map(ClienteDto.Get::new).collect(Collectors.toList());
    }

    @PostMapping
    public ResponseEntity<ClienteDto.Get> create(@Valid @RequestBody ClienteDto.Post dto) {
        Cliente c = new Cliente(dto.getNombre());
        c.setTipoCliente(dto.getTipoCliente());
        c.setLimiteCredito(dto.getLimiteCredito());
        Cliente saved = clienteRepository.save(c);
        return ResponseEntity.ok(new ClienteDto.Get(saved));
    }

    @PutMapping("/{id}/limite")
    public ResponseEntity<ClienteDto.Get> cambiarLimite(@PathVariable Integer id, @RequestParam Float nuevoLimite) {
        return clienteRepository.findById(id).map(c -> {
            c.cambiarLimiteCredito(nuevoLimite);
            Cliente saved = clienteRepository.save(c);
            return ResponseEntity.ok(new ClienteDto.Get(saved));
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

}
