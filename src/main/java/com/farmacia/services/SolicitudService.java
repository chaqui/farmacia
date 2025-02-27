package com.farmacia.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.farmacia.constants.EstadoSolicitud;
import com.farmacia.dto.LoteDto;
import com.farmacia.dto.SolicitudDetalleDto;
import com.farmacia.dto.SolicitudDto;
import com.farmacia.exception.HttpException;
import com.farmacia.models.Lote;
import com.farmacia.models.Solicitud;
import com.farmacia.models.Sucursal;
import com.farmacia.repository.SolicitudRepository;

import jakarta.transaction.Transactional;

@Service
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
