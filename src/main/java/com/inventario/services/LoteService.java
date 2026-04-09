package com.inventario.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.inventario.dto.LoteDto;
import com.inventario.exception.HttpException;
import com.inventario.models.Lote;
import com.inventario.models.Producto;
import com.inventario.models.Sucursal;
import com.inventario.repository.LoteRepository;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class LoteService {

    private final LoteRepository loteRepository;
    private final SucursalLoteService sucursalLoteService;
    private final com.inventario.repository.ProductoRepository productoRepository;

    public LoteService(LoteRepository loteRepository, SucursalLoteService sucursalLoteService,
                       com.inventario.repository.ProductoRepository productoRepository) {
        this.loteRepository = loteRepository;
        this.sucursalLoteService = sucursalLoteService;
        this.productoRepository = productoRepository;
    }

    private Lote crearLote(LoteDto.POST loteDto, Producto producto) {
        // If ubicacion was provided in the lote DTO, set it on the producto
        if (loteDto != null && loteDto.getEstanteria() != null) {
            producto.setEstanteria(loteDto.getEstanteria());
            producto.setNivel(loteDto.getNivel());
            productoRepository.save(producto);
        }
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

        loteRepository.save(loteEncontrado);
        return loteEncontrado;
    }

    public Lote reduceCantidadLote(String loteId, Long cantidad) throws HttpException {
        Lote lote = this.obtenerLote(loteId);
        if (lote.getCantidad() < cantidad) {
            throw new HttpException("No hay suficiente cantidad en el lote", 400);
        }
        return loteRepository.save(lote);
    }

    @Transactional
    public void actualizarUbicacionLote(String loteId, LoteDto.UbicacionDto ubicacion) throws HttpException {
        Lote lote = this.obtenerLote(loteId);
        if (lote.getProducto() != null) {
            var producto = lote.getProducto();
            producto.setEstanteria(ubicacion.getEstanteria());
            producto.setNivel(ubicacion.getNivel());
            productoRepository.save(producto);
        }
    }

}
