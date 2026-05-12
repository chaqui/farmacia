package com.inventario.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.inventario.dto.DevolucionVentaDto;
import com.inventario.dto.VentaDetalleDto;
import com.inventario.dto.VentaDto;
import com.inventario.exception.HttpException;
import com.inventario.models.DevolucionVenta;
import com.inventario.models.Venta;
import com.inventario.services.DevolucionVentaService;
import com.inventario.services.VentaService;
import com.inventario.services.VentaStateService;
import com.inventario.services.TicketPdfService;

import com.inventario.constants.EstadoVenta;

import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import com.inventario.security.ValidateToken;
import com.inventario.security.SystemRoles;

import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/ventas")
@Log4j2
public class VentaController {

    private final VentaService ventaService;
    private final VentaStateService ventaStateService;
    private final TicketPdfService ticketPdfService;
    private final DevolucionVentaService devolucionVentaService;

    public VentaController(VentaService ventaService, VentaStateService ventaStateService, TicketPdfService ticketPdfService, DevolucionVentaService devolucionVentaService) {
        this.ventaService = ventaService;
        this.ventaStateService = ventaStateService;
        this.ticketPdfService = ticketPdfService;
        this.devolucionVentaService = devolucionVentaService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ValidateToken(roles = {SystemRoles.VENDEDOR})
    public void crearVenta(@Valid @RequestBody VentaDto.Post ventaDto) throws HttpException {
        this.ventaService.crearVenta(ventaDto);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @ValidateToken(roles = {SystemRoles.VENDEDOR, SystemRoles.CAJA, SystemRoles.ADMINISTRADOR})
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
    @ValidateToken(roles = {SystemRoles.VENDEDOR, SystemRoles.CAJA})
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
    @ValidateToken(roles = {SystemRoles.CAJA, SystemRoles.ADMINISTRADOR})
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
    @DeleteMapping("/{ventaId}/cancelar")
    @ResponseStatus(HttpStatus.OK)
    @ValidateToken(roles = {SystemRoles.VENDEDOR, SystemRoles.ADMINISTRADOR})
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
    @ValidateToken(roles = {SystemRoles.VENDEDOR, SystemRoles.CAJA, SystemRoles.ADMINISTRADOR})
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
    @ValidateToken(roles = {SystemRoles.VENDEDOR, SystemRoles.CAJA, SystemRoles.ADMINISTRADOR})
    public List<VentaDetalleDto.Get> obtenerDetallesVenta(@PathVariable Integer id) throws HttpException {
        return this.ventaService.obtenerDetallesVenta(id);
    }

    /**
     * Descargar ticket de venta en formato PDF
     * Formato optimizado para impresora térmica (80mm)
     * 
     * Parámetro:
     * - ventaId: ID de la venta
     * 
     * Retorna:
     * - PDF con el ticket de venta
     * 
     * Ejemplo:
     * GET /ventas/1/descargar-ticket
     */
    @GetMapping("/{ventaId}/descargar-ticket")
    public ResponseEntity<byte[]> descargarTicket(@PathVariable Integer ventaId) throws HttpException {
        byte[] pdfBytes = ticketPdfService.generarTicketVenta(ventaId);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "ticket-venta-" + ventaId + ".pdf");
        headers.setContentLength(pdfBytes.length);
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }

    /**
     * Crear una devolución para una venta específica
     * 
     * Parámetro:
     * - ventaId: ID de la venta
     * 
     * Body:
     * - fecha: fecha de la devolución
     * - motivo: motivo de la devolución (opcional)
     * - detalles: lista de detalles a devolver
     *   - detalleVentaId: ID del detalle de venta
     *   - cantidad: cantidad a devolver
     *   - razonDevolucion: razón específica del detalle (opcional)
     * 
     * Ejemplo:
     * POST /ventas/1/devolucion
     */
    @PostMapping("/{ventaId}/devolucion")
    @ResponseStatus(HttpStatus.CREATED)
    @ValidateToken(roles = {SystemRoles.VENDEDOR, SystemRoles.CAJA, SystemRoles.ADMINISTRADOR})
    public DevolucionVentaDto.Get crearDevolucion(
            @PathVariable Integer ventaId,
            @Valid @RequestBody DevolucionVentaDto.Post devolucionDto) throws HttpException {
        // Asegurarse de que el ID de venta en el DTO coincida con el path
        devolucionDto.setVentaId(ventaId);
        DevolucionVenta devolucion = devolucionVentaService.crearDevolucion(devolucionDto);
        return new DevolucionVentaDto.Get(devolucion);
    }

    /**
     * Obtener todas las devoluciones de una venta específica
     * 
     * Parámetro:
     * - ventaId: ID de la venta
     * 
     * Ejemplo:
     * GET /ventas/1/devoluciones
     */
    @GetMapping("/{ventaId}/devoluciones")
    @ResponseStatus(HttpStatus.OK)
    public List<DevolucionVentaDto.Get> obtenerDevolucionesPorVenta(@PathVariable Integer ventaId)
            throws HttpException {
        return devolucionVentaService.obtenerDevolucionesPorVenta(ventaId);
    }

}
