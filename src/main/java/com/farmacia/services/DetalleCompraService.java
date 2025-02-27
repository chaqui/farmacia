package com.farmacia.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.farmacia.models.Compra;
import com.farmacia.models.DetalleCompra;
import com.farmacia.models.Lote;
import com.farmacia.repository.DetalleCompraRepository;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DetalleCompraService {

    @Autowired
    private DetalleCompraRepository detalleCompraRepository;

    @Transactional(rollbackOn = Exception.class)
    public void crearDetalleCompra(Compra compra, Lote lote, Long cantidad) {
        DetalleCompra detalleCompra = new DetalleCompra(compra, lote, cantidad);
    
        detalleCompraRepository.save(detalleCompra);

    }

}
