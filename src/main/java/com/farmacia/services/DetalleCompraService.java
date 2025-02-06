package com.farmacia.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.farmacia.models.Compra;
import com.farmacia.models.DetalleCompra;
import com.farmacia.models.Lote;
import com.farmacia.repository.DetalleCompraRepository;

@Service
public class DetalleCompraService {

    @Autowired
    private DetalleCompraRepository detalleCompraRepository;

    public void crearDetalleCompra(Compra compra, Lote lote, Integer cantidad) {
        DetalleCompra detalleCompra = new DetalleCompra(compra, lote, cantidad);
        detalleCompraRepository.save(detalleCompra);

    }

}
