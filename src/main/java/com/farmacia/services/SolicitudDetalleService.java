package com.farmacia.services;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.farmacia.dto.LoteDto.ENVIAR;
import com.farmacia.dto.SolicitudDetalleDto;
import com.farmacia.exception.HttpException;
import com.farmacia.models.DetalleSolicitud;
import com.farmacia.models.Producto;
import com.farmacia.models.Solicitud;
import com.farmacia.repository.SolicitudDetalleRepository;

import jakarta.transaction.Transactional;

@Service
public class SolicitudDetalleService {

    private final SolicitudDetalleRepository solicitudDetalleRepository;

    private final ProductoService productoService;

    @Autowired
    public SolicitudDetalleService(SolicitudDetalleRepository solicitudDetalleRepository,
            ProductoService productoService) {
        this.solicitudDetalleRepository = solicitudDetalleRepository;
        this.productoService = productoService;
    }

    @Transactional(rollbackOn = Exception.class)
    public void addDetalleToSolicitud(Solicitud solicitud, SolicitudDetalleDto.Post detalleDto) throws HttpException {
        Producto producto = productoService.obtenerProducto(detalleDto.getIdProducto());
        if (producto.getCantidad() < detalleDto.getCantidad()) {
            throw new HttpException("No hay suficiente cantidad de producto", 400);
        }
        DetalleSolicitud detalleSolicitud = new DetalleSolicitud(solicitud, producto, detalleDto.getCantidad());
        solicitudDetalleRepository.save(detalleSolicitud);
    }

    @Transactional(rollbackOn = Exception.class)
    public void enviarDetalleSolicitud(Long idProducto, Long idSolicitud, Long cantidad) throws HttpException {
        DetalleSolicitud detalleSolicitud = solicitudDetalleRepository.findBySolicitudIdAndProductoId(idSolicitud,
                idProducto);
        if (detalleSolicitud == null) {
            throw new HttpException("Detalle de solicitud no encontrado", 404);
        }
        if (detalleSolicitud.getCantidad() < cantidad) {
            throw new HttpException("La cantidad de producto a enviar es mayor a la solicitada", 400);
        }
        detalleSolicitud.setCantidadEnvida(cantidad);
        solicitudDetalleRepository.save(detalleSolicitud);
    }

    public Collection<DetalleSolicitud> obtenerDetalles(Long id) {
        return solicitudDetalleRepository.findBySolicitudId(id);
    }



}
