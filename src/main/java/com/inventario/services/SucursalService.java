package com.inventario.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.inventario.dto.SucursalDto;
import com.inventario.dto.VentaDto;
import com.inventario.exception.HttpException;
import com.inventario.models.Solicitud;
import com.inventario.models.Sucursal;
import com.inventario.repository.SucursalRepository;

import jakarta.transaction.Transactional;

@Service
public class SucursalService {

    private final SucursalRepository sucursalRepository;

    private final VentaService ventaService;

    public SucursalService(SucursalRepository sucursalRepository, VentaService ventaService) {
        this.sucursalRepository = sucursalRepository;
        this.ventaService = ventaService;
    }

    public void eliminarSucursal(Long id) {
        sucursalRepository.deleteById(id);
    }

    public void crearSucursal(SucursalDto.Post sucursalDto) {
        sucursalRepository.save(new Sucursal(sucursalDto));
    }

    public Sucursal obtenerSucursal(Long id) {
        return sucursalRepository.findById(id).orElse(null);
    }

    public void actualizarSucursal(Long id, SucursalDto.Post sucursalDto) {
        Sucursal sucursal = sucursalRepository.findById(id).orElse(null);
        if (sucursal != null) {
            sucursalRepository.save(new Sucursal(sucursalDto));
        }
    }

    public Iterable<SucursalDto.Get> obtenerSucursales() {
        return sucursalRepository.findAll().stream().map(SucursalDto.Get::new).toList();
    }

    public List<Solicitud> obtenerSolicitudes(Long id) {
        Sucursal sucursal = this.obtenerSucursal(id);
        return sucursal.getSolicitudes();
    }

    public List<VentaDto.GetConSucursal> obtenerVentas(Long id) {
        Sucursal sucursal = this.obtenerSucursal(id);
        return sucursal.getVentas().stream().map(VentaDto.GetConSucursal::new).toList();
    }

    @Transactional(rollbackOn = Exception.class)
    public void crearVenta(Long sucursalId, VentaDto.Post ventaDto) throws HttpException {
        Sucursal sucursal = this.obtenerSucursal(sucursalId);
        this.ventaService.crearVenta(ventaDto, sucursal);
    }

}
