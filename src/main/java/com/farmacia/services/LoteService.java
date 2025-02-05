package com.farmacia.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.farmacia.dto.LoteDto;
import com.farmacia.models.Lote;
import com.farmacia.models.Producto;
import com.farmacia.repository.LoteRepository;

@Service
public class LoteService {

    @Autowired
    private LoteRepository loteRepository;

    public Lote crearLote(LoteDto.POST loteDto, Producto producto) {

        return loteRepository.save(new Lote(loteDto, producto));
    }

    public List<Lote> obtenerLotes(Long idProducto) {
        return loteRepository.findByProductoId(idProducto);
    }

    public List<Lote> obtenerLotes() {
        return loteRepository.findAll();
    }

    public Lote obtenerLote(Integer idProducto, String lote) {
        return loteRepository.findByProductoIdAndLote(idProducto, lote);
    }

    public void agregarNuevoLote(LoteDto.POST loteDto, Producto producto) {
        Lote lote = this.obtenerLote(producto.getId().intValue(), loteDto.getLote());
        if (lote == null) {
            this.crearLote(loteDto, producto);
        } else {
            lote.setCantidad(lote.getCantidad() + loteDto.getCantidad());
            loteRepository.save(lote);
        }
    }

}
