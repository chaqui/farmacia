package com.inventario.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.inventario.dto.VentaDetalleDto;
import com.inventario.dto.VentaDto;
import com.inventario.exception.HttpException;
import com.inventario.models.Venta;
import com.inventario.services.VentaService;
import com.inventario.services.VentaStateService;

import com.inventario.constants.EstadoVenta;

import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/ventas")
@Log4j2
public class VentaController {

    private final VentaService ventaService;
    private final VentaStateService ventaStateService;

    public VentaController(VentaService ventaService, VentaStateService ventaStateService) {
        this.ventaService = ventaService;
        this.ventaStateService = ventaStateService;
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

    /**
     * Cambiar venta a estado VERIFICADA
     * 
     * Requiere:
     * - ventaId: ID de la venta
     * - esCredito: true si es venta a crédito, false si es al contado
     * 
     * Validaciones:
     * - La venta debe estar en estado CREADA
     * - Debe tener detalles (productos)
     * - Si es crédito: valida que el cliente tenga límite disponible
     * 
     * Comportamiento especial:
     * - Si esCredito=false (venta al contado):
     * → La venta se AUTORIZA automáticamente
     * → Se salta el estado VERIFICADA
     * - Si esCredito=true (venta a crédito):
     * → Solo cambia a VERIFICADA
     * → Requiere autorización manual posterior
     * 
     * Ejemplos:
     * POST /ventas/estados/1/verificar?esCredito=true (venta a crédito)
     * POST /ventas/estados/1/verificar?esCredito=false (venta al contado - autoriza
     * automáticamente)
     */
    @PostMapping("/{ventaId}/verificar")
    @ResponseStatus(HttpStatus.OK)
    public VentaDto.GetSinSucursal verificarVenta(
            @PathVariable Integer ventaId,
            @RequestBody VentaDto.Verificar dtoVerificar) throws HttpException {

        Venta ventaActualizada = this.ventaStateService.verificarVenta(ventaId, dtoVerificar);
        return new VentaDto.GetSinSucursal(ventaActualizada);
    }

    /**
     * Cambiar venta a estado AUTORIZADA
     * Requiere:
     * - ventaId: ID de la venta
     * 
     * Validaciones:
     * - La venta debe estar en estado VERIFICADA
     * - Si es crédito, crea el registro de crédito automáticamente
     * 
     * Efecto:
     * - Cambiar estado a AUTORIZADA
     * - Si es crédito, genera automáticamente el registro de crédito asociado
     * - Los productos quedan descontados (ya se descuentan en estado CREADA)
     */
    @PostMapping("/{ventaId}/autorizar")
    @ResponseStatus(HttpStatus.OK)
    public VentaDto.GetSinSucursal autorizarVenta(
            @PathVariable Integer ventaId) throws HttpException {

        Venta ventaActualizada = this.ventaStateService.autorizarVenta(ventaId);
        return new VentaDto.GetSinSucursal(ventaActualizada);
    }

    /**
     * Cancelar una venta y liberar los productos
     * Requiere:
     * - ventaId: ID de la venta
     * 
     * Validaciones:
     * - La venta debe estar en estado CREADA o VERIFICADA
     * - NO se puede cancelar una venta AUTORIZADA
     * 
     * Efecto:
     * - Cambiar estado a CANCELADA
     * - Se eliminan los DetalleVenta (liberan los productos bloqueados)
     * - Los productos vuelven al inventario disponible
     */
    @PostMapping("/{ventaId}/cancelar")
    @ResponseStatus(HttpStatus.OK)
    public VentaDto.GetSinSucursal cancelarVenta(
            @PathVariable Integer ventaId) throws HttpException {

        Venta ventaActualizada = this.ventaStateService.cancelarVenta(ventaId);
        return new VentaDto.GetSinSucursal(ventaActualizada);
    }

    /**
     * Obtener estado actual de una venta
     */
    @GetMapping("/{ventaId}")
    @ResponseStatus(HttpStatus.OK)
    public EstadoVenta obtenerEstado(
            @PathVariable Integer ventaId) throws HttpException {

        return this.ventaStateService.obtenerEstadoVenta(ventaId);
    }

    /**
     * Obtener todas las ventas de un estado específico
     * 
     * Parámetro:
     * - estado: CREADA, VERIFICADA, AUTORIZADA o CANCELADA
     * 
     * Ejemplo:
     * GET /ventas/estados/por-estado?estado=CREADA
     */
    @GetMapping("/por-estado")
    @ResponseStatus(HttpStatus.OK)
    public List<VentaDto.GetSinSucursal> obtenerVentasPorEstado(
            @RequestParam EstadoVenta estado) {

        List<Venta> ventas = this.ventaStateService.obtenerVentasPorEstado(estado);
        return ventas.stream()
                .map(VentaDto.GetSinSucursal::new)
                .toList();
    }

    /**
     * Obtener resumen de ventas por estado
     * 
     * Retorna:
     * - Lista de todas las ventas con su estado actual
     * 
     * Ejemplo:
     * GET /ventas/estados/todas
     */
    @GetMapping("/todas")
    @ResponseStatus(HttpStatus.OK)
    public List<VentaDto.GetSinSucursal> obtenerTodasLasVentas() {
        List<Venta> ventas = this.ventaStateService.obtenerTodasLasVentas();
        return ventas.stream()
                .map(VentaDto.GetSinSucursal::new)
                .toList();
    }

    @GetMapping("/{id}/detalles")
    @ResponseStatus(HttpStatus.OK)
    public List<VentaDetalleDto.Get> obtenerDetallesVenta(@PathVariable Integer id) throws HttpException {
        return this.ventaService.obtenerDetallesVenta(id);
    }

}
