package com.inventario.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.inventario.dto.VentaDetalleDto;
import com.inventario.dto.VentaDto;
import com.inventario.exception.HttpException;
import com.inventario.models.DetalleVenta;
import com.inventario.models.Lote;
import com.inventario.models.Sucursal;
import com.inventario.models.SucursalLote;
import com.inventario.models.Venta;
import com.inventario.models.Cliente;
import com.inventario.repository.VentaRepository;

import jakarta.transaction.Transactional;

@Service
public class VentaService {

    protected final VentaRepository ventaRepository;

    private final SucursalLoteService sucursalLoteService;

    private final ClienteService clienteService;

    private final LoteService loteService;

    private final NotificacionService notificacionService;

    public VentaService(VentaRepository ventaRepository,
            SucursalLoteService sucursalLoteService, LoteService loteService, ClienteService clienteService,
            NotificacionService notificacionService) {
        this.ventaRepository = ventaRepository;
        this.sucursalLoteService = sucursalLoteService;
        this.loteService = loteService;
        this.clienteService = clienteService;
        this.notificacionService = notificacionService;
    }

    @Transactional(rollbackOn = Exception.class)
    public void crearVenta(VentaDto.Post ventaDto) throws HttpException {
        Cliente cliente = this.clienteService.obtenerClientePorId(ventaDto.getClienteId());

        Venta venta = new Venta(ventaDto, cliente);
        this.ventaRepository.save(venta);
        this.agregarSubdetallesSinSucursal(venta, ventaDto.getDetalles());

        // Notificar creación de venta
        notificacionService.notificarVerificacionVenta(
                venta.getId(),
                cliente.getId(),
                venta.getTotal().doubleValue(),
                cliente.getNombre()
        );
    }

    private void agregarSubdetallesSinSucursal(Venta venta, List<VentaDetalleDto.Post> detalles) throws HttpException {
        for (VentaDetalleDto.Post detalleDto : detalles) {
            Lote lote = this.loteService.reduceCantidadLote(detalleDto.getLote(), detalleDto.getCantidad());
            DetalleVenta detalleVenta = new DetalleVenta(detalleDto, lote);
            venta.addDetalle(detalleVenta);
        }
        this.ventaRepository.save(venta);
    }

    public List<VentaDto.GetSinSucursal> obtenerVentas() {
        List<Venta> ventas = this.ventaRepository.findAll();
        return ventas.stream().map(VentaDto.GetSinSucursal::new).toList();
    }

    @Transactional(rollbackOn = Exception.class)
    public void crearVenta(VentaDto.Post ventaDto, Sucursal sucursal) throws HttpException {
        Cliente cliente = this.clienteService.obtenerClientePorId(ventaDto.getClienteId());
        Venta venta = new Venta(ventaDto, sucursal, cliente);
        this.ventaRepository.save(venta);
        this.agregarSubdetallesConSucursal(venta, ventaDto.getDetalles());

        // Notificar creación de venta
        notificacionService.notificarVerificacionVenta(
                venta.getId(),
                cliente.getId(),
                venta.getTotal().doubleValue(),
                cliente.getNombre()
        );
    }

    private void agregarSubdetallesConSucursal(Venta venta, List<VentaDetalleDto.Post> detalles) throws HttpException {
        for (VentaDetalleDto.Post detalleDto : detalles) {
            SucursalLote lote = this.sucursalLoteService.reducirCantidadLoteEnSucursal(venta.getSucursal().getId(),
                    detalleDto.getLote(), detalleDto.getCantidad());
            DetalleVenta detalleVenta = new DetalleVenta(detalleDto, lote.getLote());
            venta.addDetalle(detalleVenta);

        }
        this.ventaRepository.save(venta);

    }

    protected Venta obtenerVentaPorId(Integer ventaId) throws HttpException {
        return this.ventaRepository.findById(ventaId)
                .orElseThrow(() -> new HttpException("Venta no encontrada", 404));
    }

    public List<VentaDetalleDto.Get> obtenerDetallesVenta(Integer ventaId) throws HttpException {
        Venta venta = this.obtenerVentaPorId(ventaId);
        if (venta.getDetalleVentas() == null || venta.getDetalleVentas().isEmpty()) {
            throw new HttpException("La venta no tiene detalles", 404);
        }
        return venta.getDetalleVentas().stream()
                .map(VentaDetalleDto.Get::new)
                .toList();
    }

}
