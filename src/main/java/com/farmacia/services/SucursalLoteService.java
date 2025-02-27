package com.farmacia.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.farmacia.models.Lote;
import com.farmacia.models.Sucursal;
import com.farmacia.models.SucursalLote;
import com.farmacia.repository.SucursalLoteRepository;

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
        SucursalLote sucursalLote = this.sucursalLoteRepository.findBySucursalIdAndLoteId(sucursal.id(), lote.getLote());
        if (sucursalLote == null) {
            sucursalLote = new SucursalLote(cantidad, lote, sucursal);
        } else {
            sucursalLote.setCantidad(sucursalLote.getCantidad() + lote.getCantidad());

        }
        this.sucursalLoteRepository.save(sucursalLote);
    }

}
