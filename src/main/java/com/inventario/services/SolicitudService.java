package com.inventario.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.inventario.constants.EstadoSolicitud;
import com.inventario.dto.LoteDto;
import com.inventario.dto.SolicitudDetalleDto;
import com.inventario.dto.SolicitudDto;
import com.inventario.exception.HttpException;
import com.inventario.models.Lote;
import com.inventario.models.Solicitud;
import com.inventario.models.Sucursal;
import com.inventario.repository.SolicitudRepository;

import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class SolicitudService {

    private final SolicitudRepository solicitudRepository;
    private final SucursalService sucursalService;
    private final LoteService loteService;
    private final SolicitudDetalleService solicitudDetalleService;

    @Autowired
    public SolicitudService(SolicitudRepository solicitudRepository, SucursalService sucursalService,
            LoteService loteService, SolicitudDetalleService solicitudDetalleService) {
        this.solicitudRepository = solicitudRepository;
        this.sucursalService = sucursalService;
        this.loteService = loteService;
        this.solicitudDetalleService = solicitudDetalleService;
    }

    public void eliminarSolicitud(Long id) {
        solicitudRepository.deleteById(id);
    }

    @Transactional(rollbackOn = Exception.class)
    public void crearSolicitud(SolicitudDto.POST solicitudDto) throws HttpException {

        Sucursal sucursal = sucursalService.obtenerSucursal(solicitudDto.getIdSucursal());
        Solicitud solicitud = new Solicitud(solicitudDto, sucursal);
        solicitudRepository.save(solicitud);
        for (SolicitudDetalleDto.Post detalle : solicitudDto.getDetalles()) {
            solicitudDetalleService.addDetalleToSolicitud(solicitud, detalle);
        }

    }

    public Solicitud obtenerSolicitud(Long id) throws HttpException {
        return solicitudRepository.findById(id).orElseThrow(() -> new HttpException("Solicitud no encontrada", 404));
    }

    public List<Solicitud> obtenerSolicitudes() {
        return solicitudRepository.findAll();
    }

    @Transactional(rollbackOn = Exception.class)
    public void enviarSolicitud(Long id, List<LoteDto.ENVIAR> lotes) throws HttpException {
        Solicitud solicitud = this.obtenerSolicitud(id);
        Sucursal sucursal = solicitud.getSucursal();
        for (LoteDto.ENVIAR loteDto : lotes) {
            Lote lote = this.loteService.enviarLote(loteDto, sucursal);
            this.solicitudDetalleService.enviarDetalleSolicitud(lote.getProducto().getId(), id, loteDto.getCantidad());
        }

        solicitud.setEstado(EstadoSolicitud.ENVIADA.getEstado());
        solicitudRepository.save(solicitud);

    }
}
