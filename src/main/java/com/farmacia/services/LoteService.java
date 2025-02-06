package com.farmacia.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.farmacia.dto.LoteDto;
import com.farmacia.exception.HttpException;
import com.farmacia.models.Lote;
import com.farmacia.models.Producto;
import com.farmacia.repository.LoteRepository;

@Service
public class LoteService {

    @Autowired
    private LoteRepository loteRepository;

    private Lote crearLote(LoteDto.POST loteDto, Producto producto) {

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

    public Lote agregarNuevoLote(LoteDto.POST loteDto, Producto producto) throws HttpException {
        Lote lote = this.obtenerLote(producto.getId().intValue(), loteDto.getLote());
        if (lote == null) {
            return this.crearLote(loteDto, producto);
        } else {
            if (loteDto.getFechaVencimiento() != lote.getFechaVencimiento()) {
                throw new HttpException("El lote a agregar ya existe con una fecha de vencimiento diferente", 400);
            }
            lote.setCantidad(lote.getCantidad() + loteDto.getCantidad());
            loteRepository.save(lote);
            return lote;
        }
    }

}
