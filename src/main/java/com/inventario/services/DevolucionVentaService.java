package com.inventario.services;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.inventario.dto.DevolucionVentaDto;
import com.inventario.exception.HttpException;
import com.inventario.models.DetalleDevolucionVenta;
import com.inventario.models.DetalleVenta;
import com.inventario.models.DevolucionVenta;
import com.inventario.models.Venta;
import com.inventario.dto.DetalleDevolucionDto;
import com.inventario.repository.DevolucionVentaRepository;
import com.inventario.repository.DetalleVentaRepository;
import com.inventario.repository.VentaRepository;

import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class DevolucionVentaService {

    private final DevolucionVentaRepository devolucionVentaRepository;
    private final VentaRepository ventaRepository;
    private final DetalleVentaRepository detalleVentaRepository;
    private final NotificacionService notificacionService;

    public DevolucionVentaService(DevolucionVentaRepository devolucionVentaRepository,
            VentaRepository ventaRepository, DetalleVentaRepository detalleVentaRepository,
            NotificacionService notificacionService) {
        this.devolucionVentaRepository = devolucionVentaRepository;
        this.ventaRepository = ventaRepository;
        this.detalleVentaRepository = detalleVentaRepository;
        this.notificacionService = notificacionService;
    }

    @Transactional(rollbackOn = Exception.class)
    public DevolucionVenta crearDevolucion(DevolucionVentaDto.Post devolucionDto) throws HttpException {
        // Validar que la venta existe
        Venta venta = ventaRepository.findById(devolucionDto.getVentaId())
                .orElseThrow(() -> new HttpException( "Venta no encontrada", 404));

        // Crear la devolución
        DevolucionVenta devolucion = new DevolucionVenta();
        devolucion.setCodigoDevolucion(generarCodigoDevolucion());
        devolucion.setFecha(devolucionDto.getFecha());
        devolucion.setMotivo(devolucionDto.getMotivo());
        devolucion.setVenta(venta);

        // Agregar detalles
        for (DetalleDevolucionDto.Post detalleDto : devolucionDto.getDetalles()) {
            DetalleVenta detalleVentaOriginal = detalleVentaRepository
                    .findById(detalleDto.getDetalleVentaId())
                    .orElseThrow(() -> new HttpException("Detalle de venta no encontrado", 404));

            // Validar que el detalle pertenece a la venta
            if (!detalleVentaOriginal.getVenta().getId().equals(devolucionDto.getVentaId())) {
                throw new HttpException("El detalle de venta no pertenece a la venta especificada", 400);
            }

            // Validar cantidad
            if (detalleDto.getCantidad() > detalleVentaOriginal.getCantidad()) {
                throw new HttpException("La cantidad a devolver no puede ser mayor a la cantidad vendida", 400);
            }

            DetalleDevolucionVenta detalleDevolucion = new DetalleDevolucionVenta();
            detalleDevolucion.setCantidad(detalleDto.getCantidad());
            detalleDevolucion.setDetalleVentaOriginal(detalleVentaOriginal);
            detalleDevolucion.setRazonDevolucion(detalleDto.getRazonDevolucion());
            devolucion.addDetalle(detalleDevolucion);
        }

        // Procesar automáticamente: marcar como procesada
        devolucion.setProcesada(true);

        DevolucionVenta devolucionGuardada = devolucionVentaRepository.save(devolucion);

        log.info("Devolución creada y procesada: " + devolucion.getCodigoDevolucion() + " para venta: "
                + venta.getCodigoVenta());

        // Notificar creación y procesamiento de devolución
        notificacionService.notificarDevolucion(devolucionGuardada.getId(), venta.getId());

        return devolucionGuardada;
    }

    public DevolucionVenta obtenerDevolucionPorId(Integer id) throws HttpException {
        return devolucionVentaRepository.findById(id)
                .orElseThrow(() -> new HttpException("Devolución no encontrada", 404));
    }

    public List<DevolucionVentaDto.Get> obtenerDevoluciones() {
        return devolucionVentaRepository.findAll().stream()
                .map(DevolucionVentaDto.Get::new)
                .collect(Collectors.toList());
    }

    public List<DevolucionVentaDto.Get> obtenerDevolucionesPorVenta(Integer ventaId) throws HttpException {
        Venta venta = ventaRepository.findById(ventaId)
                .orElseThrow(() -> new HttpException("Venta no encontrada", 404));

        return devolucionVentaRepository.findByVentaId(ventaId).stream()
                .map(DevolucionVentaDto.Get::new)
                .collect(Collectors.toList());
    }



    private String generarCodigoDevolucion() {
        // Generar un código único: DEV-FECHA-UUID
        LocalDate hoy = LocalDate.now();
        String fecha = hoy.getYear() + String.format("%02d", hoy.getMonthValue())
                + String.format("%02d", hoy.getDayOfMonth());
        String uuid = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return "DEV-" + fecha + "-" + uuid;
    }

    @Transactional(rollbackOn = Exception.class)
    public void cancelarDevolucion(Integer devolucionId) throws HttpException {
        DevolucionVenta devolucion = obtenerDevolucionPorId(devolucionId);

        devolucionVentaRepository.delete(devolucion);
        log.info("Devolución cancelada: " + devolucion.getCodigoDevolucion());
    }
}
