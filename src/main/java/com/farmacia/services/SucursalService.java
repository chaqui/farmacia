package com.farmacia.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.farmacia.dto.SucursalDto;
import com.farmacia.models.Solicitud;
import com.farmacia.models.Sucursal;
import com.farmacia.repository.SucursalRepository;

@Service
public class SucursalService {

    private final SucursalRepository sucursalRepository;

    @Autowired
    public SucursalService(SucursalRepository sucursalRepository) {
        this.sucursalRepository = sucursalRepository;
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
            sucursalRepository.save(new Sucursal( sucursalDto));
        }
    }

    public Iterable<SucursalDto.Get> obtenerSucursales() {
        return sucursalRepository.findAll().stream().map(SucursalDto.Get::new).toList();
    }

    public List<Solicitud> obtenerSolicitudes(Long id) {
        Sucursal sucursal = this.obtenerSucursal(id);
        return sucursal.getSolicitudes();
    }

}
