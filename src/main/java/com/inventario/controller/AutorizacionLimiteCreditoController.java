package com.inventario.controller;

import com.inventario.dto.AutorizacionLimiteCreditoDto;
import com.inventario.exception.HttpException;
import com.inventario.models.AutorizacionLimiteCredito;
import com.inventario.services.AutorizacionLimiteCreditoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/autorizaciones-limite-credito")
public class AutorizacionLimiteCreditoController {

    private final AutorizacionLimiteCreditoService autorizacionService;

    public AutorizacionLimiteCreditoController(AutorizacionLimiteCreditoService autorizacionService) {
        this.autorizacionService = autorizacionService;
    }

    /**
     * Obtener todas las solicitudes pendientes
     */
    @GetMapping("/pendientes")
    public List<AutorizacionLimiteCreditoDto.Get> obtenerPendientes() {
        return autorizacionService.obtenerSolicitudesPendientes()
                .stream()
                .map(AutorizacionLimiteCreditoDto.Get::new)
                .collect(Collectors.toList());
    }



    /**
     * Obtener autorización por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<AutorizacionLimiteCreditoDto.Get> obtenerPorId(@PathVariable Integer id) throws HttpException {
        AutorizacionLimiteCredito autorizacion = autorizacionService.obtenerPorId(id);
        return ResponseEntity.ok(new AutorizacionLimiteCreditoDto.Get(autorizacion));
    }

    /**
     * Aprobar solicitud de autorización
     */
    @PostMapping("/{id}/aprobar")
    public ResponseEntity<AutorizacionLimiteCreditoDto.Get> aprobar(
            @PathVariable Integer id) throws HttpException {
        
        AutorizacionLimiteCredito aprobada = autorizacionService.aprobarAutorizacion(id);
        
        return ResponseEntity.ok(new AutorizacionLimiteCreditoDto.Get(aprobada));
    }

    /**
     * Rechazar solicitud de autorización
     */
    @PostMapping("/{id}/rechazar")
    public ResponseEntity<AutorizacionLimiteCreditoDto.Get> rechazar(
            @PathVariable Integer id,
            @Valid @RequestBody AutorizacionLimiteCreditoDto.RechazoRequest request) throws HttpException {
        
        AutorizacionLimiteCredito rechazada = autorizacionService.rechazarAutorizacion(
                id, request.getRazonRechazo());
        
        return ResponseEntity.ok(new AutorizacionLimiteCreditoDto.Get(rechazada));
    }

}
