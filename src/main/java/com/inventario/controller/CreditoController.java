package com.inventario.controller;

import com.inventario.dto.CreditoDto;
import com.inventario.exception.HttpException;
import com.inventario.models.Credito;
import com.inventario.services.CreditoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/creditos")
public class CreditoController {

    private final CreditoService creditoService;

    public CreditoController(CreditoService creditoService) {
        this.creditoService = creditoService;
    }

    @GetMapping
    public List<CreditoDto.Get> list() {
        return creditoService.listAll().stream().map(CreditoDto.Get::new).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CreditoDto.Get> get(@PathVariable Integer id) {
        return creditoService.getById(id).map(CreditoDto.Get::new).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody CreditoDto.Post dto) {
        try {
            Credito saved = creditoService.create(dto);
            return ResponseEntity.ok(new CreditoDto.Get(saved));
        } catch (HttpException e) {
            return ResponseEntity.status(e.getCode() == null ? 400 : e.getCode()).body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id, @Valid @RequestBody CreditoDto.Post dto) {
        try {
            Credito saved = creditoService.update(id, dto);
            return ResponseEntity.ok(new CreditoDto.Get(saved));
        } catch (HttpException e) {
            return ResponseEntity.status(e.getCode() == null ? 400 : e.getCode()).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        try {
            creditoService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (HttpException e) {
            return ResponseEntity.status(e.getCode() == null ? 400 : e.getCode()).body(e.getMessage());
        }
    }

}
