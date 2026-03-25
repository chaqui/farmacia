package com.inventario.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.inventario.exception.HttpException;
import com.inventario.models.Lote;
import com.inventario.models.Sucursal;
import com.inventario.models.SucursalLote;
import com.inventario.repository.SucursalLoteRepository;

import jakarta.transaction.Transactional;

@Service
public class SucursalLoteService {

    private final SucursalLoteRepository sucursalLoteRepository;

    @Autowired
    public SucursalLoteService(SucursalLoteRepository sucursalLoteRepository) {
        this.sucursalLoteRepository = sucursalLoteRepository;
    }

    @Transactional(rollbackOn = Exception.class)
    public void addLoteToSucursal(Sucursal sucursal, Lote lote, Long cantidad) {
        SucursalLote sucursalLote = this.sucursalLoteRepository.findBySucursalIdAndLoteId(sucursal.getId(), lote.getLote());
        if (sucursalLote == null) {
            sucursalLote = new SucursalLote(cantidad, lote, sucursal);
        } else {
            sucursalLote.setCantidad(sucursalLote.getCantidad() + cantidad);

        }
        this.sucursalLoteRepository.save(sucursalLote);
    }

    public SucursalLote obtenerLoteEnSucursal(Long sucursalId, String loteId) throws HttpException {
        SucursalLote sucursalLote = this.sucursalLoteRepository.findBySucursalIdAndLoteId(sucursalId, loteId);
        if (sucursalLote != null) {
            return sucursalLote;
        }
        throw new HttpException("Lote no encontrado en la sucursal", 404);
    }

    public SucursalLote reducirCantidadLoteEnSucursal(Long sucursalId, String loteId, Long cantidad) throws HttpException {
        SucursalLote sucursalLote = this.obtenerLoteEnSucursal(sucursalId, loteId);
        if (sucursalLote.getCantidad() < cantidad) {
            throw new HttpException("No hay suficiente cantidad del lote en la sucursal", 400);
        }
        sucursalLote.setCantidad(sucursalLote.getCantidad() - cantidad);
        return this.sucursalLoteRepository.save(sucursalLote);
    }

}
