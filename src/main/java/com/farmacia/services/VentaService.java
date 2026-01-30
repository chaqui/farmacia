package com.farmacia.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.farmacia.dto.VentaDetalleDto;
import com.farmacia.dto.VentaDto;
import com.farmacia.exception.HttpException;
import com.farmacia.models.DetalleVenta;
import com.farmacia.models.Lote;
import com.farmacia.models.Sucursal;
import com.farmacia.models.SucursalLote;
import com.farmacia.models.Venta;
import com.farmacia.repository.VentaRepository;

import jakarta.transaction.Transactional;

@Service
public class VentaService {

    private final VentaRepository ventaRepository;

    private final SucursalLoteService sucursalLoteService;

    private final LoteService loteService;

    public VentaService(VentaRepository ventaRepository,
            SucursalLoteService sucursalLoteService, LoteService loteService) {
        this.ventaRepository = ventaRepository;
        this.sucursalLoteService = sucursalLoteService;
        this.loteService = loteService;
    }

    @Transactional(rollbackOn = Exception.class)
    public void crearVenta(VentaDto.Post ventaDto) throws HttpException {
        Venta venta = new Venta(ventaDto);
        this.ventaRepository.save(venta);
        this.agregarSubdetallesSinSucursal(venta, ventaDto.getDetalles());
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
        Venta venta = new Venta(ventaDto, sucursal);
        this.ventaRepository.save(venta);
        this.agregarSubdetallesConSucursal(venta, ventaDto.getDetalles());
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

}
