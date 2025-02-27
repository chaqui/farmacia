package com.farmacia.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.farmacia.dto.LoteDto;
import com.farmacia.exception.HttpException;
import com.farmacia.models.Lote;
import com.farmacia.models.Producto;
import com.farmacia.models.Sucursal;
import com.farmacia.repository.LoteRepository;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class LoteService {

    private final LoteRepository loteRepository;
    private final SucursalLoteService sucursalLoteService;

    @Autowired
    public LoteService(LoteRepository loteRepository, SucursalLoteService sucursalLoteService) {
        this.loteRepository = loteRepository;
        this.sucursalLoteService = sucursalLoteService;
    }

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

    public Lote obtenerLote(String lote) throws HttpException {
        return loteRepository.findById(lote).orElseThrow(() -> new HttpException("Lote no encontrado", 404));
    }

    @Transactional(rollbackOn = Exception.class)
    public Lote agregarNuevoLote(LoteDto.POST loteDto, Producto producto) throws HttpException {
        Lote lote = this.obtenerLote(producto.getId().intValue(), loteDto.getLote());
        log.info("Lote: {}", lote);
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

    @Transactional(rollbackOn = Exception.class)
    public Lote enviarLote(LoteDto.ENVIAR loteEnviar, Sucursal sucursal) throws HttpException {
        Lote loteEncontrado = this.obtenerLote(loteEnviar.getLote());
        if (loteEncontrado.getCantidad() < loteEnviar.getCantidad()) {
            throw new HttpException("No hay suficiente cantidad en el lote", 400);
        }
        this.sucursalLoteService.addLoteToSucursal(sucursal, loteEncontrado, loteEnviar.getCantidad());

        loteEncontrado.setCantidad(loteEncontrado.getCantidad() - loteEnviar.getCantidad());
        loteRepository.save(loteEncontrado);
        return loteEncontrado;
    }

}
